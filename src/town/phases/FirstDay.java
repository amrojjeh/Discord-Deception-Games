package town.phases;

import town.persons.Person;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class FirstDay extends Day
{
	public FirstDay(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public void checkVictory(Person person) {	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(phaseManager);
	}

	//Duration: 50 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 40;
	}
}
