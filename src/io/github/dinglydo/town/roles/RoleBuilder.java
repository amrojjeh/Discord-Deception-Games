package io.github.dinglydo.town.roles;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.mafia.roles.TVMRole;

public class RoleBuilder
{
	private boolean isDefault;
	private int minimum;
	private final TVMRole role;

	/**
	 * By default, the role is not default and has a minimum of zero.
	 * <br></br>
	 * Currently only accepts TVMRole. Perhaps if the bot branches outside of Mafia, required adjustments can be made
	 * @param role The role to build
	 */
	public RoleBuilder(TVMRole role)
	{
		this.role = role;
		isDefault = false;
		minimum = 0;
	}

	public TVMRole getTVMRole()
	{
		return role;
	}

	public RoleBuilder setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
		return this;
	}

	public RoleBuilder setMinimum(int min)
	{
		if (minimum < 0) throw new IllegalArgumentException("Minimum cannot be less than 0");
		this.minimum = min;
		return this;
	}

	public int getMinimum()
	{
		return minimum;
	}

	public boolean isDefault()
	{
		return isDefault;
	}

	public boolean check(DiscordGame game, int minimumTotalPlayers, int finalTotalPlayers)
	{
		if (finalTotalPlayers < minimumTotalPlayers) throw new IllegalArgumentException("Total players cannot be fewer than the minimum");
		int fixedAmount = (isDefault() ? finalTotalPlayers - minimumTotalPlayers : 0) + getMinimum();
		return getRole(game).getPlayerAmount() < fixedAmount;
	}

	// TODO: If there are going to be role modifications, we're gonna have to change the code here
	public Role getRole(DiscordGame game)
	{
		Role role = game.getRole(getTVMRole());
		if (role == null)
		{
			role = getTVMRole().getRole(game);
			game.addRole(role);
		}

		return role;
	}
}
