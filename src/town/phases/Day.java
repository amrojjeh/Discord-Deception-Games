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

	// begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		showDayChannels();
		getGame().getPlayers().forEach((person) -> checkVictory(person));
	}

	private void showDayChannels()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "Day started");
		getGame().setChannelVisibility("daytime_discussion", true, true);
		getGame().setChannelVisibility("Daytime", true, true);
	}

	public void checkVictory(Person person)
	{
		if (person.hasWon())
			person.win();
	}

	// ends the phase, sending out a global message of this fact.
	@Override
	public void end()
	{

	}

	// After Daytime, the Accusation phase begins.
	@Override
	public Phase getNextPhase()
	{
		return new Accusation(phaseManager);
	}

	//Duration: 50 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 20;
	}
}
