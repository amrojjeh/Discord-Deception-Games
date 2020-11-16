package io.github.dinglydo.town.games;

import java.util.ArrayList;

import io.github.dinglydo.town.commands.CommandSet;
import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.mafia.commands.TVMCommands;
import io.github.dinglydo.town.mafia.phases.FirstDay;
import io.github.dinglydo.town.party.Party;
import io.github.dinglydo.town.persons.assigner.Assigner;
import io.github.dinglydo.town.phases.PhaseManager;
import io.github.dinglydo.town.roles.RoleBuilder;

public class GameMode
{
	protected ArrayList<Assigner> assigners = new ArrayList<>();
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
		for (Assigner rule : assigners)
			str += rule.toString() + "\n";
		return str;
	}

	public void addAssigner(Assigner rule)
	{
		assigners.add(rule);
		assigners.sort((s, o) -> s.getMinimumPlayers() - o.getMinimumPlayers());
	}

	public int getMinimumTotalPlayers()
	{
		return assigners.get(0).getMinimumPlayers();
	}

	public Assigner build(Party gp, DiscordGame game, boolean rand)
	{
		if (rand) return buildRand(gp, game);
		return buildDefault(gp, game);
	}

	public Assigner buildDefault(Party gp, DiscordGame game)
	{
		int totalPlayers = gp.getPlayerSize();
		Assigner assigner = getClosestAssigner(totalPlayers);
		assigner.assignRoles(gp, game);
		return assigner;
	}

	public Assigner buildRand(Party gp, DiscordGame game)
	{
		Assigner assigner = new Assigner(0);
		for (RoleBuilder role : getClosestAssigner(gp.getPlayerSize()).getRoles())
			assigner.addRole(new RoleBuilder(role.getTVMRole()).setDefault(true));
		assigner.assignRoles(gp, game);
		return assigner;
	}

	public Assigner getClosestAssigner(int totalPlayers)
	{
		Assigner ruleFloor = assigners.get(0);
		for (Assigner  rule : assigners)
			if (rule.getMinimumPlayers() > totalPlayers)
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
