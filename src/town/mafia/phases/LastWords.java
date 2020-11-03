package town.mafia.phases;

import town.discordgame.DiscordGame;
import town.persons.DiscordGamePerson;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.util.RestHelper;

public class LastWords extends Phase
{
	DiscordGamePerson defendant;

	public LastWords(DiscordGame game, PhaseManager pm, DiscordGamePerson defendant)
	{
		super(game, pm);
		this.defendant = defendant;
	}

	@Override
	public void start()
	{
		RestHelper.queueAll
		(
			getGame().sendMessageToTextChannel("daytime_discussion", String.format("What are your last words? <@%d>", defendant.getID())),
			getGame().getRole("player").muteAllInRole(true),
			getGame().getRole("defendant").muteAllInRole(false),
			getGame().setChannelVisibility("player", "daytime_discussion", true, false)
		);
	}

	@Override
	public void end()
	{
		defendant.die(String.format("<@%d> was lynched in the open.", defendant.getID()));
		getGame().sendMessageToTextChannel("daytime_discussion", "Their role was: " + defendant.getRole().getName())
		.queue();

		RestHelper.queueAll(getGame().getRole("player").muteAllInRole(false));
	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 6;
	}
}
