
package CampusCare.passwordreset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {

    private final JavaMailSender mailSender;
    private final String mailUsername;
    private final String appBaseUrl;

    public PasswordResetEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String mailUsername,
            @Value("${app.base-url}") String appBaseUrl) {
        this.mailSender = mailSender;
        this.mailUsername = mailUsername;
        this.appBaseUrl = appBaseUrl;
    }

    public void sendResetEmail(String recipient, String rawToken) {
        String resetLink = appBaseUrl
                + "/reset-password?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(recipient);
        message.setSubject("CampusCare - Password Reset");
        message.setText(
                "Hello,\n\n"
                + "A password reset was requested for your CampusCare account.\n\n"
                + "Use the link below to choose a new password. "
                + "This link expires in 15 minutes and can only be used once.\n\n"
                + resetLink
                + "\n\nIf you did not request this, you can ignore this email."
        );

        mailSender.send(message);
    }
}
