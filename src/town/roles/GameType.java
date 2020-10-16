package town.roles;

import java.util.ArrayList;

public enum GameType
{
	MAFIA("Mafia");

	private final String name;

	GameType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Role[] getAllRoles()
	{
		ArrayList<Role> roles = new ArrayList<>(Role.values().length);
		for (Role role : Role.values())
			if (role.getGameType() == this)
				roles.add(role);
		Role[] arrayRoles = new Role[roles.size()];
		roles.toArray(arrayRoles);
		return arrayRoles;
	}
}
