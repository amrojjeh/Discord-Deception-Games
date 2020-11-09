package io.github.dinglydo.town.mafia.roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.AttributeValue;
import io.github.dinglydo.town.persons.Attributes;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.EmptyRoleData;
import io.github.dinglydo.town.roles.Faction;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;

public class Civilian implements Role
{
	private final Attributes attr = new Attributes(AttributeValue.NONE, AttributeValue.NONE);
	private final DiscordGame game;
	private ArrayList<DiscordGamePerson> players = new ArrayList<>();

	public Civilian(DiscordGame game)
	{
		this.game = game;
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public TVMRole getRole()
	{
		return TVMRole.CIVILIAN;
	}

	@Override
	public String getHelp()
	{
		return "Civilian. You can't do anything, so best of luck.";
	}

	@Override
	public Attributes getAttributes()
	{
		return attr;
	}

	@Override
	public RoleData getInitialRoleData()
	{
		return new EmptyRoleData();
	}

	@Override
	@Nullable
	public ArrayList<DiscordGamePerson> getPossibleTargets(DiscordGamePerson user)
	{
		return null;
	}

	@Override
	@Nonnull
	public String ability(@Nullable DiscordGamePerson user, @Nullable List<DiscordGamePerson> list)
	{
		return getName() + " has no ability";
	}

	@Override
	@Nonnull
	public Faction getFaction()
	{
		return Faction.TOWN;
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

	@Override
	public ArrayList<DiscordGamePerson> getPlayers()
	{
		return players;
	}
}
