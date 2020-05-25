package town.phases;

import town.persons.Person;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class Day extends Phase
{
	public Day(PhaseManager pm)
	{
		super(pm);
	}

	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("system", "Day started");
		getGame().getPlayers().forEach((person) -> checkVictory(person));
	}

	public void checkVictory(Person person)
	{
		if (person.hasWon())
		{
			getGame().sendMessageToTextChannel("system", person.getNickName() + " has won");
			person.win();
		}
	}

	//ends the phase, sending out a global message of this fact.
	@Override
	public void end()
	{
		//		System.out.println("Ending day...");
	}

	//After Daytime, the Accusation phase begins.
	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Night(pm);
	}

	//Duration: 50 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}