package town.roles;

import town.DiscordGame;
import town.mafia.persons.Civilian;
import town.mafia.persons.Doctor;
import town.mafia.persons.Lookout;
import town.mafia.persons.Medium;
import town.mafia.persons.SerialKiller;
import town.persons.Person;
import town.persons.PersonBuilder;

public enum GameRole
{
	// Priority 0 if it's important to look at all events

	LOOKOUT(GameType.MAFIA, "Lookout", GameFaction.TOWN, 0, 0, 0, (g, n, id) -> new Lookout(g, n, id)),
	MEDIUM(GameType.MAFIA, "Medium", GameFaction.TOWN, 0, 0, 1, (g, n, id) -> new Medium(g, n, id)),
	DOCTOR(GameType.MAFIA, "Doctor", GameFaction.TOWN, 0, 0, 3, (g, n, id) -> new Doctor(g, n, id)),
	SERIAL_KILLER(GameType.MAFIA, "Serial Killer", GameFaction.SERIAL_KILLER, 1, 1, 5, (g, n, id) -> new SerialKiller(g, n, id)),
	CIVILIAN(GameType.MAFIA, "Civilian", GameFaction.TOWN, 0, 0, 6, (g, n, id) -> new Civilian(g, n, id));

	private final String name;
	private final GameFaction faction;
	private final GameType gameType;
	private final int attack, defense, priority;
	private final PersonBuilder builder;

	GameRole(GameType gameType, String name, GameFaction faction, int attack, int defense, int priority, PersonBuilder builder)
	{
		this.name = name;
		this.faction = faction;
		this.gameType = gameType;
		this.attack = attack;
		this.defense = defense;
		this.priority = priority;
		this.builder = builder;
	}

	public String getName()
	{
		return name;
	}

	public GameFaction getFaction()
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

	public GameType getGameType()
	{
		return gameType;
	}

	public Person build(DiscordGame g, int ref, long id)
	{
		return builder.getPerson(g, ref, id);
	}

	public static GameRole getRoleFromName(String name)
	{
		GameRole role;
		try
		{
			role = GameRole.valueOf(name.toUpperCase().replace(" ", "_"));
		} catch (IllegalArgumentException e)
		{
			return null;
		}
		return role;
	}
}
