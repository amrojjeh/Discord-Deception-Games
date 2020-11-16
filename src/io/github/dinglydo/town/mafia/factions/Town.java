package io.github.dinglydo.town.mafia.factions;

import java.util.ArrayList;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.Faction;

public class Town implements Faction
{
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> players;

	public Town(DiscordGame game)
	{
		this.game = game;
		players = new ArrayList<>();
	}

	@Override
	public String getName()
	{
		return "Town";
	}

	@Override
	public String getCodeName()
	{
		return "TOWN";
	}

	@Override
	public boolean canWin()
	{
		return isFactionAlone();
	}

	@Override
	public void win()
	{
		factionWin();
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public ArrayList<DiscordGamePerson> getPlayers()
	{
		return players;
	}
}
