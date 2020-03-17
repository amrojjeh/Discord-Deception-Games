public class Player
{
	Role role;
	String name;
	Faction faction;

	ActionOne<Player> onDeath;

	Player(String n)
	{
		name = n;
	}

	Player(String n, Role r)
	{
		name = n;
		role = r;
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

	public String getName()
	{
		return name;
	}

	public void setName(String n)
	{
		name = n;
	}

	public void dies()
	{
		onDeath.run(this);
	}

	public void registerActionOnDeath(ActionOne<Player> action)
	{
		onDeath = action;
	}

	public boolean canKill(AttackStat stat)
	{
		return stat.getValue() >= role.getDefenseStat();
	}
}
