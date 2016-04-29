package solver;

import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Solver 
{
	static String from = "*****"; //change 'from' to your email address (e.g. "johndoe@gmail.com")
	static String user = "*****"; //change 'user' to your email account name (e.g. "johndoe")
	static String pass = "*****"; //change 'pass' to your email password (e.g. "petname345")

	public static void send(String to, String host, String msg) throws MessagingException
	{
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");	
		
		Session session = Session.getInstance(props);
		Message message = new MimeMessage(session);
			
		message.setText(msg);
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		Transport.send(message, user, pass);
		System.out.println("\tSuccessfully sent message \"" + msg + "\" to " + to + "!\n");
	}
		
	public static String[] fetch(String host) throws IOException, NoSuchProviderException, MessagingException
	{
		String[] output = new String[2];
		
		Properties props = new Properties();
		props.put("mail.store.protocol", "pop3");
		props.put("mail.pop3.host", host);
		props.put("mail.pop3.port", "995");
		props.put("mail.pop3.starttls.enable", "true");
			
		Session session = Session.getInstance(props);
		Store store = session.getStore("pop3s");
		store.connect(host, user, pass);
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		
		try
		{
			Message[] messages = folder.getMessages();
			if (messages.length == 0)
				throw new InboxEmptyException("No messages found.");
			Address[] a = messages[0].getFrom();
			output[0] = a[0].toString();
			
			String html = getText(messages[0]);
			String temp = "";
			int i = 0;
			while (i < html.length())
			{
				if (html.substring(i).startsWith("<td>"))
				{
					temp = html.substring(i + 4);
					i = 0;
					break;
				}
				i++;
			}
			while (i < temp.length())
			{
				if (temp.substring(i).startsWith("</td>"))
				{
					temp = temp.substring(0, i);
					break;
				}
				i++;
			}
			if (temp == "")
				temp = html;
			output[1] = temp.trim();
			System.out.println("\n\tReceived message \"" + output[1] + "\" from " + output[0] + ".");
			
			folder.close(true);
			store.close();
		}
		catch (InboxEmptyException e)
		{
			System.out.println(e.getMessage());
		}
		return output;
	}
	
	public static String getText(Part p) throws MessagingException, IOException
	{
		if (p.isMimeType("text/*"))
		{
			String s = (String) p.getContent();
			return s;
		}
		if (p.isMimeType("multipart/*"))
		{
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++)
			{
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws NoSuchProviderException, IOException, MessagingException, InvalidSymbolException, TooComplicatedException
	{
		String[] output = new String[2];
		while(true)
		{
			output = fetch("pop.gmail.com"); //change user, password
			if (output[0] != null)
			{
				Differentiator d = new Differentiator(output[1].length()*2);
				try
				{
					String msg = d.differentiate(d.evaluate(d.formTree(output[1]))).toString();
					send(output[0], "smtp.gmail.com", msg); //change email, user, password
				}
				catch (InvalidSymbolException e)
				{
					send(output[0], "smtp.gmail.com", e.getMessage()); //change email, user, password
				}
				catch (TooComplicatedException e)
				{
					send(output[0], "smtp.gmail.com", e.getMessage()); //change email, user, password
				}
				catch (Exception e)
				{
					send(output[0], "smtp.gmail.com", "I can't differentiate that.");
				}
			}
		}
	}
}