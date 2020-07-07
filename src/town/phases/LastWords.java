package town.phases;

import town.persons.Person;
import town.util.RestHelper;

public class LastWords extends Phase
{
	Person defendant;

	public LastWords(PhaseManager pm, Person defendant)
	{
		super(pm);
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
		return new Night(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 6;
	}
}
