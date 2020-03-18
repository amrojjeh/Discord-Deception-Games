public class Player
{
	public String name;
	public final int id;

	Role role;
	Faction faction;

	ActionOne<Player> onDeath;

	Player(int id, String n)
	{
		this.id = id;
		name = n;
	}

	Player(int id, String n, Role r)
	{
		this.id = id;
		name = n;
		role = r;
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
}
