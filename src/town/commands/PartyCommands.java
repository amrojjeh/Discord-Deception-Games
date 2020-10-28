package town.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import town.DiscordGame;
import town.DiscordGameConfig;
import town.GameParty;
import town.MainListener;
import town.PartyIsEmptyException;
import town.PartyIsFullException;
import town.games.GameMode;
import town.persons.LobbyPerson;
import town.util.JavaHelper;

public class PartyCommands extends CommandSet<GameParty>
{
	public PartyCommands()
	{
		addCommand(false, PartyCommands::startGame, "startgame");
		addCommand(false, PartyCommands::joinGame, "join");
		addCommand(false, PartyCommands::leave, "leave");
		addCommand(true, PartyCommands::setGame, "setgame");
		addCommand(false, PartyCommands::displayParty, "party");
		addCommand(true, PartyCommands::displayConfig, "config");
		addCommand(true, PartyCommands::noMin, "nomin");
		addCommand(true, PartyCommands::setRand, "setrand");
	}

	public static void startGame(GameParty p, Message message)
	{
		DiscordGameConfig c = p.getConfig();

		if (p.getPlayersCache().isEmpty())
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
		else if (message.getMember().getIdLong() != p.getGameLeader().getID())
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", p.getGameLeader())).queue();
		else if (p.getPlayersCache().size() < c.getMin())
			message.getChannel().sendMessage("Not enough players to play " + c.getGameMode().getName() + "! (" +
		(c.getMin() - p.getPlayersCache().size()) + " left to play)").queue();
		else
		{
			message.getChannel().sendMessage("Game has started! Creating server...").queue();
			DiscordGame game = DiscordGame.createServer(p, message.getIdLong());
			// Add game to main listener
			JDA jda = message.getJDA();
			MainListener ml = null;
			for (Object obj : jda.getRegisteredListeners())
				if (obj instanceof MainListener)
					ml = (MainListener)obj;
			ml.addDiscordGame(game, p);
		}
	}

	public static void setGame(GameParty party, Message message)
	{
		if (message.getMember().getIdLong() != party.getGameLeader().getID())
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", party.getGameLeader().getID())).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ", 2);

		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + party.getPrefix() + "setGame 2` OR `" + party.getPrefix() + "setGame Mashup` OR\n```\nsetGame custom\n4 (Civilian, 3+), (Serial Killer, 1)```").queue();
			return;
		}

		if (words[1].contains("custom"))
		{
			String[] lines = message.getContentRaw().split("\n", 2);
			if (lines.length != 2)
			{
				message.getChannel().sendMessage("When passing a custom game, each line has to be seperated. Example:\n```\nsetGame custom\n4 (Civilian, 3+), (Serial Killer, 1)\6 (Civilian, 4+), (Serial Killer, 2)```").queue();
				return;
			}

			String rules = lines[1];
			message.getChannel().sendMessage(party.getConfig().setCustomGameMode(rules)).queue();
		}
		else
			message.getChannel().sendMessage(party.getConfig().setGameMode(words[1])).queue();
	}

	public static void joinGame(GameParty party, Message message)
	{
		MessageChannel channelUsed = message.getChannel();
		long id = message.getMember().getIdLong();
		if (party.hasPersonJoined(message.getMember()))
		{
			String messageToSend = String.format("<@%d> already joined! Check party members with " + party.getPrefix() + "party", id);
			channelUsed.sendMessage(messageToSend).queue();
			return;
		}

		if (party.isPartyFull())
		{
			channelUsed.sendMessage("Lobby is full! No more players can join unless the game mode changes or defaults are added.").queue();
			return;
		}

		try {
			party.joinGame(message.getMember());
		} catch (PartyIsFullException e) {
			e.printStackTrace();
			e.panicInDiscord(channelUsed);
			return;
		}
		String messageToSend = String.format("<@%d> joined the lobby", id);
		channelUsed.sendMessage(messageToSend).queue();
	}

	public static void leave(GameParty party, Message userMessage)
	{
		long id = userMessage.getMember().getIdLong();
		MessageChannel channelUsed = userMessage.getChannel();

		// TODO: Game leader should transfer
		if (id == party.getGameLeader().getID())
		{
			String message = String.format("Party leader can't leave the party. `" + party.getPrefix() + "endParty` instead <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		// TODO: End party if everyone leaves

		try {
			party.leaveGame(userMessage.getMember());
		} catch (PartyIsEmptyException e) {
			e.printStackTrace();
			e.panicInDiscord(channelUsed);
			return;
		}

		String message = String.format("You've been removed from the party <@%d>", id);
		channelUsed.sendMessage(message).queue();
	}

	public static void displayParty(GameParty game, Message message)
	{
		MessageChannel channelUsed = message.getChannel();
		String description = "";
		String format = "%d. <@%d> ";
		for (int x = 1; x <= game.getPlayersCache().size(); ++x)
		{
			LobbyPerson p = game.getPlayersCache().get(x - 1);
			description += String.format(format, x, p.getID());
		}
		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
	}

	public static void endParty(GameParty game, Message message)
	{
		game.registerAsListener(false);
		game.getMainListener().endParty(game);
		message.getChannel().sendMessage("Party ended").queue();
	}

	public static void displayConfig(GameParty party, Message message)
	{
		String[] words = message.getContentRaw().split(" ", 2);

		// Let StartupCommands::displayConfig handle it
		if (words.length == 2)
			return;

		GameMode selectedGameMode = party.getConfig().getGameMode();

		boolean randomMode = party.getConfig().isRandom();
		boolean noMin = party.getConfig().getMin() == 0;
		long partyLeaderID = party.getGameLeader().getID();

		EmbedBuilder embed = new EmbedBuilder();
		embed
		.setTitle(selectedGameMode.getName())
		.setDescription(selectedGameMode.getDescription())
		.setColor(Color.GREEN)
		.addField("Random", (randomMode ? "Yes" : "No"), true)
		.addField("Minimum Players", (noMin ? "0" : selectedGameMode.getMinimumTotalPlayers() + ""), true)
		.addField("Party leader","<@" + partyLeaderID + ">", true)
		.addField("Game Config", selectedGameMode.getConfig(), true);

		message.getChannel().sendMessage(embed.build()).queue();
	}

	public static void noMin(GameParty party, Message message)
	{
		if (message.getMember().getIdLong() != party.getGameLeader().getID())
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", party.getGameLeader().getID())).queue();
			return;
		}

		String syntax = "Syntax is: " + party.getPrefix() + "nomin [0|1]";
		String words[] = message.getContentRaw().split(" ");
		int activator = 0;
		if (words.length != 2) message.getChannel().sendMessage(syntax).queue();
		else
		{
			try
			{
				activator = Integer.parseInt(words[1]);
			}
			catch (NumberFormatException e)
			{
				message.getChannel().sendMessage(syntax).queue();
				return;
			}
			party.getConfig().byPassMin(activator == 1);
			if (party.getConfig().getMin() == 0) message.getChannel().sendMessage("No minimum players requried anymore.").queue();
			else message.getChannel().sendMessage("Default minimum players required.").queue();
		}
	}

	public static void setRand(GameParty game, Message message)
	{
		if (message.getMember().getIdLong() != game.getGameLeader().getID())
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", game.getGameLeader().getID())).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ");
		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + game.getPrefix() + "setRand [0|1]`");
			return;
		}

		Integer activator = JavaHelper.parseInt(words[1]);
		if (activator == null)
		{
			message.getChannel().sendMessage("Syntax is: `" + game.getPrefix() + "setRand [0|1]`");
			return;
		}

		message.getChannel().sendMessage(game.getConfig().setRandomMode(activator == 1)).queue();
	}
}
