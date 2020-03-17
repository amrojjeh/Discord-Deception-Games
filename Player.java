public class Player
{
	Role role;
	String name;

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

	public void dies()
	{
		onDeath.run();
	}

	public void registerActionOnDeath(Action action)
	{
		onDeath = action;
	}
}
