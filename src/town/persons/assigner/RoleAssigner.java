package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;

public abstract class RoleAssigner
{
	DiscordGame game;

	RoleAssigner(DiscordGame g)
	{
		game = g;
	}

	abstract Person getPerson(int ref, long id);

	abstract public boolean check();
}
