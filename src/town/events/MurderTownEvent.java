package town.events;

import town.DiscordGame;
import town.persons.Person;
import town.phases.Night;

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
		if (person == getMurderer() && getGame().getCurrentPhase() instanceof Night)
			attackVictim(person);
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

	public void attackVictim(Person person)
	{
<<<<<<< HEAD
		//POSSIBLE ERROR: Account for doctor
		if(murderer.getType().getAttack() > victim.getType().getDefense()) {
			murderer.sendMessage("You attacked <@" + murderer.getID() + ">");
			victim.die(String.format("<@%d> was murdered by a serial killer.", victim.getID()));
		}
		else {
			murderer.sendMessage("You attacked <@" + murderer.getID() + ">");
		}
=======
		murderer.sendMessage("You killed <@" + victim.getID() + ">");
		victim.die(String.format("<@%d> was murdered by a serial killer.", victim.getID()));
>>>>>>> b2ce316c8a22594b4c170feff75b4ad566bb2f0d
	}
}
