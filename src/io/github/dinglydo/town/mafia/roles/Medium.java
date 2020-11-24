package io.github.dinglydo.town.mafia.roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.mafia.factions.Town;
import io.github.dinglydo.town.mafia.phases.Morning;
import io.github.dinglydo.town.mafia.phases.Night;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.roles.EmptyRoleData;
import io.github.dinglydo.town.roles.Faction;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;

/**
 * The Medium class represnts the role "Medium."
 * Medium is able to talk to the dead during the night.
 * This is done by giving access to the dead textchannel during the night, and revoking that channel during the day.
 * @author Amr Ojjeh
 *
 */
public class Medium implements Role
{
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> players = new ArrayList<>();
	private final Faction faction;

	public Medium(DiscordGame game)
	{
		this.game = game;
		this.faction = game.getFactionManager().getOrAddGlobalFaction("TOWN", Town::new);
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public TVMRole getRole()
	{
		return TVMRole.MEDIUM;
	}

	@Override
	public String getHelp()
	{
		return "You can talk to the dead during the night.";
	}

	@Override
	public void onPhaseChange(@Nonnull DiscordGamePerson user, @Nonnull Phase phase)
	{
		// The default method checks if user and phase are null
		Role.super.onPhaseChange(user, phase);
		if (phase instanceof Night && user.isAlive())
			user.getGame().setChannelVisibility(user, "the_afterlife", true, true).queue();
		else if (phase instanceof Morning && user.isAlive())
			user.getGame().getTextChannel("the_afterlife").getPermissionOverride(user.getMember()).delete().queue();
	}

	@Override
	@Nullable
	public List<DiscordGamePerson> getPossibleTargets(DiscordGamePerson user)
	{
		return null;
	}

	@Override
	@Nonnull
	public String ability(DiscordGamePerson user, List<DiscordGamePerson> list)
	{
		return getName() + " has no ability";
	}

	@Override
	@Nonnull
	public Faction getFaction()
	{
		return faction;
	}

	@Override
	@Nonnull
	public RoleData getInitialRoleData()
	{
		return new EmptyRoleData();
	}

	@Override
	public ArrayList<DiscordGamePerson> getPlayers()
	{
		return players;
	}
}
