package town;

import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class MainListener extends ListenerAdapter
{
	String prefix;

	HashMap<String, DiscordGame> games;
	
	public MainListener()
	{
		prefix = "tos.";
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
		// TODO: Make sure that when kicked, to delete the game from the hash table
		// TODO: Add a help command
		Message message = e.getMessage();
		if (e.isFromType(ChannelType.PRIVATE))
		{
			// TODO: Check if user is in an ongoing game, then act accordingly
			return;
		}

		if (message.getContentRaw().contentEquals(prefix + "startParty"))
			startLobby(e.getJDA(), e.getGuild().getId(), e.getChannel(), e.getMember());
		// TODO: Replace endParty with endGame if the game starts (or just have them be the same)
		else if (message.getContentRaw().contentEquals(prefix + "endParty"))
			endLobby(e.getJDA(), e.getGuild().getId(), e.getChannel());
		else if (message.getContentRaw().startsWith(prefix))
		{
			DiscordGame game = games.get(e.getGuild().getId());
			if (game != null)
				games.get(e.getGuild().getId()).processMessage(e.getMessage());
			else
				e.getChannel().sendMessage("Party hasn't been created yet. Do so with tos.startParty").queue();
		}
		// TODO: When someone joins, check if they have an open private channel first.
	}
	
	private void endLobby(JDA jda, String guildID, MessageChannel channelUsed)
	{
		if (!games.containsKey(guildID))
			channelUsed.sendMessage("There is no party to end").queue();
		else
		{
			games.remove(guildID);
			channelUsed.sendMessage("Party ended").queue();
		}
	}

	public void startLobby(JDA jda, String guildID, MessageChannel channelUsed, Member partyLeader)
	{
		if (games.containsKey(guildID))
			channelUsed.sendMessage("Party already started").queue();
		else
		{
			DiscordGame game = new DiscordGame(jda, guildID, partyLeader.getId());
			channelUsed.sendMessage("Party started").queue();
			game.joinGame(partyLeader.getId(), channelUsed);
			games.put(guildID, game);
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
