package io.github.dinglydo.town.commands;

import java.awt.Color;

import javax.annotation.Nonnull;

import io.github.dinglydo.town.DiscordGameConfig;
import io.github.dinglydo.town.MainListener;
import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.games.GameMode;
import io.github.dinglydo.town.party.Party;
import io.github.dinglydo.town.party.PartyIsEmptyException;
import io.github.dinglydo.town.party.PartyIsFullException;
import io.github.dinglydo.town.persons.LobbyPerson;
import io.github.dinglydo.town.util.JavaHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * The party commands are what store data relatied to viewing and configuring game settings before launching it.
 * @author Amr Ojjeh
 */
public class PartyCommands extends CommandSet<Party>
{
	/**
	 * The default PartyCommands constructor
	 */
	public PartyCommands()
	{
		addCommand(false, PartyCommands::startGame, "startgame");
		addCommand(false, PartyCommands::joinGame, "join", "in");
		addCommand(false, PartyCommands::leave, "leave");
		addCommand(true, PartyCommands::setGame, "setgame");
		addCommand(false, PartyCommands::displayParty, "party");
		addCommand(true, PartyCommands::displayConfig, "config");
		addCommand(true, PartyCommands::setRand, "setrand");
		addCommand(false, PartyCommands::endParty, "endparty");
	}

	/**
	 * The command which starts the game.
	 * @param p party
	 * @param message message
	 */
	public static void startGame(@Nonnull Party p, @Nonnull Message message)
	{
		if (p == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");
		DiscordGameConfig c = p.getConfig();

		if (p.getPlayersCache().isEmpty())
		{
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
			return;
		}

		LobbyPerson leader;

		try {
			leader = p.getGameLeader();
		} catch (PartyIsEmptyException e) {
			// This shouldn't ever be called since we check if there are players before getting leader
			e.panicInDiscord(message.getChannel());
			return;
		}

		if (message.getMember().getIdLong() != leader.getID())
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", leader.getID())).queue();
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

	/**
	 * The command that sets the game mode.
	 * @param party party
	 * @param message message
	 */
	public static void setGame(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		try {
			if (message.getMember().getIdLong() != party.getGameLeader().getID())
			{
				message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", party.getGameLeader().getID())).queue();
				return;
			}
		} catch (PartyIsEmptyException e) {
			e.panicInDiscord(message.getChannel());
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

	/**
	 * The command which joins a user into the party.
	 * @param party party
	 * @param message message
	 */
	public static void joinGame(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

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

	/**
	 * Command for leaving the party.
	 * @param party party
	 * @param userMessage message
	 */
	public static void leave(@Nonnull Party party, @Nonnull Message userMessage)
	{
		if (party == null || userMessage == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		long id = userMessage.getMember().getIdLong();
		MessageChannel channelUsed = userMessage.getChannel();

		// TODO: End party if everyone leaves

		try {
			party.leaveGame(userMessage.getMember());
		} catch (PartyIsEmptyException e) {
			e.printStackTrace();
			e.panicInDiscord(channelUsed);
			return;
		}

		String message;

		if (party.isPartyEmpty())
		{
			party.endParty();
			message = String.format("Everyone left. Ending party...");
		}
		else
			message = String.format("You've been removed from the party <@%d>", id);

		channelUsed.sendMessage(message).queue();
	}

	/**
	 * Command to display the party.
	 * @param party party
	 * @param message message
	 */
	public static void displayParty(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		MessageChannel channelUsed = message.getChannel();
		String description = "";
		String format = "%d. <@%d>\n";
		for (int x = 1; x <= party.getPlayersCache().size(); ++x)
		{
			LobbyPerson p = party.getPlayersCache().get(x - 1);
			description += String.format(format, x, p.getID());
		}
		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
	}

	/**
	 * Command to end the game party.
	 * @param party party
	 * @param message message
	 */
	public static void endParty(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		party.endParty();
		message.getChannel().sendMessage("Party ended").queue();
	}

	/**
	 * Command to display the game configuration.
	 * @param party party
	 * @param message message
	 */
	public static void displayConfig(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		String[] words = message.getContentRaw().split(" ", 2);

		// Let StartupCommands::displayConfig handle it
		if (words.length == 2)
			return;

		GameMode selectedGameMode = party.getConfig().getGameMode();

		boolean randomMode = party.getConfig().isRandom();
		boolean noMin = party.getConfig().getMin() == 0;
		long partyLeaderID = 0;
		try {
			partyLeaderID = party.getGameLeader().getID();
		} catch (PartyIsEmptyException e) {
			e.panicInDiscord(message.getChannel());
		}

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

	/**
	 * Command to set the game mode to be random.
	 * @param party party
	 * @param message message
	 */
	public static void setRand(@Nonnull Party party, @Nonnull Message message)
	{
		if (party == null || message == null)
			throw new IllegalArgumentException("Party or message cannot be null.");

		try {
			if (message.getMember().getIdLong() != party.getGameLeader().getID())
			{
				message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", party.getGameLeader().getID())).queue();
				return;
			}
		} catch (PartyIsEmptyException e) {
			e.panicInDiscord(message.getChannel());
		}

		String words[] = message.getContentRaw().split(" ");
		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + party.getPrefix() + "setRand [0|1]`");
			return;
		}

		Integer activator = JavaHelper.parseInt(words[1]);
		if (activator == null)
		{
			message.getChannel().sendMessage("Syntax is: `" + party.getPrefix() + "setRand [0|1]`");
			return;
		}

		message.getChannel().sendMessage(party.getConfig().setRandomMode(activator == 1)).queue();
	}
}
