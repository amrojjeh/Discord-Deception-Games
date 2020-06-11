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
		getGame().removeReadExcept(defendant, "daytime_discussion");
	}

	@Override
	public void end()
	{
		defendant.die(String.format("<@%d> was lynched in the open.", defendant.getID()));
		getGame().restoreTalking("Daytime");
		getGame().restoreRead(defendant, "daytime_discussion");
		getGame().getPlayers().forEach(person -> checkVictory(person));
	}

	public void checkVictory(Person person)
	{
		if (!person.hasWon() && person.canWin())
			person.win();
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
