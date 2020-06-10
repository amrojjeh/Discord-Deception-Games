package town.persons.assigner;

import town.DiscordGame;
import town.persons.Lookout;
import town.persons.Person;

public class LookoutAssigner extends RoleAssigner
{
	public LookoutAssigner(DiscordGame g)
	{
		super(g);
	}

	public LookoutAssigner(DiscordGame g, int max)
	{
		super(g, max);
	}

	@Override
	public Person getPerson(int ref, long ID)
	{
		++amount;
		return new Lookout(game, ref, ID);
	}
}
