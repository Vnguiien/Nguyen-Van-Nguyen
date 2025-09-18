package smtp_simulator_01;

import java.io.Serializable;

/**
 * Phản hồi từ máy chủ gửi về cho client.
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    public final boolean ok;       // trạng thái thành công hay thất bại
    public final String message;   // nội dung thông điệp phản hồi

    public Response(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    // Tạo phản hồi thành công
    public static Response ok(String m) { 
        return new Response(true, m); 
    }

    // Tạo phản hồi lỗi
    public static Response err(String m) { 
        return new Response(false, m); 
    }
}
