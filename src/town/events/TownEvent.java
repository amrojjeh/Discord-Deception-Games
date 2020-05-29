package town.events;

import town.DiscordGame;
import town.persons.Person;

public interface TownEvent
{
	DiscordGame getGame();
	void standard(Person person);
}
