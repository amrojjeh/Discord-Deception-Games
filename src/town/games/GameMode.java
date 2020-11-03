package town.games;

import java.util.ArrayList;

import town.GameParty;
import town.commands.CommandSet;
import town.discordgame.DiscordGame;
import town.games.parser.Rule;
import town.mafia.commands.TVMCommands;
import town.persons.DiscordGamePerson;
import town.persons.assigner.Assigner;
import town.persons.assigner.RoleAssigner;
import town.roles.Role;

public class GameMode
{
	protected ArrayList<Rule> rules = new ArrayList<>();
	private final String name;
	private final String description;
	private final boolean isSpecial;
	private final CommandSet<DiscordGame> gameCommands;

	public GameMode(String name, String description, boolean special)
	{
		this(name, description, special, new TVMCommands());
	}

	public GameMode(String name, String description, boolean special, CommandSet<DiscordGame> gameCommands)
	{
		this.name = name;
		this.description = description;
		this.isSpecial = special;
		this.gameCommands = gameCommands;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public boolean isSpecial()
	{
		return isSpecial;
	}

	public CommandSet<DiscordGame> getCommands()
	{
		return gameCommands;
	}

	public String getConfig()
	{
		if (isSpecial()) return "Special";
		String str = "";
		for (Rule rule : rules)
			str += rule.toString() + "\n";
		return str;
	}

	public void addRule(Rule rule)
	{
		rules.add(rule);
		rules.sort((s, o) -> s.totalPlayers - o.totalPlayers);
	}

	public int getMinimumTotalPlayers()
	{
		return rules.get(0).totalPlayers;
	}

	public ArrayList<DiscordGamePerson> build(GameParty gp, DiscordGame game, boolean rand)
	{
		if (rand) return buildRand(gp, game);
		return buildDefault(gp, game);
	}

	public ArrayList<DiscordGamePerson> buildDefault(GameParty gp, DiscordGame game)
	{
		int totalPlayers = gp.getPlayerSize();
		Rule ruleFloor = getClosestRule(totalPlayers);
		Assigner assigner = ruleFloor.buildAssigner();
		return assigner.assignRoles(gp, game);
	}

	public ArrayList<DiscordGamePerson> buildRand(GameParty gp, DiscordGame game)
	{
		Assigner assigner = new Assigner(0);
		for (Role role : getClosestRule(gp.getPlayerSize()).getRoles())
			assigner.addRole(new RoleAssigner(role));
		return assigner.assignRoles(gp, game);
	}

	public Rule getClosestRule(int totalPlayers)
	{
		Rule ruleFloor = rules.get(0);
		for (Rule rule : rules)
			if (rule.totalPlayers > totalPlayers)
				break;
			else
				ruleFloor = rule;
		return ruleFloor;
	}

//	public void start(DiscordGame game, PhaseManager pm)
//	{
//		throw new UnsupportedOperationException();
//		pm.start(game, new FirstDay(game, pm));
//	}
}
