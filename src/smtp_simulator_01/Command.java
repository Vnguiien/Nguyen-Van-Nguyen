package smtp_simulator_01;
import java.io.Serializable;

/**
 * Lệnh từ client (máy khách) gửi đến server (máy chủ).
 */
public class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    // Các loại lệnh có thể gửi
    public enum Type { REGISTER, LOGIN, SEND, LOGOUT, PING }

    public final Type type;      // Loại lệnh
    public final Object payload; // Dữ liệu kèm theo lệnh

    public Command(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    // Tạo lệnh đăng ký tài khoản
    public static Command register(String username, String password) {
        return new Command(Type.REGISTER, new String[]{username, password});
    }

    // Tạo lệnh đăng nhập
    public static Command login(String username, String password) {
        return new Command(Type.LOGIN, new String[]{username, password});
    }

    // Tạo lệnh gửi email
    public static Command send(EmailMessage msg) {
        return new Command(Type.SEND, msg);
    }

    // Tạo lệnh đăng xuất
    public static Command logout(String username) {
        return new Command(Type.LOGOUT, username);
    }

    // Tạo lệnh kiểm tra kết nối (ping)
    public static Command ping() { return new Command(Type.PING, null); }
}
