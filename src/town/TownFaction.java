package town;

public enum TownFaction
{
	TOWN("Town"),
	SERIAL_KILLER("Serial Killer");

	private final String name;

	TownFaction(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
