package town.games.parser;

// TODO: Change this name so that it doesn't conflict with town.roles.Role
public class Role
{
	public town.roles.Role role;
	public int max;
	public boolean isDefault;

	public Role(town.roles.Role role, int max, boolean isDefault)
	{
		this.role = role;
		this.max = max;
		this.isDefault = isDefault;
	}
}