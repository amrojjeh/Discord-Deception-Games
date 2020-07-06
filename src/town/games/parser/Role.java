package town.games.parser;

import town.TownRole;

public  class Role
{
	public TownRole role;
	public int max;
	public boolean isDefault;

	public Role(TownRole role, int max, boolean isDefault)
	{
		this.role = role;
		this.max = max;
		this.isDefault = isDefault;
	}
}