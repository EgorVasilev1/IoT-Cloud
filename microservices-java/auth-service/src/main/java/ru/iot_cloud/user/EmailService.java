//package ru.iot_cloud.user;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.mail.Message;
//import jakarta.mail.MessagingException;
//import jakarta.mail.Session;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;
//
//import java.util.Properties;
//
//@ApplicationScoped
//public class EmailService {
//    public void sendEmail(String email, String subject, String message) throws MessagingException {
//        Properties props = new Properties();
//        // заполните свойства согласно вашим настройкам SMTP сервера
////        props.put("mail.smtp.host", "smtp.gmail.com");
////        props.put("mail.smtp.port", "587");
////        props.put("mail.smtp.starttls.enable", "true");
////        props.put("mail.smtp.auth", "true");
////        props.put("mail.smtp.user", "iotcloud170@gmail.com");
////        props.put("mail.smtp.password", "zoom061106");
//
//        props.put("mail.smtp.host", "localhost");
//        props.put("mail.smtp.port", "1025");
//        props.put("mail.smtp.starttls.enable", "false");
//        props.put("mail.smtp.auth", "false");
////        props.put("mail.smtp.user", "iotcloud170@gmail.com");
////        props.put("mail.smtp.password", "zoom061106");
//
//        Session session = Session.getInstance(props);
//        MimeMessage mimeMessage = new MimeMessage(session);
//
//        Transport transport = null;
//        try {
//            mimeMessage.setFrom(new InternetAddress("iotcloud170@gmail.com"));
//            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//            mimeMessage.setSubject(subject);
//            mimeMessage.setText(message);
//
//            transport = session.getTransport("smtp");
//            transport.connect("iotcloud170@gmail.com", "zoom061106");
//            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
//            transport.close();
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (transport != null && !transport.isConnected()) {
//                transport.close();
//            }
//        }
//    }
//}
//

package ru.iot_cloud.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@ApplicationScoped
public class EmailService {
    public void sendEmail(String email, String subject, String message) throws MessagingException {
        Properties props = new Properties();
        // Настройки SMTP для локального сервера
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "1025");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.auth", "false");

        Session session = Session.getInstance(props);
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(new InternetAddress("iotcloud170@gmail.com"));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport transport = session.getTransport("smtp");
            // Убираем аутентификацию, так как она не требуется
            transport.connect();
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}