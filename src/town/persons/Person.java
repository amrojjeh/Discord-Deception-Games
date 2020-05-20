package town.persons;

import town.events.onDeathTownEvent;
import town.events.onMurderTownEvent;
import town.events.TownEvent;

import net.dv8tion.jda.api.JDA;

public abstract class Person
{
	String discordUserID;
	JDA jda; // Should be put into its own interface to seperate the game and discord
	
	Person(JDA jda, String id)
	{
		this.jda = jda;
		discordUserID = id;
	}
	
	public String getID()
	{
		return discordUserID;
	}
	
	public void sendMessage(String msg)
	{
		// TODO: Should check if user has private channel first
		jda.getUserById(discordUserID).openPrivateChannel().queue((channel) -> channel.sendMessage(msg).queue());
	}
	
	public void onDeath(onDeathTownEvent event) { event.standard(this); } // Returns 1 to skip standard, 0 to continue normally
	public void onMurder(onMurderTownEvent event) { event.standard(this); }
	
	
	public void onEvent(TownEvent event) 
	{
		if (event instanceof onDeathTownEvent) 
			onDeath((onDeathTownEvent)event);
		else if (event instanceof onMurderTownEvent)
			onMurder((onMurderTownEvent)event);
	}
}
