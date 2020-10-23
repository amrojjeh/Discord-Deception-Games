package town;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import town.commands.StartupCommands;

// This listens to discord messages and events. It also manages discord games.
public class MainListener extends ListenerAdapter
{
	private String prefix = "pg.";
	private ArrayList<GameParty> parties = new ArrayList<>();
	private ArrayList<DiscordGame> games = new ArrayList<>();
	private JDA jda;
	private final StartupCommands commands = new StartupCommands();

	public static void main(String[] args)
			throws InterruptedException
	{
		String token;
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
			MainListener ml = new MainListener();
			ml.jda = new JDABuilder(token).addEventListeners(ml).setActivity(Activity.playing("Try pg.help")).build();
		}
		catch (LoginException e)
		{
			System.out.println("Couldn't login: " + e);
			return;
		}
	}

	public boolean isChannelUsedForParty(TextChannel channel)
	{
		return isChannelUsedForParty(channel.getIdLong());
	}

	public boolean isChannelUsedForParty(long channelId)
	{
		for (GameParty gp : parties)
			if (gp.getChannelId() == channelId)
				return true;
		return false;
	}

	public boolean isGuildUsedForGame(Guild guild)
	{
		return isGuildUsedForGame(guild.getIdLong());
	}

	public boolean isGuildUsedForGame(long guildId)
	{
		for (DiscordGame game : games)
			if (game.getGuildId() == guildId)
				return true;
		return false;
	}

	public JDA getJDA()
	{
		return jda;
	}

	public void addGameParty(GameParty p)
	{
		parties.add(p);
	}

	public void addDiscordGame(DiscordGame dg, GameParty p)
	{
		parties.remove(p);
		games.add(dg);
	}

//	@Override
//	public void onGuildMemberJoin(GuildMemberJoinEvent event)
//	{
//		DiscordGame game = games.get(event.getGuild().getIdLong());
//		if (game != null)
//			games.get(event.getGuild().getIdLong()).gameGuildJoin(event.getMember());
//	}

	@Override
	public void onReady(ReadyEvent e)
	{
		System.out.println("Bot is ready to be used -- Refactored version");
		e.getJDA().getGuilds().forEach((guild) -> delete(guild));
	}

	public void delete(Guild guild)
	{
		if (guild.getOwner().getUser().isBot()) guild.delete().queue();
	}

//	@Override
//	public void onGuildJoin(GuildJoinEvent e)
//	{
//		System.out.println("Joined new guild");
//		Guild guild = e.getGuild();
//		String roleName = guild.getRoles().get(0).getName();
//		long oldGuildID = 0;
//		try
//		{
//			oldGuildID = Long.parseLong(roleName);
//		}
//		catch (NumberFormatException exception)
//		{
//			return;
//		}
//
//		DiscordGame game = parties.get(oldGuildID);
//		if (game == null)
//			return;
//		game.getNewGuild(guild);
//		games.put(guild.getIdLong(), game);
//		parties.remove(oldGuildID);
//	}

	// Bot should leave the game once the owner has changed
//	@Override
//	public void onGuildUpdateOwner(GuildUpdateOwnerEvent event)
//	{
//		DiscordGame game = games.get(event.getGuild().getIdLong());
//		if (game == null) return;
//		game.getGameGuild().leave().queue();
//		games.remove(game.getGameID());
//		parties.remove(game.getPartyID());
//	}

//	@Override
//	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
//	{
//		DiscordGame game = games.get(event.getGuild().getIdLong());
//		if (game == null) return;
//		game.gameGuildVoiceJoin(event.getMember(), event.getChannelJoined());
//	}

//	@Override
//	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
//	{
//		DiscordGame game = games.get(event.getGuild().getIdLong());
//		if (game == null) return;
//		game.gameGuildVoiceJoin(event.getMember(), event.getChannelJoined());
//	}


//	@Override
//	public void onGuildMemberLeave(GuildMemberLeaveEvent event)
//	{
//		DiscordGame game = games.get(event.getGuild().getIdLong());
//		if (game == null) return;
//		game.memberLeftGameGuild(event.getMember());
//	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{
		Message message = e.getMessage();

		// Ignore DMs
		if (e.isFromType(ChannelType.PRIVATE))
			return;

		commands.executeCommand(this, "pg.", message);

//		else if (lowerCaseMessage.contentEquals(prefix + "endparty"))
//			endLobby(e.getGuild().getIdLong(), e.getChannel(), message.getMember());
//		else if (lowerCaseMessage.startsWith(prefix + "help"))
//			e.getChannel().sendMessage(helpTable()).queue();
//		else if (lowerCaseMessage.contentEquals(prefix + "games"))
//				e.getChannel().sendMessage(displayGames()).queue();
//		else if (lowerCaseMessage.startsWith(prefix + "config"))
//			displayConfig(e.getChannel(), lowerCaseMessage, e.getGuild().getIdLong());
//		else if (lowerCaseMessage.contentEquals(prefix + "delete") || lowerCaseMessage.contentEquals("!delete"))
//		{
//			DiscordGame game = games.get(message.getGuild().getIdLong());
//			if (game == null) return;
//			if (!game.ended)
//			{
//				message.getChannel().sendMessage("Game hasn't ended yet. If you no longer want to play, leave the server.").queue();
//				return;
//			}
//			games.remove(message.getGuild().getIdLong());
//			game.deleteServer();
//		}
//		else if (lowerCaseMessage.contentEquals(prefix + "transfer") || lowerCaseMessage.contentEquals("!transfer"))
//		{
//			DiscordGame game = games.get(message.getGuild().getIdLong());
//			if (game == null) return;
//			if (!game.ended)
//			{
//				message.getChannel().sendMessage("Game hasn't ended yet. If you no longer want to play, leave the server.").queue();
//				return;
//			}
//			game.transferOrDelete(); // Game gets removed from games when ownership updates
//		}
//		else if (lowerCaseMessage.startsWith(prefix))
//		{
//			DiscordGame party = parties.get(e.getGuild().getIdLong());
//			DiscordGame game = games.get(e.getGuild().getIdLong());
//
//			if (party != null)
//				party.processMessage(e.getMessage());
//			else if (game != null)
//				game.processMessage(e.getMessage());
//			else
//				e.getChannel().sendMessage("Party hasn't been created yet. Do so with " + prefix + "startParty").queue();
//		}
//		else if (lowerCaseMessage.startsWith("!"))
//		{
//			DiscordGame game = games.get(e.getGuild().getIdLong());
//			if (game != null)
//				game.processMessage("!", e.getMessage());
//		}
	}

//	private MessageEmbed displayGames()
//	{
//		EmbedBuilder builder = new EmbedBuilder().setTitle("Party Games").setColor(Color.GREEN);
//		List<GameMode> gameModes = GameModeLoader.getGames(true);
//		for (int x = 1; x <= gameModes.size(); ++x)
//		{
//			GameMode game = gameModes.get(x - 1);
//			if (!game.isSpecial())
//				builder.addField(x + ". " + game.getName(), game.getDescription(), false);
//			else
//				builder.addField(x + ". " + game.getName() + " (Special)", game.getDescription(), false);
//		}
//		return builder.build();
//	}

//	private void displayConfig(MessageChannel channelUsed, String message, long guildID)
//	{
//		EmbedBuilder embed = new EmbedBuilder();
//		GameMode selectedGameMode;
//		String[] words = message.split(" ", 2);
//		if (words.length == 1)
//		{
//			if (!parties.containsKey(guildID) && !games.containsKey(guildID))
//			{
//				channelUsed.sendMessage("Syntax: pg.config [GAME_MODE]").queue();
//				return;
//			}
//
//			DiscordGame game;
//			if (parties.containsKey(guildID)) game = parties.get(guildID);
//			else game = games.get(guildID);
//
//			selectedGameMode = game.config.getGameMode();
//
//			boolean randomMode = game.config.isRandom();
//			boolean noMin = game.config.getMin() == 0;
//			long partyLeaderID = game.partyLeaderID;
//
//			embed
//			.addField("Random", (randomMode ? "Yes" : "No"), true)
//			.addField("Minimum Players", (noMin ? "0" : selectedGameMode.getMinimumTotalPlayers() + ""), true)
//			.addField("Party leader","<@" + partyLeaderID + ">", true);
//
//		}
//
//		else
//		{
//			selectedGameMode = GameModeLoader.getGameMode(words[1], true);
//			if (selectedGameMode == null)
//				channelUsed.sendMessage("FAILED: Game mode **" + words[1] + "** does not exist.").queue();
//		}
//
//			embed.setTitle(selectedGameMode.getName())
//				.setDescription(selectedGameMode.getDescription())
//				.setColor(Color.GREEN)
//				.addField("Game Config", selectedGameMode.getConfig(), true);
//
//		channelUsed.sendMessage(embed.build()).queue();
//	}

	private String helpTable()
	{
		String commands =
				"Lobby:\n" +
				"  " + prefix + "startParty | starts a new party for players to join\n" +
				"  " + prefix + "endParty   | cancels the current party\n" +
				"  " + prefix + "setgame    | changes the game mode\n" +
				"  " + prefix + "join       | join the current party\n" +
				"  " + prefix + "leave      | leaves the current party\n" +
				"  " + prefix + "party      | displays all members currently in the party\n" +
				"  " + prefix + "startGame  | begins the game with current party members\n" +
				"\nIn-game(can also use ! for prefix):\n" +
				"  " + prefix + "ability    | activates your role ability\n" +
				"  " + prefix + "cancel     | cancels your role ability\n" +
				"  " + prefix + "targets    | lists everyone you can use your ability on\n" +
				"\nAdvanced Lobby:\n" +
				"  " + prefix + "setrand    | removes the max amount of roles in a game\n" +
				"  " + prefix + "nomin      | sets the minimum players that can play to 0\n";

		return "```\n" + commands + "```";
	}

//	private void endLobby(Long guildID, MessageChannel channelUsed, Member member)
//	{
//		if (!parties.containsKey(guildID))
//			channelUsed.sendMessage("There is no party to end").queue();
//		else if (!games.containsKey(guildID))
//		{
//			parties.remove(guildID);
//			channelUsed.sendMessage("Party ended").queue();
//		}
//	}

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
