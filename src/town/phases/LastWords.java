package town.phases;

import town.persons.Person;

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
		getGame().sendMessageToTextChannel("daytime_discussion", String.format("What are your last words? <@%d>", defendant.getID()));
		getGame().muteExcept("Daytime", defendant);
		getGame().setChannelVisibility("daytime_discussion", true, false);
		getGame().setChannelVisibility(defendant, "daytime_discussion", true, true);
	}

	@Override
	public void end()
	{
		defendant.die();
		getGame().restoreTalking("Daytime");
		getGame().setChannelVisibility("daytime_discussion", true, true);
		getGame().resetVisibility(defendant, "daytime_discussion");
	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 10;
	}
}
