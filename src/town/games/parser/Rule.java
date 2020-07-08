package town.games.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import town.TownRole;
import town.persons.assigner.Assigner;
import town.persons.assigner.GeneralAssigner;

public class Rule
{
	public int totalPlayers;
	public ArrayList<Role> roles = new ArrayList<>();

	public Rule(int totalPlayers)
	{
		this.totalPlayers = totalPlayers;
	}

	public boolean hasDefault()
	{
		for (Role role : roles)
			if (role.isDefault)
				return true;
		return false;
	}

	public void addRole(TownRole role, int max, boolean isDefault)
	{
		Role singleRole = new Role(role, max, isDefault);
		roles.add(singleRole);
	}

	public void addRole(Role role)
	{
		roles.add(role);
	}

	public Set<TownRole> getRoles()
	{
		HashSet<TownRole> allRoles = new HashSet<>();
		for (Role sr : roles)
			allRoles.add(sr.role);
	return allRoles;
	}

	public Assigner buildAssigner()
	{
		Assigner assigner = new Assigner();
		for (Role sr : roles)
		{
			GeneralAssigner ga = new GeneralAssigner(sr.role, sr.max);
			ga.setDefault(sr.isDefault);
			assigner.addRole(ga);
		}
		return assigner;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(totalPlayers + " ");
		for (int x = 0; x < roles.size(); ++x)
		{
			Role role = roles.get(x);
			builder.append(role.role.getName() + " " + role.max + (role.isDefault ? "+" : ""));
			if (x != roles.size() - 1)
				builder.append(", ");
		}
		return builder.toString();
	}
}
