package town.phases;

import java.util.LinkedList;

import town.persons.Person;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class Morning extends Phase
{
	LinkedList<Person> deaths = null;

	public Morning(PhaseManager pm)
	{
		super(pm);
	}

	public Morning(PhaseManager pm, LinkedList<Person> deaths)
	{
		super(pm);
		this.deaths = deaths;
	}

	@Override
	public void start()
	{
		if (deaths != null)
			if (deaths.isEmpty())
				getGame().sendMessageToTextChannel("daytime_discussion", "No one one died last night.");
			else
				getGame().sendMessageToTextChannel("daytime_discussion", deaths.pop().getCauseOfDeath());
	}

	// After Daytime, the Accusation phase begins.
	@Override
	public Phase getNextPhase()
	{
		return new Day(phaseManager);
	}

	//Duration: 50 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 4;
	}
}
