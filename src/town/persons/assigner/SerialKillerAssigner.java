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

	public SerialKillerAssigner(DiscordGame g, int max)
	{
		super(g, max);
	}

	@Override
	public Person getPerson(int refNum, long ID)
	{
		++amount;
		return new SerialKiller(game, refNum, ID);
	}
}
