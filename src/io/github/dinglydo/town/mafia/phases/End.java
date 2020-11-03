package io.github.dinglydo.town.mafia.phases;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.phases.PhaseManager;
import io.github.dinglydo.town.util.RestHelper;

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
	public void start()
	{
		RestHelper.queueAll
		(
				getGame().setChannelVisibility("dead", "daytime_discussion", true, true),
				getGame().setChannelVisibility("player", "daytime_discussion", true, true),
				getGame().setChannelVisibility("player", "the_afterlife", true, true),
				getGame().sendMessageToTextChannel("daytime_discussion",
				"The game has ended! You can either `!delete` the server or `!transfer`" +
				" the server. In 60 seconds if no choice is made, the server will delete itself." +
				" (To transfer, the party leader must be in the server)")
		);
		getGame().getPlayersCache().forEach(player -> player.mute(false));
		getGame().openPrivateChannels();
	}

	@Override
	public void end()
	{
		delete();
	}

	public void transfer()
	{
		getPhaseManager().end();
		getGame().transferOrDelete();
	}

	public void delete()
	{
		getPhaseManager().end();
		getGame().sendMessageToTextChannel("daytime_discussion", "!delete").queue();
	}
}
