package smtp_simulator_01;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

/**
 * Giao diện chính cho chat/email.
 * Cài đặt SMTPClient.MessageListener để nhận phản hồi/tin nhắn.
 */
public class MainApp extends JFrame implements SMTPClient.MessageListener {
    private final String username;
    private final SMTPClient client;

    private final JTextArea chatArea = new JTextArea();
    private final JTextField toField = new JTextField();
    private final JTextField subjectField = new JTextField("Chat");
    private final JTextArea bodyArea = new JTextArea(6, 20);
    private File attached = null;

    public MainApp(String username, SMTPClient client) {
        super("ChatMail - " + username);
        this.username = username;
        this.client = client;
        setSize(1000, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(new EmptyBorder(10,10,10,10));
        setContentPane(root);

        // thanh trên cùng
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Đang đăng nhập với: " + username), BorderLayout.WEST);
        JButton logout = new JButton("Đăng xuất");
        top.add(logout, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        logout.addActionListener(e -> {
            try { client.logout(username); client.close(); } catch (Exception ignored) {}
            dispose();
            SwingUtilities.invokeLater(() -> new LoginApp().setVisible(true));
        });

        // chia giao diện trung tâm
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);

        // bên trái: hội thoại
        JPanel left = new JPanel(new BorderLayout(6,6));
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); chatArea.setWrapStyleWord(true);
        left.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // khung soạn tin nhắn
        JPanel compose = new JPanel(new BorderLayout(6,6));
        JPanel fields = new JPanel(new GridLayout(1,3,6,6));
        fields.add(labeled("Người nhận", toField));
        fields.add(labeled("Tiêu đề", subjectField));
        JButton attachBtn = new JButton("Đính kèm");
        fields.add(attachBtn);
        compose.add(fields, BorderLayout.NORTH);
        compose.add(new JScrollPane(bodyArea), BorderLayout.CENTER);
        JPanel sendRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton emojiBtn = new JButton("😀");
        JButton sendBtn = new JButton("Gửi");
        sendRow.add(emojiBtn); sendRow.add(sendBtn);
        compose.add(sendRow, BorderLayout.SOUTH);
        left.add(compose, BorderLayout.SOUTH);

        // bên phải: hộp thư đến
        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Hộp thư đến (lưu trên máy chủ)"), BorderLayout.NORTH);
        JTextArea inboxArea = new JTextArea();
        inboxArea.setEditable(false);
        right.add(new JScrollPane(inboxArea), BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Làm mới");
        right.add(refreshBtn, BorderLayout.SOUTH);

        split.setLeftComponent(left); split.setRightComponent(right);
        root.add(split, BorderLayout.CENTER);

        // hành động
        attachBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                attached = fc.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Đã đính kèm: " + attached.getName());
            }
        });

        emojiBtn.addActionListener(e -> bodyArea.append("😀"));

        sendBtn.addActionListener(e -> {
            try {
                String to = toField.getText().trim(); 
                if (to.isEmpty()) { 
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người nhận"); 
                    return; 
                }
                String subj = subjectField.getText().trim();
                String body = bodyArea.getText();
                EmailMessage m = new EmailMessage(username, to, subj, body);
                if (attached != null) {
                    byte[] data = Files.readAllBytes(attached.toPath());
                    m.addAttachment(new Attachment(attached.getName(), data));
                    attached = null;
                }
                client.sendMessage(m);
                chatArea.append("Bạn -> " + to + " [" + m.timestamp + "]\n" + body + "\n----\n");
                bodyArea.setText("");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Gửi thất bại: " + ex.getMessage()); 
            }
        });

        refreshBtn.addActionListener(e -> {
            // yêu cầu server gửi lại hộp thư đã lưu bằng PING 
            // (server gửi hộp thư trong quá trình LOGIN; ở đây chỉ yêu cầu PONG để cập nhật)
            try { client.ping(); } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); 
            }
        });

        // khi người dùng đăng nhập, server đã gửi email lưu trữ (trong lúc login). 
        // Các email đó sẽ được chuyển đến onMessage().
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // Lắng nghe phản hồi
    @Override
    public void onResponse(Response r) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, r.message));
    }

    // Lắng nghe tin nhắn đến
    @Override
    public void onMessage(EmailMessage m) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(m.from + " -> bạn [" + m.timestamp + "]\n" + m.body + "\n");
            if (!m.attachments.isEmpty()) {
                try {
                    File d = new File("downloads");
                    if (!d.exists()) d.mkdirs();
                    for (Attachment a : m.attachments) {
                        File f = new File(d, a.fileName);
                        Files.write(f.toPath(), a.data);
                        chatArea.append("[Đã lưu tệp đính kèm: downloads/" + a.fileName + "]\n");
                    }
                } catch (Exception ex) {
                    chatArea.append("[Lưu tệp đính kèm thất bại]\n");
                }
            }
            chatArea.append("----\n");
        });
    }
}
