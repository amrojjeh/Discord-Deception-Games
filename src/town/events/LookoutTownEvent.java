package town.events;

import java.util.ArrayList;

import town.DiscordGame;
import town.persons.Person;

public class LookoutTownEvent implements TownEvent
{
	private Person lookout;
	private Person target;
	private DiscordGame game;
	private ArrayList<Person> visitors = new ArrayList<>();

	public LookoutTownEvent(DiscordGame game, Person l, Person t)
	{
		this.game = game;
		lookout = l;
		target = t;
	}

	public Person getLookout()
	{
		return lookout;
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
		if (isVisitor(person))
			visitors.add(person);
	}

	@Override
	public void postDispatch()
	{
		if (visitors.size() == 1) lookout.sendMessage(String.format("No one visited <@%d>", target.getID()));
		else
		{
			StringBuilder message = new StringBuilder(String.format("You watched <@%d> over night, here are the visitors\n", target.getID()));
			visitors.forEach(person -> {if (person != lookout) message.append(String.format("- <@%d>\n", person.getID()));});
			lookout.sendMessage(message.toString());
		}
	}

	@Override
	public int getPriority()
	{
		return lookout.getType().getPriority();
	}
}
