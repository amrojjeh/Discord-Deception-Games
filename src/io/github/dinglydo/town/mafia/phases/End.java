package io.github.dinglydo.town.mafia.phases;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.phases.PhaseManager;

public class End extends Phase
{
	public End(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public Phase getNextPhase()
	{
		return new End(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 60;
	}

	@Override
	public void end()
	{
		delete();
	}

	public void delete()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "!delete").queue();
	}
}
