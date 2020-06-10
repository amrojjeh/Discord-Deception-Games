package town.persons.assigner;

import town.DiscordGame;
import town.persons.Civilian;
import town.persons.Person;

public class CivilianAssigner extends RoleAssigner
{
	public CivilianAssigner(DiscordGame g)
	{
		super(g);
	}

	public CivilianAssigner(DiscordGame g, int maxAmount)
	{
		super(g, maxAmount);
	}

	@Override
	public Person getPerson(int ref, long ID)
	{
		amount++;
		return new Civilian(game, ref, ID);
	}
}
