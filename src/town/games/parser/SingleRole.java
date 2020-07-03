package town.games.parser;

import town.TownRole;

public  class SingleRole
{
	public TownRole role;
	public int max;
	public boolean isDefault;

	public SingleRole(TownRole role, int max, boolean isDefault)
	{
		this.role = role;
		this.max = max;
		this.isDefault = isDefault;
	}
}