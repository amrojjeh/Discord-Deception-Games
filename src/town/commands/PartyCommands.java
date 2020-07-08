package town.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import town.DiscordGame;
import town.games.parser.Rule;
import town.persons.LobbyPerson;
import town.util.JavaHelper;

public class PartyCommands extends CommandSet
{
	public PartyCommands()
	{
		addCommand(false, PartyCommands::startGame, "startgame");
		addCommand(false, PartyCommands::joinGame, "join");
		addCommand(false, PartyCommands::leave, "leave");
		addCommand(true, PartyCommands::setGame, "setgame");
		addCommand(true, PartyCommands::noMin, "nomin");
		addCommand(true, PartyCommands::setRand, "setrand");
	}

	public static void startGame(DiscordGame game, Message message)
	{
		if (game.hasInitiated())
			message.getChannel().sendMessage("Game is already running!").queue();
		else if (game.getPlayersCache().isEmpty())
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
		else if (message.getMember().getIdLong() != game.partyLeaderID)
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", game.partyLeaderID)).queue();
		else if (game.getPlayersCache().size() < game.config.getMin())
			message.getChannel().sendMessage("Not enough players to play " + game.config.getGame().getName() + "! (" +
		(game.config.getMin() - game.getPlayersCache().size()) + " left to play)").queue();
		else
		{
			message.getChannel().sendMessage("Game has started! Creating server...").queue();
			game.createServer();
		}
	}

	public static void setGame(DiscordGame game, Message message)
	{
		if (message.getMember().getIdLong() != game.partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", game.partyLeaderID)).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ", 2);

		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + game.config.getPrefix() + "setGame 2` OR `" + game.config.getPrefix() + "setGame Mashup` OR\n```\nsetGame custom\n4 (Civilian, 3+), (Serial Killer, 1)```").queue();
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
			message.getChannel().sendMessage(game.config.setCustomGameMode(rules)).queue();
		}
		else
			message.getChannel().sendMessage(game.config.setGameMode(words[1])).queue();
	}

	public static void joinGame(DiscordGame game, Message userMessage)
	{
		MessageChannel channelUsed = userMessage.getChannel();
		long id = userMessage.getMember().getIdLong();
		if (game.hasInitiated())
		{
			String message = String.format("Can't join game until session is over <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (game.getPerson(id) != null)
		{
			String message = String.format("<@%d> already joined! Check party members with " + game.config.getPrefix() + "party", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		Rule rule = game.config.getGame().getClosestRule(game.getPlayersCache().size());

		if (!rule.hasDefault() && !game.config.isRandom() && rule.totalPlayers < game.getPlayersCache().size() + 1)
		{
			String message = String.format("<@%d> cannot join, as the closest rule, *%s*, has no defaults and thus can't support more players", id, rule.toString());
			channelUsed.sendMessage(message).queue();
			return;
		}


		game.getPlayersCache().add(new LobbyPerson(game, game.getPlayersCache().size() + 1, id));
		String message = String.format("<@%d> joined the lobby", id);
		channelUsed.sendMessage(message).queue();
	}

	public static void leave(DiscordGame game, Message userMessage)
	{
		long id = userMessage.getMember().getIdLong();
		MessageChannel channelUsed = userMessage.getChannel();
		if (game.hasInitiated())
		{
			String message = String.format("Can't leave a game after it has started. Leave the server instead. <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (game.getPerson(id) == null)
		{
			String message = String.format("Can't leave a game you're not in <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (id == game.partyLeaderID)
		{
			String message = String.format("Party leader can't leave the party. `" + game.config.getPrefix() + "endParty` instead <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		game.getPlayersCache().remove(game.getPerson(id));
		for (int x = 1; x <= game.getPlayersCache().size(); ++x)
			game.getPlayersCache().get(x - 1).setNum(x);

		String message = String.format("You've been removed from the party <@%d>", id);
		channelUsed.sendMessage(message).queue();

	}

	public static void noMin(DiscordGame game, Message message)
	{
		if (message.getMember().getIdLong() != game.partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", game.partyLeaderID)).queue();
			return;
		}

		String syntax = "Syntax is: " + game.config.getPrefix() + "nomin [0|1]";
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
			game.config.byPassMin(activator == 1);
			if (game.config.getMin() == 0) message.getChannel().sendMessage("No minimum players requried anymore.").queue();
			else message.getChannel().sendMessage("Default minimum players required.").queue();
		}
	}

	public static void setRand(DiscordGame game, Message message)
	{
		if (message.getMember().getIdLong() != game.partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", game.partyLeaderID)).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ");
		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + game.config.getPrefix() + "setRand [0|1]`");
			return;
		}

		Integer activator = JavaHelper.parseInt(words[1]);
		if (activator == null)
		{
			message.getChannel().sendMessage("Syntax is: `" + game.config.getPrefix() + "setRand [0|1]`");
			return;
		}

		game.config.setRandomMode(activator == 1);
		message.getChannel().sendMessage(game.config.setRandomMode(activator == 1)).queue();

	}
}
