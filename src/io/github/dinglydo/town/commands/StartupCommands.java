package io.github.dinglydo.town.commands;

import java.awt.Color;
import java.util.ArrayList;

import io.github.dinglydo.town.MainListener;
import io.github.dinglydo.town.games.GameMode;
import io.github.dinglydo.town.games.GameModeLoader;
import io.github.dinglydo.town.party.Party;
import io.github.dinglydo.town.party.PartyIsFullException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class StartupCommands extends CommandSet<MainListener>
{
	public StartupCommands()
	{
		addCommand(true, StartupCommands::startLobby, "startparty");
		addCommand(false, StartupCommands::displayGames, "games");
		addCommand(true, StartupCommands::displayConfig, "config");
	}

	public static void startLobby(MainListener ml, Message message)
	{
		System.out.println("Starting party");
		MessageChannel channelUsed = message.getChannel();

		if (ml.getGamePartyFromMessage(message) != null)
			channelUsed.sendMessage("Party already started").queue();
		else if (ml.getDiscordGameFromMessage(message) != null)
			channelUsed.sendMessage("Can't start a party in a discord game!").queue();
		else
		{
			Party party = Party.createParty(ml, message.getTextChannel(), message.getMember());
			String[] words = message.getContentRaw().split(" ", 2);
			String messageToSend = "Party started\n";
			// TODO: Change custom parsers
			String gameName = party.getConfig().getGameMode().getName();

			// words[0] = pg.startparty
			// words[1] = Talking Graves Rand
			if (words.length == 2)
					gameName = words[1];
			messageToSend += party.getConfig().setGameMode(gameName) + "\n";

			channelUsed.sendMessage(messageToSend).queue();
			if (messageToSend.contains("FAILED"))
			{
				party.registerAsListener(false);
				return;
			}

			ml.addGameParty(party);
			try {
				// TODO: Make him game leader automatically when he first joins
				party.joinGame(message.getMember());
				party.setGameLeader(party.getPerson(message.getMember()));
			} catch (PartyIsFullException e) {
				e.panicInDiscord(channelUsed);
			}
		}
	}

	public static void displayGames(MainListener ml, Message message)
	{
		EmbedBuilder builder = new EmbedBuilder().setTitle("Party Games").setColor(Color.GREEN);
		ArrayList<GameMode> gameModes = GameModeLoader.getGames(true);
		for (int x = 1; x <= gameModes.size(); ++x)
		{
			GameMode game = gameModes.get(x - 1);
			if (!game.isSpecial())
				builder.addField(x + ". " + game.getName(), game.getDescription(), false);
			else
				builder.addField(x + ". " + game.getName() + " (Special)", game.getDescription(), false);
		}
		message.getChannel().sendMessage(builder.build()).queue();
	}

	public static void displayConfig(MainListener ml, Message message)
	{
		String[] words = message.getContentRaw().split(" ", 2);
		// Let PartyCommands::displayConfig handle it
		if (words.length == 1)
		{
			if (ml.getGamePartyFromMessage(message) == null)
				message.getChannel().sendMessage("Syntax is: `pg.config [GAME_MODE]`").queue();
			return;
		}

		GameMode selectedGameMode = GameModeLoader.getGameMode(words[1], true);
		if (selectedGameMode == null)
		{
			message.getChannel().sendMessage("FAILED: Game mode **" + words[1] + "** does not exist.").queue();
			return;
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(selectedGameMode.getName())
		.setDescription(selectedGameMode.getDescription())
		.setColor(Color.GREEN)
		.addField("Game Config", selectedGameMode.getConfig(), true);

		message.getChannel().sendMessage(embed.build()).queue();
	}
}
