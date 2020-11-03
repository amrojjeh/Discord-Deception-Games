package town;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import town.commands.StartupCommands;
import town.discordgame.DiscordGame;

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
			ml.jda = JDABuilder.create(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
					.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setChunkingFilter(ChunkingFilter.ALL)
					.addEventListeners(ml)
					.setActivity(Activity.playing("Try pg.help"))
					.build();
		}
		catch (LoginException e)
		{
			System.out.println("Couldn't login: " + e);
			return;
		}
	}

	@Nullable
	public GameParty getGamePartyFromMessage(@Nonnull Message message)
	{
		if (message == null) throw new NullPointerException("Message cannot be null");
		long id = message.getChannel().getIdLong();
		for (GameParty gp : parties)
		{
			if (gp.getChannelId() == id)
				return gp;
		}
		return null;
	}

	public DiscordGame getDiscordGameFromMessage(@Nonnull Message message)
	{
		if (message == null) throw new NullPointerException("Message cannot be null");
		long id = message.getGuild().getIdLong();
		for (DiscordGame g : games)
		{
			if (g.getGuildId() == id)
				return g;
		}
		return null;
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
		games.add(dg);
		endParty(p);
	}

	public void endParty(GameParty party)
	{
		party.registerAsListener(false);
		parties.remove(party);
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
		for (Guild g : e.getJDA().getGuilds())
			delete(g);
	}

	public void delete(Guild guild)
	{
		Member member = guild.getOwner();
		if (member == null) guild.retrieveOwner().queue(m ->
		{
			if (m.getIdLong() == jda.getSelfUser().getIdLong()) guild.delete().queue();
		});
		else if (member.getIdLong() == jda.getSelfUser().getIdLong()) guild.delete().queue();
	}

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

//		else if (lowerCaseMessage.startsWith(prefix + "help"))
//			e.getChannel().sendMessage(helpTable()).queue();
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
	}

	// TODO: Add multiple help tables
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
