package town.events;

import java.util.ArrayList;

import town.DiscordGame;
import town.mafia.persons.Doctor;
import town.persons.Person;

public class DoctorTownEvent implements TownEvent
{
	private Doctor doc;
	private Person target;
	private DiscordGame game;
	private ArrayList<Person> visitors = new ArrayList<>();

	public DoctorTownEvent(DiscordGame game, Doctor d, Person t)
	{
		this.game = game;
		doc = d;
		target = t;
	}

	public Doctor getDoctor()
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
		if (isVisitor(person) && person.getEvent() instanceof MurderTownEvent)
			visitors.add(person);
		else if (target == person)
		{
			protect();
			if (person == doc)
				doc.selfHealed();
		}
	}

	@Override
	public void postDispatch()
	{
		if (visitors.size() == 0) doc.sendMessage(String.format("No one attacked <@%d>.", target.getID()));
		else doc.sendMessage(String.format("Your target, <@%d>, was attacked.", target.getID()));
	}

	public void protect()
	{
		target.setDefense(2);
	}
}