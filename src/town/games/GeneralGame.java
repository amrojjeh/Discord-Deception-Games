package town.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import town.DiscordGame;
import town.TownRole;
import town.games.parser.Rule;
import town.persons.assigner.Assigner;
import town.persons.assigner.GeneralAssigner;

public class GeneralGame
{
	ArrayList<Rule> rules = new ArrayList<>();
	final String name;
	Set<TownRole> roles = new HashSet<>();

	public GeneralGame(String name)
	{
		this.name = name;
	}

	public void addRule(Rule rule)
	{
		rules.add(rule);
		roles.addAll(rule.getRoles());
		rules.sort((s, o) -> s.totalPlayers - o.totalPlayers);
	}

	public Set<TownRole> getRoles()
	{
		return roles;
	}

	public String getName()
	{
		return name;
	}

	public int getMinimumTotalPlayers()
	{
		return rules.get(0).totalPlayers;
	}

	public void build(DiscordGame game)
	{
		int totalPlayers = game.getPlayersCache().size();
		Assigner assigner;
		Rule ruleFloor = rules.get(0);
		for (Rule rule : rules)
			if (rule.totalPlayers > totalPlayers)
				break;
			else
				ruleFloor = rule;
		assigner = ruleFloor.buildAssigner();
		int basePlayers = ruleFloor.totalPlayers;
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, basePlayers, person.getNum(), person.getID()));
	}

	public void buildRand(DiscordGame game)
	{
		Assigner assigner = new Assigner();
		for (TownRole role : roles)
			assigner.addRole(new GeneralAssigner(role));
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(game, 0, person.getNum(), person.getID()));
	}
}
