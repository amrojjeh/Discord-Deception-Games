package town.phases;

import town.DiscordGame;
import town.util.RestHelper;

public class End extends Phase
{
	public End(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public Phase getNextPhase()
	{
		return new End(getGame(), phaseManager);
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
		phaseManager.end();
		getGame().transferOrDelete();
	}

	public void delete()
	{
		phaseManager.end();
		getGame().sendMessageToTextChannel("daytime_discussion", "!delete").queue();
	}
}
