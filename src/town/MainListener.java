package town;

import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class MainListener extends ListenerAdapter
{
	HashMap<String, DiscordGame> games;
	
	public MainListener() 
	{
		games = new HashMap<>();
	}
	
	public static void main(String[] args)
		throws InterruptedException
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
		jda.awaitReady();
	}

	@Override
	public void onReady(ReadyEvent e)
	{
		System.out.println("Bot is ready to be used");
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) 
	{
		System.out.println("Joined new guild");
		Guild guild = e.getGuild();
		guild.getRoles().stream().filter((role) -> games.get(role.getName()) != null).forEach((role) -> games.get(role.getName()).getNewGuild(e.getGuild()));
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{
		// TODO: Allow user to specify prefix
		Message message = e.getMessage();
		if (message.getContentRaw().contentEquals("!startLobby"))
			startLobby(e.getJDA(), e.getGuild().getId(), e.getChannel());
		else if (message.getContentRaw().contentEquals("!endLobby"))
			endLobby(e.getJDA(), e.getGuild().getId(), e.getChannel());
		else if (message.getContentRaw().startsWith("!"))
		{
			DiscordGame game = games.get(e.getGuild().getId());
			if (game != null)
				games.get(e.getGuild().getId()).processMessage(e.getMessage());
			else
				e.getChannel().sendMessage("Lobby hasn't been created yet. Do so with !startLobby").queue();
		}
		
		// TODO: When someone joins, check if they have an open private channel first.
	}
	
	private void endLobby(JDA jda, String guildID, MessageChannel channelUsed)
	{
		if (!games.containsKey(guildID))
			channelUsed.sendMessage("There is no lobby to end").queue();
		else
		{
			games.remove(guildID);
			channelUsed.sendMessage("Lobby ended").queue();
		}		
	}

	public void startLobby(JDA jda, String guildID, MessageChannel channelUsed)
	{
		if (games.containsKey(guildID))
			channelUsed.sendMessage("Lobby already started").queue();
		else
		{
			games.put(guildID, new DiscordGame(jda, guildID));
			channelUsed.sendMessage("Lobby started").queue();
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
}
