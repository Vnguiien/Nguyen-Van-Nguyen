<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   GỬI EMAIL MÔ PHỎNG SMTP QUA SOCKET
</h2>
<div align="center">
    <p align="center">
        <img src="docs/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="docs/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="docs/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>


## 📖 1. Giới thiệu

Đề tài: Gửi email mô phỏng qua SMTP bằng Socket

Mục tiêu: Xây dựng hệ thống mô phỏng quá trình gửi email qua giao thức SMTP (Simple Mail Transfer Protocol).

Cách hoạt động:

Người dùng nhập thông tin email (người gửi, người nhận, tiêu đề, nội dung, tệp đính kèm) qua giao diện Swing.

Client gửi các lệnh SMTP qua TCP Socket đến Server.

Server xử lý yêu cầu, phản hồi theo chuẩn mã SMTP, sau đó lưu email thành file .txt trong thư mục mailbox/.

Ứng dụng mô phỏng này giúp sinh viên hiểu rõ hơn về cách một máy khách (email client) như Outlook, Gmail hoạt động khi giao tiếp với máy chủ SMTP, nhưng trong phạm vi an toàn và đơn giản hơn (lưu file thay vì gửi email thật).



## 📌 2. Công nghệ sử dụng

- Trong quá trình xây dựng hệ thống mô phỏng gửi email qua giao thức SMTP bằng Socket, nhóm sử dụng các công nghệ chính sau:

⸻

2.1. Ngôn ngữ lập trình Java

Java là ngôn ngữ lập trình hướng đối tượng, đa nền tảng, chạy trên JVM (Java Virtual Machine) với phương châm "Write Once, Run Anywhere".

Trong hệ thống này, Java được lựa chọn vì:

Hỗ trợ mạnh mẽ lập trình Socket và đa luồng (multithreading).

Thư viện I/O phong phú để đọc/ghi dữ liệu giữa client – server.

Có cộng đồng lớn, tài liệu hỗ trợ phong phú.

Dễ dàng xây dựng giao diện đồ họa Swing để nhập email và quản lý tương tác người dùng..

⸻

2.2. Socket trong Java

Socket là điểm cuối (endpoint) cho quá trình giao tiếp giữa Client – Server qua mạng.

ServerSocket: tạo máy chủ, lắng nghe yêu cầu.

Socket: tạo kết nối từ client đến server.

InputStream / OutputStream: trao đổi dữ liệu qua kết nối.

Trong ứng dụng này:

Server mở cổng 2525 và chờ Client kết nối.

Client gửi các lệnh SMTP như:

HELO → chào server

MAIL FROM → khai báo địa chỉ gửi

RCPT TO → khai báo địa chỉ nhận

DATA → gửi nội dung email

QUIT → thoát kết nối

Server phản hồi bằng mã chuẩn SMTP:

220 (Ready), 250 (OK), 354 (Start mail input), 221 (Bye).

 Việc sử dụng TCP Socket đảm bảo dữ liệu được gửi tin cậy, đúng thứ tự, mô phỏng sát cách thức SMTP hoạt động trong thực tế.


2.3. Mô hình Client – Server

Hệ thống được xây dựng theo kiến trúc Client – Server:

Client: Giao diện người dùng (Swing), nhập thông tin email → gửi lệnh SMTP đến server.

Server: Nhận lệnh SMTP, xử lý, phản hồi → lưu email thành file .txt.

📌 Ưu điểm:

Giúp sinh viên dễ hình dung cách ứng dụng email thật (Gmail, Outlook) giao tiếp với SMTP server.

Dễ dàng mở rộng để bổ sung thêm tính năng: xác thực người dùng, hộp thư đến, gửi nhiều email cùng lúc…

⸻

2.4. IDE: Eclipse / IntelliJ IDEA

Để phát triển ứng dụng, nhóm sử dụng IDE hỗ trợ Java:

Eclipse: miễn phí, phổ biến.

IntelliJ IDEA: hiện đại, hỗ trợ nhiều tiện ích như debug, gợi ý code.

Ưu điểm khi dùng IDE:

Quản lý project và các file .java rõ ràng.

Debug và chạy chương trình thuận tiện.

Quan sát log SMTP Client – Server trực tiếp trong console

⸻

 ## 💻 3. Các hình ảnh chức năng

Trong phần này, hệ thống được minh họa bằng các hình ảnh chụp từ quá trình chạy chương trình. Các hình này giúp làm rõ cách thức giao tiếp giữa SMTP Client và SMTP Server, cũng như kết quả lưu trữ email trên server.

⸻

3.1. Giao tiếp Client ↔ Server (Console log)

Khi chương trình được chạy, phía Client sẽ gửi các lệnh theo chuẩn SMTP đến Server thông qua kết nối TCP Socket. Đồng thời, Server sẽ phản hồi bằng các mã trạng thái.

• Console phía Client hiển thị:

<p align="center">
  <img src="docs/client.png" width="484" height="139" alt="Client console" />
</p>
<p align="center"><i>Hình ảnh 1</i></p>

• Console phía Server hiển thị:

<p align="center">
  <img src="docs/server.png" width="615" height="260" alt="Server console" />
</p>
<p align="center"><i>Hình ảnh 2</i></p>

---

### 3.2. Email được lưu trên Server

- Sau khi client gửi email thành công, Server sẽ tự động tạo thư mục `mailbox/` (nếu chưa tồn tại) và lưu toàn bộ nội dung email thành file `.txt`.

• File được tạo:

<p align="center">
  <img src="docs/Screenshot 2025-09-18 231500.png" width="240" height="148" alt="Mailbox file" />
</p>
<p align="center"><i>Hình ảnh 3</i></p>

• Nội dung file email:

<p align="center">
  <img src="docs/email-content.png" width="387" height="164" alt="Email content" />
</p>
<p align="center"><i>Hình ảnh 4</i></p>

• Giao diện email:

<p align="center">
  <img src="docs/email-ui.png" width="488" height="444" alt="Email UI" />
</p>
<p align="center"><i>Hình ảnh 5</i></p>


## ⚙️ 4. Các bước cài đặt

    Phần này mô tả các bước chuẩn bị, cài đặt môi trường và chạy thử hệ thống SMTP mô phỏng bằng Java. Toàn bộ các bước đều có thể thực hiện trên một máy tính cá nhân mà không cần Internet, vì chương trình chỉ chạy trong mạng cục bộ (localhost).

⸻

4.1. Chuẩn bị môi trường

    Trước khi chạy hệ thống, cần chuẩn bị:
    
1. Cài đặt JDK (Java Development Kit)
        
     • Phiên bản khuyến nghị: JDK 8 trở lên
     
     • Kiểm tra bằng lệnh:

        java -version

2. Cài đặt IDE để lập trình và chạy chương trình
   
         • Có thể sử dụng Eclipse IDE, IntelliJ IDEA hoặc NetBeans.
         
         • Trong đề tài này, IDE phổ biến nhất là Eclipse.
 
3. Cấu trúc thư mục project
    
- Sau khi tạo project Java trong Eclipse, sắp xếp các file theo cấu trúc:

<p align="center"> <img width="231" height="275" alt="image" src="https://github.com/user-attachments/assets/3f0075dd-6231-4601-b568-2e8f2e5e89a3" /> </p>
<p align="center"><i>Hình ảnh 6</i></p>
4.2. Chạy chương trình

4.2.1. Chạy Server

 1. Mở file SmtpServer.java trong Eclipse.
    
 2. Chọn Run As → Java Application.
    
 3. Console của Eclipse hiển thị thông báo:

<p align="center"> <img width="455" height="59" alt="image" src="https://github.com/user-attachments/assets/1435de41-91fd-407e-91b5-7ab6effe72d2" /> </p>
<p align="center"><i>Hình ảnh 7</i></p>
4.2.2. Chạy Client

 1. Mở file SmtpClientUI.java trong Eclipse.
    
 2. Chọn Run As → Java Application.
    
 3. Giao diện ứng dụng hiển thị cửa sổ với:
    
         • Ô nhập người nhận.
         
         • Ô nhập nội dung email.
 
 • Nút ✉ Gửi Email.

Khi người dùng bấm nút gửi, client sẽ:

     • Tạo kết nối TCP đến server (cổng 2525).
     
     • Gửi lệnh SMTP: HELO, MAIL FROM, RCPT TO, DATA.
     
     • Gửi nội dung email.
     
     • Kết thúc bằng dấu "." theo chuẩn SMTP.
     
     • Đóng kết nối bằng lệnh QUIT.

4.2.3. Kiểm tra kết quả

 1. Sau khi email được gửi thành công, server sẽ tự động tạo thư mục mailbox/ (nếu chưa có).
    
 2. Mỗi email sẽ được lưu thành một file .txt với tên theo thời gian, ví dụ:

<p align="center"> <img width="197" height="32" alt="image" src="https://github.com/user-attachments/assets/321c6abe-641d-426b-b984-b008b41b45b1" /> </p>
<p align="center"><i>Hình ảnh 8</i></p>
3. Nội dung file email bao gồm:
   
         • Người gửi
         
         • Người nhận
         
         • Chủ đề
         
         • Thời gian
         
         • Nội dung email

Ví dụ:

<p align="center"> <img width="393" height="108" alt="image" src="https://github.com/user-attachments/assets/bdfdc47c-27ff-470a-8205-51154ec6ebda" /> </p>
<p align="center"><i>Hình ảnh 9</i></p>

## 📞 5. Liên hệ
- 💌 Email: thankfwong23@gmail.com  
- ☎️ SĐT: 0383 609 685 


© 2025 AIoTLab, Faculty of Information Technology, DaiNam University. All rights reserved.

---
