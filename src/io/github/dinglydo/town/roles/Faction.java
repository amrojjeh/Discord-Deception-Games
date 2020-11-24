package io.github.dinglydo.town.roles;

import java.util.ArrayList;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;

public interface Faction
{
	String getName();

	String getCodeName();

	boolean canWin();

	void win();

	DiscordGame getGame();

	/**
	 * Whether the faction is the last one standing.
	 * @return Returns true if every alive player is part of the faction
	 */
	default boolean isFactionAlone()
	{
		return getPlayersAlive().length == getGame().getAlivePlayers().size();
	}

	default void factionWin()
	{
		getGame().getFactionManager().winTownFaction(this);
		getGame().sendMessageToTextChannel("daytime_discussion", "**" + getName() + " has won!**").queue();
		getGame().endGame();
	}

	ArrayList<DiscordGamePerson> getPlayers();

	default DiscordGamePerson[] getPlayersAlive()
	{
		return getPlayers().stream().filter(p -> p.isAlive()).toArray(DiscordGamePerson[]::new);
	}
}
