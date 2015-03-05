package cloudgene.mapred.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailHelper {

	public static void send(final String smtp, final String port,
			final String username, final String password, final String name,
			String tos, String subject, String text) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			InternetAddress[] addresses = InternetAddress.parse(tos);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(name));
			message.setRecipients(Message.RecipientType.TO, addresses);
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

			System.out.println("E-Mail an " + tos + " gesendet...");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
