package town.mafia.games;

import town.games.GameMode;
import town.games.parser.Rule;
import town.roles.Role;
import town.roles.GameType;

public class Mashup extends GameMode
{
	public Mashup()
	{
		super("Mashup", "Play with all the roles, no limits!", true);
		Rule rule = new Rule(0);
		Role[] roles = GameType.MAFIA.getAllRoles();
		for (Role role : roles)
		{
			System.out.println(role.getName());
			rule.addRole(role, -1, true);
		}
		addRule(rule);
	}
}
