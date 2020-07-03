package town.persons.assigner;

import town.DiscordGame;
import town.TownRole;
import town.persons.Person;

public class GeneralAssigner extends RoleAssigner
{
	final TownRole role;

	public GeneralAssigner(TownRole role)
	{
		super();
		this.role = role;
	}

	public GeneralAssigner(TownRole role, int max)
	{
		super(max);
		this.role = role;
	}

	@Override
	public Person getPerson(DiscordGame game, int ref, long id)
	{
		++amount;
		return role.build(game, ref, id);
	}

	@Override
	public TownRole[] getTownRoles()
	{
		TownRole[] roles = {role};
		return roles;
	}
}
