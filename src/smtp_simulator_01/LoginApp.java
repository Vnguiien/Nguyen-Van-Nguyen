package smtp_simulator_01;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Giao diện Đăng nhập / Đăng ký.
 * Khi đăng nhập thành công sẽ mở MainApp và tái sử dụng cùng một SMTPClient.
 */
public class LoginApp extends JFrame implements SMTPClient.MessageListener {
    private final JTextField userField = new JTextField(16);
    private final JPasswordField passField = new JPasswordField(16);
    private SMTPClient client; // sẽ được tạo khi khởi động để xác thực

    public LoginApp() {
        super("ChatMail — Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420,300);
        setLocationRelativeTo(null);
        initUI();

        try {
            // tạo kết nối client và dùng this làm listener (nhận phản hồi từ server)
            client = new SMTPClient("localhost", 2525, this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("📧 ChatMail"); title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        p.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; p.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; p.add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; p.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; p.add(passField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JButton loginBtn = new JButton("Đăng nhập");
        p.add(loginBtn, gbc);
        gbc.gridx = 1;
        JButton regBtn = new JButton("Đăng ký");
        p.add(regBtn, gbc);

        add(p);

        loginBtn.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên và mật khẩu");
            return;
        }
        try {
            client.register(u, p);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi IO: " + e.getMessage());
        }
    }

    private void doLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên và mật khẩu");
            return;
        }
        try {
            client.login(u, p);
            // phản hồi thực sự xử lý trong onResponse()
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi IO: " + e.getMessage());
        }
    }

    // Lắng nghe phản hồi từ server
    @Override
    public void onResponse(Response r) {
        SwingUtilities.invokeLater(() -> {
            if (r.ok && (r.message.equals("Login ok") || r.message.equals("Đăng nhập thành công"))) {
                // đăng nhập thành công -> mở MainApp và chuyển quyền sở hữu client
                String username = userField.getText().trim();
                MainApp main = new MainApp(username, client);
                client.setListener(main);
                main.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, r.message);
            }
        });
    }

    @Override
    public void onMessage(EmailMessage m) {
        // bỏ qua tin nhắn khi đang ở màn hình đăng nhập
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginApp().setVisible(true));
    }
}
