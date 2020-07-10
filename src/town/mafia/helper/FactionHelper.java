package town.mafia.helper;

import java.util.List;

import town.DiscordGame;
import town.persons.Person;
import town.roles.GameFaction;

public class FactionHelper
{
	public static boolean isTownLeft(DiscordGame game)
	{
		System.out.println(game.getCurrentPhase().getClass().getName() + " " + isFactionLeft(game, GameFaction.TOWN));
		return isFactionLeft(game, GameFaction.TOWN);
	}

	public static void townWin(DiscordGame game)
	{
		factionWin(game, GameFaction.TOWN);
	}

	public static boolean isSKLeft(DiscordGame game)
	{
		return isFactionLeft(game, GameFaction.SERIAL_KILLER);
	}

	public static void skWin(DiscordGame game)
	{
		factionWin(game, GameFaction.SERIAL_KILLER);
	}

	public static boolean isFactionLeft(DiscordGame game, GameFaction faction)
	{
		int townAlive = 0;
		List<Person> alivePlayers = game.getAlivePlayers();
		for (Person p : alivePlayers)
			if (p.getType().getFaction() == faction)
				++townAlive;
		return townAlive == alivePlayers.size();
	}

	public static void factionWin(DiscordGame game, GameFaction faction)
	{
		System.out.println("faction won");
		game.winTownFaction(faction);
		game.sendMessageToTextChannel("daytime_discussion", "**" + faction.getName() + " has won!**").queue();
		game.endGame();
	}
}
