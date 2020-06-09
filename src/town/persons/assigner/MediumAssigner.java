package town.persons.assigner;

import town.DiscordGame;
import town.persons.Medium;
import town.persons.Person;

public class MediumAssigner extends RoleAssigner {
	public MediumAssigner(DiscordGame g)
	{
		super(g);
	}

	@Override
	public Person getPerson(int ref, long ID)
	{
		return new Medium(game, ref, ID);
	}
}
