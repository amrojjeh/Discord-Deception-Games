package town.games;

import town.TownRole;
import town.games.parser.Rule;

public class Mashup extends GeneralGame
{
	public Mashup()
	{
		super("Mashup");
		Rule rule = new Rule(0);
		for (TownRole role : TownRole.values())
			rule.addRole(role, -1, false);
	}
}
