package io.github.dinglydo.town.mafia.phases;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.phases.PhaseManager;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.util.RestHelper;

public class Night extends Phase
{
	public Night(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public void start()
	{
		getGame().getPlayersCache().forEach(person -> checkVictory(person));

		if (getGame().hasEnded())
			return;

		RestHelper.queueAll
		(
				getGame().toggleVC("Daytime", false),
				getGame().setChannelVisibility("player", "daytime_discussion", true, false)
		);

		getGame().getPlayersCache().forEach(person -> person.sendMessage("Night " + getGame().getDayNum() + " started"));
		getPhaseManager().setWarningToAll(5);
	}

	public void checkVictory(DiscordGamePerson person)
	{
		Role role = person.getRole();
		if (!role.hasWon(person) && role.canWin(person))
			role.win(person);
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
		getGame().startNextDay();
	}

	@Override
	public Phase getNextPhase()
	{
		return new Morning(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 40;
	}
}
