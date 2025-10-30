package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.response.EmailResponse;
import com.devteria.identity_service.service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
  @Autowired EmailSenderService emailSenderService;

  @PostMapping("/sendToEmail/{email}/subject/{subject}/body/{body}")
  @Operation(summary = "Send email to user")
  public ApiResponse<EmailResponse> sendEmail(
      @PathVariable String email, @PathVariable String subject, @PathVariable String body)
      throws MessagingException {
    MailBody mailBody =
        MailBody.builder().to(new String[] {email}).subject(subject).body(body).build();
    emailSenderService.sendEmail(mailBody);
    EmailResponse emailResponse = new EmailResponse();
    emailResponse.setToEmail(email);
    emailResponse.setSubject(subject);
    emailResponse.setBody(body);
    emailResponse.setSent(true);
    return ApiResponse.<EmailResponse>builder()
        .code(1000)
        .message("Email sent successfully")
        .data(emailResponse)
        .build();
  }
  @PostMapping("/sendToEmail/{email}/subject/{subject}")
  @Operation(summary = "Send kit email to user")
  public ApiResponse<EmailResponse> sendKitByEmail(@PathVariable String email, @PathVariable String subject) throws MessagingException {
      String body = "<!doctype html>\n" +
              "<html lang=\"vi\">\n" +
              "  <head>\n" +
              "    <meta charset=\"utf-8\" />\n" +
              "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\" />\n" +
              "    <title>Cảm ơn</title>\n" +
              "  </head>\n" +
              "  <body style=\"margin:0;padding:0;background-color:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#333;\">\n" +
              "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n" +
              "      <tr>\n" +
              "        <td align=\"center\" style=\"padding:24px 12px;\">\n" +
              "          <!-- container -->\n" +
              "          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 6px 18px rgba(20,20,30,0.06);\">\n" +
              "            <!-- header -->\n" +
              "            <tr>\n" +
              "              <td style=\"padding:20px 24px;background:linear-gradient(90deg,#10b981,#059669);color:#fff;\">\n" +
              "                <table width=\"100%\" role=\"presentation\">\n" +
              "                  <tr>\n" +
              "                    <td style=\"vertical-align:middle;\">\n" +
              "                      <img src=\"/veganlife-logo.png\" alt=\"Logo\" width=\"36\" style=\"display:inline-block;vertical-align:middle;border:0;outline:none;\" />\n" +
              "                    </td>\n" +
              "                    <td align=\"right\" style=\"font-size:18px;font-weight:700;\">Coaching App</td>\n" +
              "                  </tr>\n" +
              "                </table>\n" +
              "              </td>\n" +
              "            </tr>\n" +
              "\n" +
              "            <!-- hero -->\n" +
              "            <tr>\n" +
              "              <td style=\"padding:32px 36px 16px;\">\n" +
              "                <h1 style=\"margin:0 0 12px;font-size:20px;color:#0f172a;\">Cảm ơn bạn đã đăng ký \uD83C\uDF89</h1>\n" +
              "                <p style=\"margin:0 0 18px;color:#475569;line-height:1.5;\">\n" +
              "                  Chúng tôi đã nhận được yêu cầu của bạn. Tài liệu miễn phí sẽ được gửi tới email này. Trong lúc chờ, bạn có thể truy cập ngay nội dung tại nút bên dưới.\n" +
              "                </p>\n" +
              "\n" +
              "                <!-- CTA button -->\n" +
              "                <table cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"margin:18px 0;\">\n" +
              "                  <tr>\n" +
              "                    <td align=\"center\">\n" +
              "                      <a href=\"https://www.facebook.com/profile.php?id=61581699869417\" target=\"_blank\" rel=\"noopener noreferrer\"\n" +
              "                        style=\"display:inline-block;padding:12px 22px;background:#10b981;color:#ffffff;text-decoration:none;border-radius:10px;font-weight:600;\">\n" +
              "                        Tải tài liệu miễn phí\n" +
              "                      </a>\n" +
              "                    </td>\n" +
              "                  </tr>\n" +
              "                </table>\n" +
              "\n" +
              "                <p style=\"margin:0;color:#64748b;font-size:13px;\">\n" +
              "                  Nếu nút không hoạt động, sao chép và dán đường dẫn sau vào trình duyệt: <br/>\n" +
              "                  <span style=\"word-break:break-all;color:#0f172a;\">YOUR_LINK_HERE</span>\n" +
              "                </p>\n" +
              "              </td>\n" +
              "            </tr>\n" +
              "\n" +
              "            <!-- footer -->\n" +
              "            <tr>\n" +
              "              <td style=\"padding:20px 36px 28px;background:#f8fafc;color:#64748b;font-size:13px;\">\n" +
              "<p style=\"margin:0;\">Chúc bạn một ngày tốt lành,<br /><strong>Đội ngũ Coaching App</strong></p>\n" +
              "                <p style=\"margin:8px 0 0;font-size:12px;color:#94a3b8;\">\n" +
              "                  Nếu bạn không yêu cầu email này, bạn có thể bỏ qua. Địa chỉ của bạn sẽ không được chia sẻ.\n" +
              "                </p>\n" +
              "              </td>\n" +
              "            </tr>\n" +
              "          </table>\n" +
              "          <!-- /container -->\n" +
              "        </td>\n" +
              "      </tr>\n" +
              "    </table>\n" +
              "  </body>\n" +
              "</html>";
      MailBody mailBody =
              MailBody.builder().to(new String[] {email}).subject(subject).body(body).build();
      emailSenderService.sendEmail(mailBody);
      EmailResponse emailResponse = new EmailResponse();
      emailResponse.setToEmail(email);
      emailResponse.setSubject(subject);
      emailResponse.setBody(body);
      emailResponse.setSent(true);
      return ApiResponse.<EmailResponse>builder()
              .code(1000)
              .message("Email sent successfully")
              .data(emailResponse)
              .build();
  }
}
