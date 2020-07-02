package town;

import town.persons.Civilian;
import town.persons.Doctor;
import town.persons.Lookout;
import town.persons.Medium;
import town.persons.Person;
import town.persons.PersonBuilder;
import town.persons.SerialKiller;

public enum TownRole
{
	// Priority 0 if it's important to look at all events

	LOOKOUT("Lookout", TownFaction.TOWN, 0, 0, 0, (g, n, id) -> new Lookout(g, n, id)),
	MEDIUM("Medium", TownFaction.TOWN, 0, 0, 1, (g, n, id) -> new Medium(g, n, id)),
	DOCTOR("Doctor", TownFaction.TOWN, 0, 0, 3, (g, n, id) -> new Doctor(g, n, id)),
	SERIAL_KILLER("Serial Killer", TownFaction.SERIAL_KILLER, 1, 1, 5, (g, n, id) -> new SerialKiller(g, n, id)),
	CIVILIAN("Civilian", TownFaction.TOWN, 0, 0, 6, (g, n, id) -> new Civilian(g, n, id));

	private final String name;
	private final TownFaction faction;
	private final int attack, defense, priority;
	private final PersonBuilder builder;

	TownRole(String name, TownFaction faction, int attack, int defense, int priority, PersonBuilder builder)
	{
		this.name = name;
		this.faction = faction;
		this.attack = attack;
		this.defense = defense;
		this.priority = priority;
		this.builder = builder;
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

	public Person build(DiscordGame g, int ref, long id)
	{
		return builder.getPerson(g, ref, id);
	}
}
