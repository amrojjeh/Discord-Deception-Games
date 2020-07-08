package town;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
import town.games.GameMode;
import town.games.GameModeLoader;


public class MainListener extends ListenerAdapter
{
	String prefix = "pg.";

	HashMap<Long, DiscordGame> parties = new HashMap<>();
	HashMap<Long, DiscordGame> games = new HashMap<>();

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
			new JDABuilder(token).addEventListeners(new MainListener()).setActivity(Activity.playing("Try pg.help")).build();
		}
		catch (LoginException e)
		{
			System.out.println("Couldn't login: " + e);
			return;
		}
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

		if (lowerCaseMessage.startsWith(prefix + "startparty"))
			startLobby(e.getJDA(), e.getGuild().getIdLong(), e.getMessage().getContentRaw(), e.getChannel(), e.getMember());
		else if (lowerCaseMessage.contentEquals(prefix + "endparty"))
			endLobby(e.getGuild().getIdLong(), e.getChannel(), message.getMember());
		else if (lowerCaseMessage.contentEquals(prefix + "help")) {
			e.getChannel().sendMessage(helpTable()).queue();
		}
		else if (lowerCaseMessage.contentEquals(prefix + "games"))
				e.getChannel().sendMessage(displayGames()).queue();
		else if (lowerCaseMessage.startsWith(prefix + "config"))
			displayConfig(e.getChannel(), lowerCaseMessage, e.getGuild().getIdLong());
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
				e.getChannel().sendMessage("Party hasn't been created yet. Do so with " + prefix + "startParty").queue();
		}
		else if (lowerCaseMessage.startsWith("!"))
		{
			DiscordGame game = games.get(e.getGuild().getIdLong());
			if (game != null)
				game.processMessage("!", e.getMessage());
		}
	}

	private MessageEmbed displayGames()
	{
		EmbedBuilder builder = new EmbedBuilder().setTitle("Party Games").setColor(Color.GREEN);
		List<GameMode> gameModes = GameModeLoader.getGames(true);
		for (int x = 1; x <= gameModes.size(); ++x)
		{
			GameMode game = gameModes.get(x - 1);
			if (!game.isSpecial())
				builder.addField(x + ". " + game.getName(), game.getDescription(), false);
			else
				builder.addField(x + ". " + game.getName() + " (Special)", game.getDescription(), false);
		}
		return builder.build();
	}

	private void displayConfig(MessageChannel channelUsed, String message, long guildID)
	{
		EmbedBuilder embed = new EmbedBuilder();
		GameMode selectedGameMode;
		String[] words = message.split(" ", 2);
		if (words.length == 1)
		{
			if (!parties.containsKey(guildID) && !games.containsKey(guildID))
			{
				channelUsed.sendMessage("Syntax: pg.config [GAME_MODE]").queue();
				return;
			}

			DiscordGame game;
			if (parties.containsKey(guildID)) game = parties.get(guildID);
			else game = games.get(guildID);

			selectedGameMode = game.config.getGame();

			boolean randomMode = game.config.isRandom();
			boolean noMin = game.config.getMin() == 0;
			long partyLeaderID = game.partyLeaderID;

			embed
			.addField("Random", (randomMode ? "Yes" : "No"), true)
			.addField("Minimum Players", (noMin ? "0" : selectedGameMode.getMinimumTotalPlayers() + ""), true)
			.addField("Party leader","<@" + partyLeaderID + ">", true);

		}

		else
		{
			selectedGameMode = GameModeLoader.getGameMode(words[1], true);
			if (selectedGameMode == null)
				channelUsed.sendMessage("FAILED: Game mode **" + words[1] + "** does not exist.").queue();
		}

			embed.setTitle(selectedGameMode.getName())
				.setDescription(selectedGameMode.getDescription())
				.setColor(Color.GREEN)
				.addField("Game Config", selectedGameMode.getConfig(), true);

		channelUsed.sendMessage(embed.build()).queue();
	}

	private String helpTable()
	{
		String commands =
				"Lobby:\n" +
				"  " + prefix + "startParty | starts a new party for players to join\n" +
				"  " + prefix + "endParty   | cancels the current party\n" +
				"  " + prefix + "join       | join the current party\n" +
				"  " + prefix + "party      | displays all members currently in the party\n" +
				"  " + prefix + "startGame  | begins the game with current party members\n" +
				"\nGame commands (can also use ! for prefix):\n" +
				"  " + prefix + "ability    | activates your role ability\n" +
				"  " + prefix + "targets    | lists everyone you can use your ability on\n" +
				"  " + prefix + "roleHelp   | displays the help message for your role.\n";

		return "```\n" + commands + "```";
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

	public void startLobby(JDA jda, Long guildID, String message, MessageChannel channelUsed, Member partyLeader)
	{
		if (parties.containsKey(guildID))
			channelUsed.sendMessage("Party already started").queue();
		else if (games.containsKey(guildID))
			channelUsed.sendMessage("Can't start a party in a discord game!").queue();
		else
		{
			DiscordGame game = new DiscordGame(jda, guildID, partyLeader.getIdLong());
			String[] words = message.split(" ", 2);
			String messageToSend = "Party started\n";
			String customRules = "";
			String gameName = game.config.getGame().getName();

			// words[0] = pg.startparty
			// words[1] = Talking Graves Rand
			if (words.length == 2)
			{
				if (words[1].contains("custom"))
				{
					String[] lines = message.split("\n", 2);
					if (lines.length == 2)
						customRules = lines[1];
					else
					{
						channelUsed.sendMessage("When passing a custom game, each line has to be seperated. Example:\n```\nstartParty custom\n4 (Civilian, 3+), (Serial Killer, 1)\n6 (Civilian, 4+), (Serial Killer, 2)```").queue();
						return;
					}
				}
				else
					gameName = words[1];
			}

			if (!customRules.isBlank())
				messageToSend += game.config.setCustomGameMode(customRules) + "\n";
			else
				messageToSend += game.config.setGameMode(gameName) + "\n";
			channelUsed.sendMessage(messageToSend).queue();
			if (messageToSend.contains("FAILED")) return;

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
