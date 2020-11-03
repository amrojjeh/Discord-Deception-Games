package io.github.dinglydo.town.games.parser;

import io.github.dinglydo.town.roles.Role;

public class RoleInfo
{
	public Role role;
	public int max;
	public boolean isDefault;

	public RoleInfo(Role role, int max, boolean isDefault)
	{
		this.role = role;
		this.max = max;
		this.isDefault = isDefault;
	}
}