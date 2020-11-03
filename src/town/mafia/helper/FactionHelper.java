package town.mafia.helper;

import town.discordgame.DiscordGame;
import town.roles.Faction;

public class FactionHelper
{
	public static boolean isTownLeft(DiscordGame game)
	{
//		System.out.println(game.getCurrentPhase().getClass().getName() + " " + isFactionLeft(game, Faction.TOWN));
//		return isFactionLeft(game, Faction.TOWN);
		throw new UnsupportedOperationException();
	}

	public static void townWin(DiscordGame game)
	{
		factionWin(game, Faction.TOWN);
	}

	public static boolean isSKLeft(DiscordGame game)
	{
		return isFactionLeft(game, Faction.SERIAL_KILLER);
	}

	public static void skWin(DiscordGame game)
	{
		factionWin(game, Faction.SERIAL_KILLER);
	}

	public static boolean isFactionLeft(DiscordGame game, Faction faction)
	{
		int townAlive = 0;
//		List<Person> alivePlayers = game.getAlivePlayers();
//		for (Person p : alivePlayers)
//			if (p.getType().getFaction() == faction)
//				++townAlive;
//		return townAlive == alivePlayers.size();
		throw new UnsupportedOperationException();
	}

	public static void factionWin(DiscordGame game, Faction faction)
	{
		System.out.println("faction won");
//		game.winTownFaction(faction);
//		game.sendMessageToTextChannel("daytime_discussion", "**" + faction.getName() + " has won!**").queue();
//		game.endGame();
		throw new UnsupportedOperationException();
	}
}
