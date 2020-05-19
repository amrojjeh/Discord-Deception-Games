package town;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class MainListener extends ListenerAdapter
{
	public static void main(String[] args)
	{
		String token;
		JDA jda;
		try
		{
			token = loadToken();
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("token.txt did not exist.");
			return;
		}
		
		if (token == "")
			System.out.println("No token is found in token.txt");
		
		try 
		{
			jda = new JDABuilder(token).addEventListeners(new MainListener()).build();
		}
		catch (LoginException e) 
		{
			System.out.println("Couldn't login: " + e);
			return;
		}
	}

	public void onMessageReceived(MessageReceivedEvent e)
	{
		if(e.getMessage().getContentRaw().equals("!phaseStart")) {
			phaseCycle(e.getTextChannel());
		}
	}
	
	public static String loadToken()
		throws FileNotFoundException
	{
		File file = new File("token.txt");
		Scanner scanner = new Scanner(file);
		if (!scanner.hasNextLine())
		{
			scanner.close();
			return "";
		}
		String token = scanner.nextLine();
		scanner.close();
		return token;
	}
	
	public void phaseCycle(MessageChannel c) {
		
		c.sendMessage("Starting phase cycle").queue();
		PhaseManager m = new PhaseManager();
		m.run();
		
	}
}
