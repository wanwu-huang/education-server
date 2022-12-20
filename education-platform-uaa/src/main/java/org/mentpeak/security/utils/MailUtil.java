package org.mentpeak.security.utils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author lxp
 * @date 2022/06/14 16:57
 **/
public class MailUtil {
	public static void sendMail(String fromEmail, String toEmail, String emailName, String emailPassword, String title,
								String centent) throws Exception
	{
		Properties prop=new Properties();
		prop.put("mail.host","smtp.mentpeak.com" );
		prop.put("mail.transport.protocol", "smtps");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.port", "465");
		Session session=Session.getInstance(prop);
		session.setDebug(true);
		Transport ts=session.getTransport();
		ts.connect(emailName, emailPassword);
		Message message=new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject(title);
		message.setContent(centent, "text/html;charset=utf-8");
		ts.sendMessage(message, message.getAllRecipients());
	}
}
