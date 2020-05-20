package town.events;

import net.dv8tion.jda.api.JDA;
import town.persons.Person;

public class onMurderTownEvent implements TownEvent
{
	private Person murderer;
	private Person victim;
	private JDA jda;
	
	
	public onMurderTownEvent(JDA jda, Person m, Person v)
	{
		this.jda = jda;
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
	public JDA getJDA() 
	{
		return jda;
	}
	
	@Override
	public void standard(Person eventReceiver)
	{
		if (eventReceiver == getMurderer())
			eventReceiver.sendMessage("You killed " + jda.getUserById(getVictim().getID()).getName());
	}
}
