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

	public boolean check() {return true;}

	abstract Person getPerson(int ref, long id);
}
