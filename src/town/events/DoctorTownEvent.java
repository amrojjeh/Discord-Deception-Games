package town.events;

import town.DiscordGame;
import town.persons.Doctor;
import town.persons.Person;

public class DoctorTownEvent implements TownEvent
{
	private Doctor doc;
	private Person target;
	private DiscordGame game;

	public DoctorTownEvent(DiscordGame game, Doctor d, Person t)
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
	public int getPriority()
	{
		return doc.getType().getPriority();
	}

	@Override
	public void standard(Person person)
	{
		if (target == person) {
			protect();
			if (person == doc)
				doc.selfHealed();
		}
	}

	public void protect()
	{
		target.setDefense(2);
	}

}