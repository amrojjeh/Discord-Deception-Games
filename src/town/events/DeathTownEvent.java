package town.events;

import net.dv8tion.jda.api.JDA;
import town.persons.Person;

public class onDeathTownEvent implements TownEvent
{
	private Person dead;
	private JDA jda;
	
	public onDeathTownEvent(JDA jda, Person dead)
	{
		this.jda = jda;
		this.dead = dead;
	}
	
	public Person getDeadPerson()
	{
		return dead;
	}
	
	@Override
	public JDA getJDA() 
	{
		return jda;
	}

	@Override
	public void standard(Person person)
	{
		if (getDeadPerson() == person)
			person.sendMessage("You just died.");
	}
}
