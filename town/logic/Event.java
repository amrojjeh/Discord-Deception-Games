package town.logic;

public class Event
{
	public final String name;
	public final Player actor;
	public final Player target;

	Event(String name, Player a)
	{
		this.name = name;
		actor = a;
		target = null;
	}

	Event(String name, Player actor, Player target)
	{
		this.name = name;
		this.actor = actor;
		this.target = target;
	}
}
