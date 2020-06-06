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

	@Override
	public Person getPerson(int ref, long ID)
	{
		return new Civilian(game, ref, ID);
	}

	@Override
	public boolean check()
	{
		return true;
	}
}
