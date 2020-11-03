package io.github.dinglydo.town.discordgame;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A collection of discord roles. Names and Ids must be unique.
 * @author Amr Ojjeh
 *
 */
public class DiscordRoles extends ArrayList<DiscordRole>
{
	private static final long serialVersionUID = 843813709240917377L;
	private final DiscordGame game;

	public DiscordRoles(@Nonnull DiscordGame game)
	{
		super();
		if (game == null) throw new IllegalArgumentException("Game cannot be null");
		this.game = game;
	}

	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public boolean add(@Nonnull DiscordRole role)
	{
		if (role == null) throw new IllegalArgumentException("Cannot add null");
		if (exists(role)) throw new IllegalArgumentException("Cannot add duplicate role: " + role.getName());
		if (role.getGame() != getGame()) throw new IllegalArgumentException("Discord games must be the same");
		return super.add(role);
	}

	public boolean add(String name, long id)
	{
		return add(new DiscordRole(getGame(), name, id));
	}

	public boolean exists(DiscordRole role)
	{
		for (DiscordRole other : this)
			if (other.getName().equalsIgnoreCase(role.getName()) || other.getId() == role.getId())
				return true;
		return false;
	}

	public boolean exists(String name, long id)
	{
		return exists(new DiscordRole(getGame(), name, id));
	}

	@Nullable
	public DiscordRole get(String name)
	{
		for (DiscordRole role : this)
			if (role.getName().equalsIgnoreCase(name)) return role;
		return null;
	}
}
