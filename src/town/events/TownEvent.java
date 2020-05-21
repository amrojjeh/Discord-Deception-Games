package town.events;

import net.dv8tion.jda.api.JDA;
import town.DiscordGame;
import town.persons.Person;

public interface TownEvent
{
	public DiscordGame getGame();
	public JDA getJDA();
	public void standard(Person person);
}
