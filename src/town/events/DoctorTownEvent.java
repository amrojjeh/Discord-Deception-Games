package town.events;

import java.util.ArrayList;

import town.DiscordGame;
import town.persons.Person;
import town.phases.Night;

public class DoctorTownEvent implements TownEvent
{
	private Person doc;
	private Person target;
	private DiscordGame game;

	public DoctorTownEvent(DiscordGame game, Person d, Person t)
	{
		this.game = game;
		doc = d;
		target = t;
	}

	public Person getDoctor()
	{
		return doc;
	}

	@Override
	public Person getTarget()
	{
		return target;
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public void standard(Person person)
	{
		if (person.getEvent() != null && getGame().getCurrentPhase() instanceof Night) {
			target.getType().setDefense(2);
			//TODO: Take away the person's defense at night
		}
	}

	@Override
	public int getPriority()
	{
		return doc.getType().getPriority();
	}

}