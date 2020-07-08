package town;

public enum GameFaction
{
	TOWN("Town"),
	SERIAL_KILLER("Serial Killer");

	private final String name;

	GameFaction(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
