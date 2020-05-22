package town.persons;

import town.DiscordGame;

// Civilian is NOT A REAL ROLE. This is a temporary useless town role to simulate games
public class Civilian extends Person
{
	static int amount = 0;
	
	public Civilian(DiscordGame game, int num, String id)
	{
		super(game, num, id);
		amount++;
	}

	@Override
	public String getRoleName()
	{
		return "Civilian";
	}

	@Override
	public int getAttackStat()
	{
		return 0;
	}

	@Override
	public int getDefenseStat()
	{
		return 0;
	}

	@Override
	public int getPriority()
	{
		return 6;
	}

	public static int getAmount()
	{
		return amount;
	}

	public static int getMaxAmount()
	{
		return 0;
	}
}
