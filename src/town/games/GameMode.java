package town.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import town.DiscordGame;
import town.commands.CommandSet;
import town.games.parser.Rule;
import town.mafia.commands.TVMCommands;
import town.persons.assigner.Assigner;
import town.roles.Role;

public class GameMode
{
	protected ArrayList<Rule> rules = new ArrayList<>();
	private final String name;
	private final String description;
	private final boolean isSpecial;
	private final CommandSet<DiscordGame> gameCommands;
	protected Set<Role> roles = new HashSet<>();

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
		roles.addAll(rule.getRoles());
		rules.sort((s, o) -> s.totalPlayers - o.totalPlayers);
	}

	public Set<Role> getTownRoles()
	{
		return roles;
	}

	public int getMinimumTotalPlayers()
	{
		return rules.get(0).totalPlayers;
	}

	public void build(DiscordGame game, boolean rand)
	{
		if (rand) buildRand(game);
		else buildDefault(game);
	}

	public void buildDefault(DiscordGame game)
	{
		int totalPlayers = game.getPlayersCache().size();
		Assigner assigner;
		Rule ruleFloor = getClosestRule(totalPlayers);
		assigner = ruleFloor.buildAssigner();
//		int basePlayers = ruleFloor.totalPlayers;
//		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, basePlayers, person.getNum(), person.getID()));
		throw new UnsupportedOperationException();

	}

	public void buildRand(DiscordGame game)
	{
//		Assigner assigner = new Assigner();
//		for (Role role : roles)
//			assigner.addRole(new GeneralAssigner(role));
//		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, 0, person.getNum(), person.getID()));
		throw new UnsupportedOperationException();

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
