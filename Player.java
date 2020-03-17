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

	public boolean canBeKillAble(AttackStat stat)
	{
		//when i git pulled this morning, this gibberish came up. does it mean anything to you?
//<<<<<<< HEAD
		//return stat.getValue() > role.getDefenseStat();
//=======
		return stat.getValue() > role.getDefenseStat().getValue();
//>>>>>>> 3c094d05ffc716a56f688636717e12652c3db641
	}
}
