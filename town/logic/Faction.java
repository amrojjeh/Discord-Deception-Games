package town.logic;
// Factions needs to be a single class, so members in a faction like the mafia can switch roles when someone dies.

import java.util.ArrayList;
import town.logic.delegates.ActionOne;
import town.logic.delegates.Func;
import town.logic.roles.Role;

public class Faction
{
	// There are major and minor alignments in ToS
	// A major role would be something like townee, while a minor role would be something like "townee killing" or "neutral killing"
	public final String major;
	public final String minor;

	private ArrayList<Player> members;

	Faction(String ma, String mi)
	{
		major = ma;
		minor = mi;
		members = new ArrayList<>();
	}

	Faction(String ma, String mi, ArrayList<Player> m)
	{
		major = ma;
		minor = mi;
		members = m;
		for (Player player : members)
			player.setFaction(this);
	}

	public void onMemberDeath(ActionOne<Player> action)
	{
		for (Player member : members)
			member.registerActionOnDeath(action);
	}

	public void assignRoles(Func<Role, Player> func)
	{
		for (Player player : members)
		{
			player.setFaction(this);
			player.setRole(func.run(player));
		}
	}

	public void addMember(Player player, Func<Role, Player> func)
	{
		player.setFaction(this);
		player.setRole(func.run(player));
		members.add(player);
	}

	public void addMember(Player player)
	{
		player.setFaction(this);
		members.add(player);
	}
}
