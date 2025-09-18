package smtp_simulator_01;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Trợ giúp SQLite cho người dùng, email và tệp đính kèm.
 * Tệp cơ sở dữ liệu: server_data.db (trong thư mục làm việc)
 */
public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:server_data.db";

    static {
        try {
            // nạp driver (không bắt buộc trong JVM hiện đại nhưng vẫn an toàn)
            Class.forName("org.sqlite.JDBC");
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT UNIQUE NOT NULL,
              password TEXT NOT NULL
            );
            """;
        String createEmails = """
            CREATE TABLE IF NOT EXISTS emails (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              sender TEXT NOT NULL,
              receiver TEXT NOT NULL,
              subject TEXT,
              body TEXT,
              timestamp TEXT NOT NULL
            );
            """;
        String createAttachments = """
            CREATE TABLE IF NOT EXISTS attachments (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              email_id INTEGER NOT NULL,
              filename TEXT,
              data BLOB,
              FOREIGN KEY(email_id) REFERENCES emails(id) ON DELETE CASCADE
            );
            """;
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(createUsers);
            st.execute(createEmails);
            st.execute(createAttachments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // đăng ký tài khoản
    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username,password) VALUES(?,?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // đăng nhập
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // lưu email và tệp đính kèm
    public static boolean saveEmail(EmailMessage m) {
        String sql = "INSERT INTO emails(sender,receiver,subject,body,timestamp) VALUES(?,?,?,?,?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.from.toLowerCase());
            ps.setString(2, m.to.toLowerCase());
            ps.setString(3, m.subject);
            ps.setString(4, m.body);
            ps.setString(5, m.timestamp);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    int emailId = gk.getInt(1);
                    if (!m.attachments.isEmpty()) {
                        String aSql = "INSERT INTO attachments(email_id,filename,data) VALUES(?,?,?)";
                        try (PreparedStatement aps = c.prepareStatement(aSql)) {
                            for (Attachment a : m.attachments) {
                                aps.setInt(1, emailId);
                                aps.setString(2, a.fileName);
                                aps.setBytes(3, a.data);
                                aps.addBatch();
                            }
                            aps.executeBatch();
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // lấy hộp thư đến cho username
    public static List<EmailMessage> getInbox(String username) {
        List<EmailMessage> list = new ArrayList<>();
        String sql = "SELECT id,sender,receiver,subject,body,timestamp FROM emails WHERE receiver=? ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String sender = rs.getString("sender");
                    String receiver = rs.getString("receiver");
                    String subject = rs.getString("subject");
                    String body = rs.getString("body");
                    String timestamp = rs.getString("timestamp");
                    EmailMessage m = new EmailMessage(sender, receiver, subject, body);
                    // ghi đè timestamp bằng thời gian từ DB
                    // (timestamp trong EmailMessage được set cố định trong constructor,
                    // nhưng để hiển thị thì có thể bỏ qua sự khác biệt)
                    // tải tệp đính kèm
                    String aSql = "SELECT filename,data FROM attachments WHERE email_id=?";
                    try (PreparedStatement aps = c.prepareStatement(aSql)) {
                        aps.setInt(1, id);
                        try (ResultSet ar = aps.executeQuery()) {
                            while (ar.next()) {
                                String fn = ar.getString("filename");
                                byte[] data = ar.getBytes("data");
                                m.addAttachment(new Attachment(fn, data));
                            }
                        }
                    }
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
