package town.persons.assigner;

import town.DiscordGame;
import town.persons.Medium;
import town.persons.Person;

public class MediumAssigner extends RoleAssigner
{
	public MediumAssigner(DiscordGame g)
	{
		super(g);
	}

	public MediumAssigner(DiscordGame g, int max)
	{
		super(g, max);
	}

	@Override
	public Person getPerson(int ref, long ID)
	{
		++amount;
		return new Medium(game, ref, ID);
	}
}
