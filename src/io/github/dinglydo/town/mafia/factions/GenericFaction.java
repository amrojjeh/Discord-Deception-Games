//package io.github.dinglydo.town.mafia.factions;
//
//import io.github.dinglydo.town.discordgame.DiscordGame;
//import io.github.dinglydo.town.roles.Faction;
//
//public class GenericFaction implements Faction
//{
//	private final DiscordGame game;
//	private final String name;
//
//	public GenericFaction(DiscordGame game, String name)
//	{
//		this.game = game;
//		this.name = name;
//	}
//
//	@Override
//	public String getName()
//	{
//		return name;
//	}
//
//	@Override
//	public boolean canWin()
//	{
//		System.out.println(game.getCurrentPhase().getClass().getName() + " " + isFactionLeft());
//		return isFactionLeft();
//	}
//
//	@Override
//	public void win()
//	{
//		factionWin();
//	}
//
//	@Override
//	public DiscordGame getGame()
//	{
//		return game;
//	}
//}
