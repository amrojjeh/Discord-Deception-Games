package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;
import town.persons.SerialKiller;

public class SerialKillerAssigner extends RoleAssigner
{
	public SerialKillerAssigner(DiscordGame g)
	{
		super(g);
	}

	@Override
	public Person getPerson(int refNum, long ID)
	{
		return new SerialKiller(game, refNum, ID);
	}
}
