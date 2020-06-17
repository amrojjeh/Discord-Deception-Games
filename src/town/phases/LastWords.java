package town.phases;

import net.dv8tion.jda.api.requests.RestAction;
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
		getGame().sendMessageToTextChannel("daytime_discussion", String.format("What are your last words? <@%d>", defendant.getID()))
		.flatMap(message -> getGame().muteExcept("Daytime", defendant))
		.flatMap(nothing -> getGame().removeReadExcept(defendant, "daytime_discussion"))
		.queue();
	}

	@Override
	public void end()
	{
		defendant.die(String.format("<@%d> was lynched in the open.", defendant.getID()));
		getGame().sendMessageToTextChannel("daytime_discussion", "Their role was: " + defendant.getType().getName())
		.queue();

		RestAction<?> action1 = getGame().resetPermissions(defendant, "daytime_discussion");
		RestAction<?> action2 = getGame().restoreTalking("Daytime", false);

		if (action1 != null) action1.queue();
		if (action2 != null) action2.queue();


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
		return 6;
	}
}
