public class Player
{
	Role role;
	String name;
	Faction faction;

	Action onDeath;

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

	public void dies()
	{
		onDeath.run();
	}

	public void registerActionOnDeath(Action action)
	{
		onDeath = action;
	}
}
