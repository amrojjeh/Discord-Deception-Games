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

	public GameRole[] getAllRoles()
	{
		ArrayList<GameRole> roles = new ArrayList<>(GameRole.values().length);
		for (GameRole role : GameRole.values())
			if (role.getGameType() == this)
				roles.add(role);
		GameRole[] arrayRoles = new GameRole[roles.size()];
		roles.toArray(arrayRoles);
		return arrayRoles;
	}
}
