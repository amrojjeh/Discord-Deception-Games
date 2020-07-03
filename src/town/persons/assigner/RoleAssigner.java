package town.persons.assigner;

import town.DiscordGame;
import town.TownRole;
import town.persons.Person;

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
		// Adjust maxAmount if default is set
		int newMax = maxAmount;
		if (isDefault)
			newMax = totalPlayers - baseTotalOfPlayers + maxAmount;

		if (newMax == -1) return true;
		return amount < maxAmount;
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
	public abstract TownRole[] getTownRoles();
}
