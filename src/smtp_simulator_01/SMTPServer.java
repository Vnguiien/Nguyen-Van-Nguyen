package smtp_simulator_01;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Máy chủ mô phỏng SMTP (giao tiếp bằng đối tượng).
 * Hỗ trợ nhiều client cùng lúc.
 * Lưu trữ tài khoản và email trong SQLite qua DatabaseHelper.
 */
public class SMTPServer {
    private final int port;
    // Danh sách người dùng đang online: username(lowercase) -> luồng xuất đối tượng
    private final Map<String, ClientConnection> online = new ConcurrentHashMap<>();

    public SMTPServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("✅ SMTPServer khởi động trên cổng " + port);
            while (true) {
                Socket s = ss.accept();
                new Thread(new Worker(s)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Lỗi server: " + e.getMessage());
        }
    }

    private class Worker implements Runnable {
        private final Socket socket;
        Worker(Socket socket) { this.socket = socket; }

        public void run() {
            String loggedUser = null;
            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                Object o;
                while ((o = ois.readObject()) != null) {
                    if (!(o instanceof Command)) continue;
                    Command cmd = (Command) o;
                    switch (cmd.type) {
                        case REGISTER -> {
                            String[] reg = (String[]) cmd.payload;
                            boolean ok = DatabaseHelper.registerUser(reg[0], reg[1]);
                            oos.writeObject(ok ? Response.ok("Đăng ký thành công") : Response.err("Tên tài khoản đã tồn tại"));
                            oos.flush();
                        }
                        case LOGIN -> {
                            String[] lg = (String[]) cmd.payload;
                            boolean ok = DatabaseHelper.loginUser(lg[0], lg[1]);
                            if (ok) {
                                loggedUser = lg[0].toLowerCase();
                                // ghi nhận client online
                                online.put(loggedUser, new ClientConnection(loggedUser, oos));
                                oos.writeObject(Response.ok("Đăng nhập thành công"));
                                oos.flush();
                                // gửi hộp thư đã lưu
                                for (EmailMessage m : DatabaseHelper.getInbox(loggedUser)) {
                                    oos.writeObject(m);
                                    oos.flush();
                                }
                                System.out.println("👤 Người dùng đăng nhập: " + loggedUser);
                            } else {
                                oos.writeObject(Response.err("Sai tài khoản hoặc mật khẩu"));
                                oos.flush();
                            }
                        }
                        case SEND -> {
                            EmailMessage msg = (EmailMessage) cmd.payload;
                            if (msg == null) {
                                oos.writeObject(Response.err("Tin nhắn rỗng"));
                                oos.flush();
                                continue;
                            }
                            // lưu vào DB
                            DatabaseHelper.saveEmail(msg);
                            // chuyển tiếp nếu người nhận đang online
                            ClientConnection dest = online.get(msg.to.toLowerCase());
                            if (dest != null) {
                                try { dest.sendObject(msg); }
                                catch (IOException ex) {
                                    System.err.println("⚠️ Gửi tiếp thất bại: " + ex.getMessage());
                                }
                            }
                            oos.writeObject(Response.ok("Tin nhắn đã lưu/gửi"));
                            oos.flush();
                        }
                        case LOGOUT -> {
                            if (loggedUser != null) {
                                online.remove(loggedUser);
                                oos.writeObject(Response.ok("Đã đăng xuất"));
                                oos.flush();
                                System.out.println("👋 Người dùng thoát: " + loggedUser);
                            }
                        }
                        case PING -> {
                            oos.writeObject(Response.ok("PONG"));
                            oos.flush();
                        }
                    }
                }
            } catch (EOFException | SocketException e) {
                // Client đóng kết nối hoặc reset
                System.out.println("❎ Client ngắt kết nối: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (loggedUser != null) {
                    online.remove(loggedUser);
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static class ClientConnection {
        final String username;
        final ObjectOutputStream out;
        ClientConnection(String username, ObjectOutputStream out) {
            this.username = username;
            this.out = out;
        }
        synchronized void sendObject(Object o) throws IOException {
            out.writeObject(o);
            out.flush();
            out.reset();
        }
    }

    public static void main(String[] args) {
        int port = 2525;
        SMTPServer server = new SMTPServer(port);
        server.start();
    }
}
