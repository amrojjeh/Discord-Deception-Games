package town.logic.roles;

import town.logic.Event;

public class DefaultOnEvent
{
	public static void run(Role role, Event event)
	{
		switch (event.name)
		{
			case "NightEnd":
				role.setDefenseStat(null);
		}
	}
}