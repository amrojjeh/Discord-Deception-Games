package town.persons.assigner;

import town.roles.Role;

public class RoleAssigner
{
	private final Role role;
	private int amount = 0;
	private int maxAmount = -1;
	private boolean isDefault = false;

	public RoleAssigner(Role role)
	{
		this.role = role;
	}

	public RoleAssigner(Role role, int max)
	{
		maxAmount = max;
		this.role = role;
	}

	public boolean check(int min, int totalPlayers)
	{
		if (maxAmount == -1) return true;
		// Adjust maxAmount if default is set
		int newMax = maxAmount;
		if (isDefault && totalPlayers >= min)
			newMax = totalPlayers - min + maxAmount;

		return amount < newMax;
	}

	public int getAmount()
	{
		return amount;
	}

	// Returns -1 if there is no max, there can still be a max even if default is set and the min has been assigned
	public int getMax()
	{
		return maxAmount;
	}

	public Role getRole()
	{
		return role;
	}

	public Role useRole()
	{
		++amount;
		return role;
	}

	public void setDefault(boolean value)
	{
		isDefault = value;
	}

}
