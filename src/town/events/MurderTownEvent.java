package town.events;

import town.DiscordGame;
import town.persons.Person;

public class MurderTownEvent implements TownEvent
{
	private Person murderer;
	private Person victim;
	private DiscordGame game;

	public MurderTownEvent(DiscordGame game, Person m, Person v)
	{
		this.game = game;
		murderer = m;
		victim = v;
	}

	public Person getMurderer()
	{
		return murderer;
	}

	public Person getVictim()
	{
		return victim;
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public void standard(Person person)
	{
		if (person == getMurderer())
			attackVictim();
	}

	@Override
	public Person getTarget()
	{
		return victim;
	}

	@Override
	public int getPriority()
	{
		return murderer.getType().getPriority();
	}

	public void attackVictim()
	{
		murderer.sendMessage("You attacked <@" + murderer.getID() + ">");
		if(murderer.getAttack() > victim.getDefense())
			victim.die(String.format("<@%d> (%d) was murdered by a serial killer.", victim.getID(), victim.getNum()));
	}
}
