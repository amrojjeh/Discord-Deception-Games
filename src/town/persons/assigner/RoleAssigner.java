package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;
import town.roles.GameRole;

public abstract class RoleAssigner
{
	int amount = 0;
	int maxAmount = -1;
	boolean isDefault = false;

	RoleAssigner()
	{

	}

	RoleAssigner(int maxAmount)
	{
		this.maxAmount = maxAmount;
	}

	public boolean check(int baseTotalOfPlayers, int totalPlayers)
	{
		if (maxAmount == -1) return true;
		// Adjust maxAmount if default is set
		int newMax = maxAmount;
		if (isDefault && totalPlayers >= baseTotalOfPlayers)
			newMax = totalPlayers - baseTotalOfPlayers + maxAmount;

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

	public void setDefault(boolean value)
	{
		isDefault = value;
	}

	public abstract Person getPerson(DiscordGame game, int ref, long id);
	public abstract GameRole[] getTownRoles();
}
