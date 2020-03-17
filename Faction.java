// Factions needs to be a single class, so that factions like members in a faction like the mafia can switch roles when someone dies.

import java.util.ArrayList;

public class Faction{
	public String factionName;

	private ArrayList<Player> members;

	Faction(String name)
	{
		factionName = name;
		members = new ArrayList<>();
	}

	Faction(String name, ArrayList<Player> m)
	{
		factionName = name;
		members = m;
	}

	public void onMemberDeath(Action action)
	{
		for (Player member : members)
			member.registerActionOnDeath(action);
	}

	public void assignRoles(Func<Role, Player> func)
	{
		for (Player player : members)
			player.setRole(func.run(player));
	}
}
