package town.persons.assigner;

import town.DiscordGame;
import town.TownRole;
import town.persons.Person;

public class GeneralAssigner extends RoleAssigner
{
	final TownRole role;
	public GeneralAssigner(DiscordGame g, TownRole role)
	{
		super(g);
		this.role = role;
	}

	public GeneralAssigner(DiscordGame g, int max, TownRole role)
	{
		super(g, max);
		this.role = role;
	}

	@Override
	public Person getPerson(int ref, long id)
	{
		return role.build(game, ref, id);
	}
}
