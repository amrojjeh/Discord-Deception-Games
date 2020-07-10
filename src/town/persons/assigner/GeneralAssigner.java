package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;
import town.roles.GameRole;

public class GeneralAssigner extends RoleAssigner
{
	final GameRole role;

	public GeneralAssigner(GameRole role)
	{
		super();
		this.role = role;
	}

	public GeneralAssigner(GameRole role, int max)
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
	public GameRole[] getTownRoles()
	{
		GameRole[] roles = {role};
		return roles;
	}
}
