package io.github.dinglydo.town.mafia.roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.events.TownEvent;
import io.github.dinglydo.town.mafia.factions.Town;
import io.github.dinglydo.town.mafia.phases.Night;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.EmptyRoleData;
import io.github.dinglydo.town.roles.Faction;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;

public class Lookout implements Role
{
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> players = new ArrayList<>();
	private final Faction faction;

	public Lookout(DiscordGame game)
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
		return TVMRole.LOOKOUT;
	}

	@Override
	@Nonnull
	public String ability(DiscordGamePerson user, List<DiscordGamePerson> references)
	{
		if (!user.isAlive())
			return "Can't watch people if you're dead.";
		if (references.isEmpty())
			return "There's no person to watch. `!ability 1` to watch the first person shown in `!party`.";
		if (references.size() > 1)
			return "Cannot watch more than one person at once. `!ability 1` to watch the first person show in `!party`.";
		if (!(user.getGame().getCurrentPhase() instanceof Night))
			return "Lookouts can only watch visitors during the night.";
		if (!references.get(0).isAlive())
			return "Lookouts can't watch dead people.";
		// In the real game, you can't track yourself
		//		if (references.get(0) == this)
		//			return "You can't track yourself.";

		String msg = "";

		if (user.getTownEvent() != null)
		{
			msg += "You've changed your mind.\n";
			user.clearTownEvent();
		}

		user.setTownEvent(new LookoutTownEvent(user.getGame(), user, this, references.get(0)));

		return msg + String.format("You will watch <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public ArrayList<DiscordGamePerson> getPossibleTargets(DiscordGamePerson user)
	{
		return user.getGame().getAlivePlayers();
	}

	@Override
	@Nonnull
	public String getHelp()
	{
		return "LOOKOUT\n" +
				"Lookouts win with other townees. His goal is to eliminate any evil doer.\n" +
				"Ability: Can see who visited a person that night. Ex: `!ability 2` watches person number two. Check a person's number with !party";
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

class LookoutTownEvent implements TownEvent
{
	private final DiscordGamePerson user;
	private final DiscordGamePerson target;
	private final Lookout role;
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> visitors = new ArrayList<>();

	public LookoutTownEvent(DiscordGame game, DiscordGamePerson l, Lookout role, DiscordGamePerson t)
	{
		this.game = game;
		this.user = l;
		this.role = role;
		this.target = t;
	}

	public Lookout getRole()
	{
		return role;
	}

	public DiscordGamePerson getLookout()
	{
		return user;
	}

	@Override
	public DiscordGamePerson getUser()
	{
		return getLookout();
	}

	@Override
	public DiscordGamePerson getTarget()
	{
		return target;
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public void standard(DiscordGamePerson person)
	{
		if (person.isVisiting(getTarget()))
			visitors.add(person);
	}

	@Override
	public void postDispatch()
	{
		if (visitors.size() == 1) getLookout().sendMessage(String.format("No one visited <@%d>", target.getID()));
		else
		{
			StringBuilder message = new StringBuilder(String.format("You watched <@%d> over night, here are the visitors\n", getTarget().getID()));
			visitors.forEach(person -> {if (person != getLookout()) message.append(String.format("- <@%d>\n", person.getID()));});
			getLookout().sendMessage(message.toString());
		}
	}

	@Override
	public int getPriority()
	{
		return getRole().getPriority();
	}
}
