package town;

public enum TownRole
{
	SERIAL_KILLER("Serial Killer", TownFaction.SERIAL_KILLER, 1, 1, 3),
	CIVILIAN("Civilian", TownFaction.TOWN, 0, 0, 6),
	LOOKOUT("Lookout", TownFaction.TOWN, 0, 0, 0),
	MEDIUM("Medium", TownFaction.TOWN, 0, 0, -1);

	private final String name;
	private final TownFaction faction;
	private final int attack, defense, priority;

	TownRole(String name, TownFaction faction, int attack, int defense, int priority)
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

	public TownFaction getFaction()
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
