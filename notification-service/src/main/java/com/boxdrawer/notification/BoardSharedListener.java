package com.boxdrawer.notification;

import com.boxdrawer.notification.event.BoardSharedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Async consumer: receives "board shared" events from RabbitMQ and sends an email. */
@Component
public class BoardSharedListener {

    private static final Logger log = LoggerFactory.getLogger(BoardSharedListener.class);

    private final JavaMailSender mailSender;
    private final String publicUrl;

    public BoardSharedListener(JavaMailSender mailSender, @Value("${app.public-url}") String publicUrl) {
        this.mailSender = mailSender;
        this.publicUrl = publicUrl;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onBoardShared(BoardSharedEvent event) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("noreply@boxdrawer.local");
        mail.setTo(event.recipientEmail());
        mail.setSubject(event.sharedBy() + " shared a board with you");
        mail.setText("Hello,\n\n" + event.sharedBy() + " shared the board \""
                + event.boardName() + "\" with you on BoxDrawer.\n\n"
                + "Download it once (open on https://excalidraw.com):\n"
                + publicUrl + "/api/shared/" + event.shareToken() + "\n\nSee you soon!");
        mailSender.send(mail);
        log.info("Email sent to {} for board '{}'", event.recipientEmail(), event.boardName());
    }
}
