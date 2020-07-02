package town.persons;

import town.DiscordGame;

@FunctionalInterface
public interface PersonBuilder
{
	public Person getPerson(DiscordGame game, int refNum, long id);
}
