package town.persons.assigner;

import town.DiscordGame;
import town.persons.Person;

public abstract class RoleAssigner
{
	DiscordGame game;
	int amount = 0;
	int maxAmount = -1;

	RoleAssigner(DiscordGame g)
	{
		game = g;
	}

	RoleAssigner(DiscordGame g, int maxAmount)
	{
		game = g;
		this.maxAmount = maxAmount;
	}

	public boolean check()
	{
		if (maxAmount == -1) return true;
		return amount < maxAmount;
	}

	public int getAmount()
	{
		return amount;
	}

	abstract Person getPerson(int ref, long id);
}
