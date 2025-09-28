package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
  @Value("${spring.mail.username}")
  private String fromEmail;

  @Autowired private JavaMailSender mailSender;

  public void sendEmail(MailBody mailBody) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
    mimeMessageHelper.setTo(mailBody.to());
    mimeMessageHelper.setFrom(fromEmail);
    mimeMessageHelper.setSubject(mailBody.subject());
    mimeMessageHelper.setText(mailBody.body(), true);
    mailSender.send(mimeMessage);
  }
}
