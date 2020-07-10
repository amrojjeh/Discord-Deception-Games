package town.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import town.DiscordGame;
import town.commands.CommandSet;
import town.games.parser.Rule;
import town.mafia.commands.TVMCommands;
import town.mafia.phases.FirstDay;
import town.persons.assigner.Assigner;
import town.persons.assigner.GeneralAssigner;
import town.phases.PhaseManager;
import town.roles.GameRole;

public class GameMode
{
	protected ArrayList<Rule> rules = new ArrayList<>();
	private final String name;
	private final String description;
	private final boolean isSpecial;
	private final CommandSet gameCommands;
	protected Set<GameRole> roles = new HashSet<>();

	public GameMode(String name, String description, boolean special)
	{
		this(name, description, special, new TVMCommands());
	}

	public GameMode(String name, String description, boolean special, CommandSet gameCommands)
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

	public CommandSet getCommands()
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

	public Set<GameRole> getTownRoles()
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
		int basePlayers = ruleFloor.totalPlayers;
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, basePlayers, person.getNum(), person.getID()));
	}

	public void buildRand(DiscordGame game)
	{
		Assigner assigner = new Assigner();
		for (GameRole role : roles)
			assigner.addRole(new GeneralAssigner(role));
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, 0, person.getNum(), person.getID()));
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

	public void start(DiscordGame game, PhaseManager pm)
	{
		pm.start(game, new FirstDay(game, pm));
	}
}
