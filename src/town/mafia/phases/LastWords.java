package town.mafia.phases;

import town.DiscordGame;
import town.persons.Person;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.util.RestHelper;

public class LastWords extends Phase
{
	Person defendant;

	public LastWords(DiscordGame game, PhaseManager pm, Person defendant)
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
			getGame().muteAllInRole("player", true),
			getGame().muteAllInRole("defendant", false),
			getGame().setChannelVisibility("player", "daytime_discussion", true, false)
		);
	}

	@Override
	public void end()
	{
		defendant.die(String.format("<@%d> was lynched in the open.", defendant.getID()));
		getGame().sendMessageToTextChannel("daytime_discussion", "Their role was: " + defendant.getType().getName())
		.queue();

		RestHelper.queueAll(getGame().muteAllInRole("player", false));
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
