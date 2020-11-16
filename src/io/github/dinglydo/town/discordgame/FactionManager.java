package io.github.dinglydo.town.discordgame;

import java.util.ArrayList;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.github.dinglydo.town.roles.Faction;

public class FactionManager
{
	private final ArrayList<Faction> globalFactions = new ArrayList<>();
	private final DiscordGame game;

	public FactionManager(DiscordGame game)
	{
		this.game = game;
	}

	public DiscordGame getGame()
	{
		return game;
	}

	public void addGlobalFaction(Faction faction)
	{
		if (getGlobalFaction(faction.getCodeName()) != null)
			throw new IllegalArgumentException("Faction already exists");
		globalFactions.add(faction);
	}

	public ArrayList<Faction> getGlobalFactions()
	{
		return globalFactions;
	}

	@Nullable
	public Faction getGlobalFaction(String codeName)
	{
		for (Faction f : getGlobalFactions())
		{
			if (f.getCodeName().contentEquals(codeName))
				return f;
		}
		return null;
	}

	public Faction getOrAddGlobalFaction(String codeName, Function<DiscordGame, Faction> factionGetter)
	{
		Faction faction = getGlobalFaction(codeName);
		if (faction != null)
			return faction;
		faction = factionGetter.apply(getGame());
		addGlobalFaction(faction);
		return faction;
	}
}
