package town.phases;

import town.persons.Person;

public class Morning extends Phase
{
	Phase nextPhase = new Day(phaseManager);

	public Morning(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public void start()
	{
		if (getGame().peekDeathForMorning() == null)
			return;
		Person person = getGame().getDeathForMorning();
		getGame().sendMessageToTextChannel("daytime_discussion", person.getCauseOfDeath() + "\nTheir role was: " + person.getType().getName())
		.queue();
		if (getGame().peekDeathForMorning() != null)
			nextPhase = new Morning(phaseManager);
	}

	@Override
	public Phase getNextPhase()
	{
		return nextPhase;
	}

	@Override
	public int getDurationInSeconds()
	{
		return 4;
	}
}
