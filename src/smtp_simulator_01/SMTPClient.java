package smtp_simulator_01;
import java.io.*;
import java.net.Socket;

/**
 * Lớp Client sử dụng Object streams.
 * Listener sẽ nhận phản hồi (Response) và EmailMessage từ server một cách bất đồng bộ.
 */
public class SMTPClient {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private MessageListener listener;

    // Giao diện để lắng nghe sự kiện từ server
    public interface MessageListener {
        void onResponse(Response r);      // Nhận phản hồi từ server
        void onMessage(EmailMessage m);   // Nhận tin nhắn/email từ server
    }

    public SMTPClient(String host, int port, MessageListener listener) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.listener = listener;
        Thread t = new Thread(this::readLoop);
        t.setDaemon(true); // chạy nền
        t.start();
    }

    // Thay đổi listener (chuyển giao sự kiện)
    public void setListener(MessageListener listener) { 
        this.listener = listener; 
    }

    // Vòng lặp đọc dữ liệu từ server
    private void readLoop() {
        try {
            Object o;
            while ((o = in.readObject()) != null) {
                if (o instanceof Response) {
                    if (listener != null) listener.onResponse((Response) o);
                } else if (o instanceof EmailMessage) {
                    if (listener != null) listener.onMessage((EmailMessage) o);
                }
            }
        } catch (Exception e) {
            // kết nối đóng hoặc lỗi
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // Đăng ký tài khoản
    public synchronized void register(String username, String password) throws IOException {
        out.writeObject(Command.register(username, password));
        out.flush();
    }

    // Đăng nhập
    public synchronized void login(String username, String password) throws IOException {
        out.writeObject(Command.login(username, password));
        out.flush();
    }

    // Gửi tin nhắn/email
    public synchronized void sendMessage(EmailMessage m) throws IOException {
        out.writeObject(Command.send(m));
        out.flush();
    }

    // Đăng xuất
    public synchronized void logout(String username) throws IOException {
        out.writeObject(Command.logout(username));
        out.flush();
    }

    // Ping (yêu cầu server phản hồi, thường dùng để kiểm tra kết nối)
    public synchronized void ping() throws IOException {
        out.writeObject(Command.ping());
        out.flush();
    }

    // Đóng kết nối
    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
