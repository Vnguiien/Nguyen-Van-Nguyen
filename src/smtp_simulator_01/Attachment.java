package smtp_simulator_01;
import java.io.Serializable;

/**
 * Bộ chứa tệp đính kèm: tên tệp + dữ liệu dạng byte.
 */
public class Attachment implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String fileName; // Tên tệp
    public final byte[] data;     // Dữ liệu tệp (dạng mảng byte)

    public Attachment(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }
}
