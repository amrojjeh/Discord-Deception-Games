package town;

public enum TownRole
{
	SERIAL_KILLER("Serial Killer", 1, 1, 3),
	CIVILIAN("Civilian", 0, 0, 6);

	private final String name;
	private final int attack, defense, priority;

	TownRole(String name, int attack, int defense, int priority)
	{
		this.name = name;
		this.attack = attack;
		this.defense = defense;
		this.priority = priority;
	}

	public String getName()
	{
		return name;
	}

	public  int getAttack()
	{
		return attack;
	}

	public int getDefense()
	{
		return defense;
	}

	public int getPriority()
	{
		return priority;
	}
}
