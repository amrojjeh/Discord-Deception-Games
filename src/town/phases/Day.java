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
		getGame().sendMessageToTextChannel("daytime_discussion", "Day " + getGame().getDayNum() + " started")
		.flatMap(msg -> getGame().setChannelVisibility("daytime_discussion", true, true))
		.flatMap(perm -> getGame().setChannelVisibility("Daytime", true, true))
		.queue();

		getGame().getPlayers().forEach((person) -> checkVictory(person));
		phaseManager.setWarningInSeconds(5);
	}

	public void checkVictory(Person person)
	{
		if (!person.hasWon() && person.canWin())
			person.win();
	}

	@Override
	public Phase getNextPhase()
	{
		if (getGame().getAlivePlayers().size() > 2)
		return new Accusation(phaseManager, 3);
		else
		{
			getGame().sendMessageToTextChannel("daytime_discussion", "Accusation was skipped because there were only two people.").queue();
			return new Night(phaseManager);
		}
	}

	@Override
	public int getDurationInSeconds()
	{
		return 80;
	}
}
