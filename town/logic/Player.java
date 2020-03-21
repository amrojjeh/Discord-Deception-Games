package town.logic;

import java.util.ArrayList;
import town.logic.roles.Role;
import town.logic.delegates.ActionOne;
import town.logic.roles.stats.AttackStat;

public class Player
{
	public String name;
	public final int id;

	private static int final_id;

	public ArrayList<Player> nightlyVisitors;
	boolean roleBlocked = false;
	
	Role role;
	Faction faction;

	ActionOne<Player> onDeath;

	Player(String n)
	{
		id = final_id++;
		name = n;
		nightlyVisitors = new ArrayList<Player>();
	}

	Player(String n, Role r)
	{
		this.id = final_id++;
		name = n;
		role = r;
		nightlyVisitors = new ArrayList<Player>();
	}

	public boolean equals(Object other)
	{
		if (other instanceof Player) return equals(other);
		return false; 
	}

	// This assumes that player IDs are unique
	public boolean equals(Player player)
	{
		return id == player.id;
	}

	public void setRole(Role r)
	{
		role = r;
	}

	public Role getRole()
	{
		return role;
	}

	public void setFaction(Faction f)
	{
		faction = f;
	}

	public Faction getFaction()
	{
		return faction;
	}

	public void dies()
	{
		onDeath.run(this);
	}

	public void registerActionOnDeath(ActionOne<Player> action)
	{
		onDeath = action;
	}

	// Can be killed
	public boolean canBeKillAble(AttackStat stat)
	{
		return stat.getValue() > role.getDefenseStat().getValue();
	}

	public boolean roleBlock()
	{
		if (role.hasRBImunnity()) return false;
		roleBlocked = true;
		return true;
	}

	public void addVisitor(Player visitor)
	{
		nightlyVisitors.add(visitor);
	}

	// To make it more natural
	public void visit(Player target)
	{
		target.addVisitor(this);
	}

	public void onEvent(Event event)
	{
		switch (event.name)
		{
			case "endDay":
				roleBlocked = false;
				break;
			case "endNight":
				// Run and reset visitors
				runVisitors();
				nightlyVisitors.clear();
				break;
		}
	}

	// It's assumed that the nightlyVisitors are in order
	public void runVisitors()
	{
		for (Player p : nightlyVisitors)
			p.getRole().execute(p, this);
	}
}
