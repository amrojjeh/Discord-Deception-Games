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

	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "Day " + getGame().getDayNum() + " started");
		showDayChannels();
		getGame().getPlayers().forEach((person) -> checkVictory(person));
		phaseManager.setWarningInSeconds(5);
	}

	private void showDayChannels()
	{
		getGame().setChannelVisibility("daytime_discussion", true, true);
		getGame().setChannelVisibility("Daytime", true, true);
	}

	public void checkVictory(Person person)
	{
		if (!person.hasWon() && person.canWin())
			person.win();
	}

	@Override
	public Phase getNextPhase()
	{
		return new Accusation(phaseManager, 3);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 60;
	}
}
