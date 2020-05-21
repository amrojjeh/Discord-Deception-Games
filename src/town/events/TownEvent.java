package town.events;

import net.dv8tion.jda.api.JDA;
import town.DiscordGame;
import town.persons.Person;

public interface TownEvent
{
	DiscordGame getGame();
	JDA getJDA();
	void standard(Person person);
}
