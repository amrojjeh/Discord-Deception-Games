package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;
import town.roles.Role;

public class GeneralAssigner extends RoleAssigner
{
	final Role role;

	public GeneralAssigner(Role role)
	{
		super();
		this.role = role;
	}

	public GeneralAssigner(Role role, int max)
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
	public Role[] getTownRoles()
	{
		Role[] roles = {role};
		return roles;
	}
}
