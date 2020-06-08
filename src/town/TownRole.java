package town;

public enum TownRole
{
	SERIAL_KILLER("Serial Killer", "Serial Killer", 1, 1, 3),
	CIVILIAN("Civilian", "Town", 0, 0, 6),
	MEDIUM("Medium", "Town", 0, 0, -1);

	private final String name, faction;
	private final int attack, defense, priority;

	TownRole(String name, String faction, int attack, int defense, int priority)
	{
		this.name = name;
		this.faction = faction;
		this.attack = attack;
		this.defense = defense;
		this.priority = priority;
	}

	public String getName()
	{
		return name;
	}
	
	public String getFaction()
	{
		return faction;
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
