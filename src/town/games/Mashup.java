package town.games;

import town.TownRole;
import town.games.parser.Rule;

public class Mashup extends GameMode
{
	public Mashup()
	{
		super("Mashup", "Play with all the roles, no limits!", true);
		Rule rule = new Rule(0);
		for (TownRole role : TownRole.values())
			rule.addRole(role, -1, false);
		addRule(rule);
	}
}
