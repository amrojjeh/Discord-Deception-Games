import java.util.ArrayList;

public class Player
{
	public String name;
	public final int id;

	public ArrayList<Player> nightlyVisitors;

	Role role;
	Faction faction;

	ActionOne<Player> onDeath;

	Player(int id, String n)
	{
		this.id = id;
		name = n;
		nightlyVisitors = new ArrayList<Player>();
	}

	Player(int id, String n, Role r)
	{
		this.id = id;
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

	public boolean canBeKillAble(AttackStat stat)
	{
		return stat.getValue() > role.getDefenseStat().getValue();
	}

	public void newVisitor(Player visitor){
		nightlyVisitors.add(visitor);
		//IMPLEMENT: reset this list each new day
	}
}
