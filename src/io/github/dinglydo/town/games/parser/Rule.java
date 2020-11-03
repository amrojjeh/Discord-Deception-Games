package io.github.dinglydo.town.games.parser;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.dinglydo.town.persons.assigner.Assigner;
import io.github.dinglydo.town.persons.assigner.RoleAssigner;
import io.github.dinglydo.town.roles.Role;

public class Rule
{
	public int totalPlayers;
	public ArrayList<RoleInfo> roles = new ArrayList<>();

	public Rule(int totalPlayers)
	{
		this.totalPlayers = totalPlayers;
	}

	public boolean hasDefault()
	{
		for (RoleInfo role : roles)
			if (role.isDefault)
				return true;
		return false;
	}

	public void addRole(RoleInfo role)
	{
		roles.add(role);
	}

	public HashSet<Role> getRoles()
	{
		HashSet<Role> allRoles = new HashSet<>();
		for (RoleInfo sr : roles)
			allRoles.add(sr.role);
		return allRoles;
	}

	public Assigner buildAssigner()
	{
		Assigner assigner = new Assigner(totalPlayers);
		for (RoleInfo sr : roles)
		{
			RoleAssigner ga = new RoleAssigner(sr.role, sr.max);
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
			RoleInfo role = roles.get(x);
			builder.append(role.role.getName() + " " + role.max + (role.isDefault ? "+" : ""));
			if (x != roles.size() - 1)
				builder.append(", ");
		}
		return builder.toString();

	}
}
