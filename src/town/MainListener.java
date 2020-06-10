package town;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MainListener extends ListenerAdapter
{
	String prefix;

	HashMap<Long, DiscordGame> parties;
	HashMap<Long, DiscordGame> games;

	public MainListener()
	{
		prefix = "tos.";
		parties = new HashMap<>();
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
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		DiscordGame game = games.get(event.getGuild().getIdLong());
		if (game != null)
			games.get(event.getGuild().getIdLong()).gameGuildJoin(event.getMember());
	}

	@Override
	public void onReady(ReadyEvent e)
	{
		System.out.println("Bot is ready to be used");
		e.getJDA().getGuilds().forEach((guild) -> delete(guild));
	}

	public void delete(Guild guild)
	{
		if (guild.getOwner().getUser().isBot()) guild.delete().queue();
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e)
	{
		System.out.println("Joined new guild");
		Guild guild = e.getGuild();
		String roleName = guild.getRoles().get(0).getName();
		long oldGuildID = 0;
		try
		{
			oldGuildID = Long.parseLong(roleName);
		}
		catch (NumberFormatException exception)
		{
			return;
		}

		DiscordGame game = parties.get(oldGuildID);
		if (game == null)
			return;
		game.getNewGuild(guild);
		games.put(guild.getIdLong(), game);
		parties.remove(oldGuildID);
	}

	// Bot should leave the game once the owner has changed
	@Override
	public void onGuildUpdateOwner(GuildUpdateOwnerEvent event)
	{
		DiscordGame game = games.get(event.getGuild().getIdLong());
		if (game == null) return;
		game.getGameGuild().leave().queue();
		games.remove(game.getGameID());
		parties.remove(game.getPartyID());
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		DiscordGame game = games.get(event.getGuild().getIdLong());
		if (game == null) return;
		game.gameGuildVoiceJoin(event.getMember(), event.getChannelJoined());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
	{
		DiscordGame game = games.get(event.getGuild().getIdLong());
		if (game == null) return;
		game.gameGuildVoiceJoin(event.getMember(), event.getChannelJoined());
	}


	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event)
	{
		DiscordGame game = games.get(event.getGuild().getIdLong());
		if (game == null) return;
		game.gameGuildPersonLeave(event.getMember());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{
		Message message = e.getMessage();
		if (e.isFromType(ChannelType.PRIVATE))
			return;

		String lowerCaseMessage = message.getContentRaw().toLowerCase();

		if (lowerCaseMessage.contentEquals(prefix + "startparty"))
			startLobby(e.getJDA(), e.getGuild().getIdLong(), e.getChannel(), e.getMember());
		else if (lowerCaseMessage.contentEquals(prefix + "endparty"))
			endLobby(e.getGuild().getIdLong(), e.getChannel(), message.getMember());
		else if (lowerCaseMessage.contentEquals(prefix + "help")) {
			e.getChannel().sendMessage(helpTable()).queue();
		}
		else if (lowerCaseMessage.contentEquals(prefix + "delete") || lowerCaseMessage.contentEquals("!delete"))
		{
			DiscordGame game = games.get(message.getGuild().getIdLong());
			if (game == null) return;
			if (!game.ended)
			{
				message.getChannel().sendMessage("Game hasn't ended yet. If you no longer want to play, leave the server.").queue();
				return;
			}
			games.remove(message.getGuild().getIdLong());
			game.deleteServer();
		}
		else if (lowerCaseMessage.contentEquals(prefix + "transfer") || lowerCaseMessage.contentEquals("!transfer"))
		{
			DiscordGame game = games.get(message.getGuild().getIdLong());
			if (game == null) return;
			if (!game.ended)
			{
				message.getChannel().sendMessage("Game hasn't ended yet. If you no longer want to play, leave the server.").queue();
				return;
			}
			game.transferOrDelete(); // Game gets removed from games when ownership updates
		}
		else if (lowerCaseMessage.startsWith(prefix))
		{
			DiscordGame party = parties.get(e.getGuild().getIdLong());
			DiscordGame game = games.get(e.getGuild().getIdLong());

			if (party != null)
				party.processMessage(e.getMessage());
			else if (game != null)
				game.processMessage(e.getMessage());
			else
				e.getChannel().sendMessage("Party hasn't been created yet. Do so with tos.startParty").queue();
		}
		else if (lowerCaseMessage.startsWith("!"))
		{
			DiscordGame game = games.get(e.getGuild().getIdLong());
			if (game != null)
				game.processMessage("!", e.getMessage());
		}
	}

	public MessageEmbed helpTable() {
		String description = "```" +
				prefix + "startParty.. " + "starts a new party for players to join" + "\n" +
				prefix + "endParty.... " + "cancels the current party" + "\n" +
				prefix + "join........ " + "join the current party" + "\n" +
				prefix + "party....... " + "displays all members currently in the party" + "\n" +
				prefix + "startGame... " + "begins the game with current party members" + "\n" +
				prefix + "roleHelp... " + "Displays the help message for your role. Only works when in game" + "\n" +
				prefix + "help........ " + "displays this message" + "```";

		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("List of ToS Commands").setDescription(description).build();
		return embed;
	}

	private void endLobby(Long guildID, MessageChannel channelUsed, Member member)
	{
		if (!parties.containsKey(guildID))
			channelUsed.sendMessage("There is no party to end").queue();
		else if (!games.containsKey(guildID))
		{
			parties.remove(guildID);
			channelUsed.sendMessage("Party ended").queue();
		}
	}

	public void startLobby(JDA jda, Long guildID, MessageChannel channelUsed, Member partyLeader)
	{
		if (parties.containsKey(guildID))
			channelUsed.sendMessage("Party already started").queue();
		else if (games.containsKey(guildID))
			channelUsed.sendMessage("Can't start a party in a discord game!").queue();
		else
		{
			DiscordGame game = new DiscordGame(jda, guildID, partyLeader.getIdLong());
			channelUsed.sendMessage("Party started with game mode **Talking Graves**").queue();
			game.joinGame(partyLeader.getIdLong(), channelUsed);
			parties.put(guildID, game);
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
