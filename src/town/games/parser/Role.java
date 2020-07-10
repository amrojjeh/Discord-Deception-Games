package town.games.parser;

import town.roles.GameRole;

public  class Role
{
	public GameRole role;
	public int max;
	public boolean isDefault;

	public Role(GameRole role, int max, boolean isDefault)
	{
		this.role = role;
		this.max = max;
		this.isDefault = isDefault;
	}
}