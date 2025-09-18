package smtp_simulator_01;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mô hình Email/Tin nhắn (hỗ trợ nhiều tệp đính kèm).
 */
public class EmailMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String from;   // ví dụ: "alice"
    public final String to;     // ví dụ: "bob"
    public final String subject;  // tiêu đề
    public final String body;     // nội dung
    public final String timestamp; // thời gian gửi
    public final List<Attachment> attachments = new ArrayList<>(); // danh sách tệp đính kèm

    public EmailMessage(String from, String to, String subject, String body) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // thêm tệp đính kèm
    public void addAttachment(Attachment a) { attachments.add(a); }

    @Override
    public String toString() {
        // hiển thị email ở dạng chuỗi
        return "[" + timestamp + "] " + from + " -> " + to + " : " + subject + "\n" + body;
    }
}
