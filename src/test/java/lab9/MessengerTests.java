package lab9;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessengerTests
{

	Client client;
	TemplateEngine templateEngine;
	Template template;
	MailServer mailServer;
	String message = "message";
	String clientEmail = "someone@somewhere.com";

	Messenger messenger;

	@BeforeEach
	public void setup()
	{
		client = mock(Client.class);
		template = mock(Template.class);
		templateEngine = mock(TemplateEngine.class);
		mailServer = mock(MailServer.class);

		messenger = new Messenger(mailServer, templateEngine);
	}

	@Test
	public void create()
	{
		var messenger = new Messenger(mailServer, templateEngine);
		assertNotNull(messenger);
	}

	@Test
	public void sendMessage()
	{
		when(client.getEmail()).thenReturn(clientEmail);
		when(templateEngine.prepareMessage(template, client)).thenReturn(message);

		messenger.sendMessage(client, template);

		verify(mailServer, times(1)).send(clientEmail, message);
	}

	@Test
	public void sendMessage_NullMessage()
	{
		when(client.getEmail()).thenReturn(clientEmail);
		when(templateEngine.prepareMessage(template, client)).thenReturn(null);

		messenger.sendMessage(client, template);

		verify(mailServer, times(1)).send(clientEmail, null);
	}

	@Test
	public void sendMessage_NullClientEmail()
	{
		when(client.getEmail()).thenReturn(null);
		when(templateEngine.prepareMessage(template, client)).thenReturn(message);

		messenger.sendMessage(client, template);

		verify(mailServer, times(1)).send(null, message);
	}

	@Test
	public void sendMessage_NullClient()
	{
		when(templateEngine.prepareMessage(template, null)).thenReturn(message);

		assertThrows(NullPointerException.class, () -> messenger.sendMessage(null, template));
	}

	@Test
	public void sendMessage_NullTemplate()
	{
		when(client.getEmail()).thenReturn(clientEmail);
		when(templateEngine.prepareMessage(null, client)).thenReturn(message);

		messenger.sendMessage(client, null);

		verify(mailServer, times(1)).send(clientEmail, message);
	}

	@Test
	public void sendMessage_NullEngine()
	{
		messenger = new Messenger(mailServer, null);
		when(client.getEmail()).thenReturn(clientEmail);
		when(templateEngine.prepareMessage(template, client)).thenReturn(message);

		assertThrows(NullPointerException.class, () -> messenger.sendMessage(client, template));
	}

	@Test
	public void sendMessage_NullService()
	{
		messenger = new Messenger(null, templateEngine);
		when(client.getEmail()).thenReturn(clientEmail);
		when(templateEngine.prepareMessage(template, client)).thenReturn(message);

		assertThrows(NullPointerException.class, () -> messenger.sendMessage(client, template));
	}
}
