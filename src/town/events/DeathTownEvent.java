package town.events;

import net.dv8tion.jda.api.JDA;
import town.DiscordGame;
import town.persons.Person;

public class DeathTownEvent implements TownEvent
{
	private Person dead;
	DiscordGame game;
	
	public DeathTownEvent(DiscordGame game, Person dead)
	{
		this.game = game;
		this.dead = dead;
	}
	
	public Person getDeadPerson()
	{
		return dead;
	}
	
	@Override
	public DiscordGame getGame() 
	{
		return game;
	}
	
	@Override
	public JDA getJDA() 
	{
		return game.getJDA();
	}

	@Override
	public void standard(Person person)
	{
		if (getDeadPerson() == person)
			die(person);
	}

	public void die(Person person)
	{
		person.sendMessage("You just died.");
	}
}
