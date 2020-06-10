package town.phases;

import town.persons.Person;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
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
		getGame().sendMessageToTextChannel("daytime_discussion", person.getCauseOfDeath());
		if (getGame().peekDeathForMorning() != null)
			nextPhase = new Morning(phaseManager);
	}

	// After Daytime, the Accusation phase begins.
	@Override
	public Phase getNextPhase()
	{
		return nextPhase;
	}

	//Duration: 50 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 4;
	}
}
