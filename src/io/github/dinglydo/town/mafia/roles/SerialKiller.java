package io.github.dinglydo.town.mafia.roles;

import java.util.ArrayList;
import java.util.List;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.events.MurderTownEvent;
import io.github.dinglydo.town.mafia.phases.Night;
import io.github.dinglydo.town.persons.AttributeValue;
import io.github.dinglydo.town.persons.Attributes;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.EmptyRoleData;
import io.github.dinglydo.town.roles.Faction;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;

// A Serial Killer can kill a person each night.
public class SerialKiller implements Role
{
	private final Attributes attr = new Attributes(AttributeValue.BASIC, AttributeValue.BASIC);
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> players = new ArrayList<>();

	public SerialKiller(DiscordGame game)
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
		return TVMRole.SERIAL_KILLER;
	}

	@Override
	public String ability(DiscordGamePerson user, List<DiscordGamePerson> references)
	{
		if (!user.isAlive())
			return "Can't kill if you're dead.";
		if (references.isEmpty())
			return "There's no person to kill. `!ability 1` to kill the first person shown in `!party`.";
		if (references.size() > 1)
			return "Cannot kill more than one person at once. `!ability 1` to kill the first person show in `!party`.";
		if (!(user.getGame().getCurrentPhase() instanceof Night))
			return "Serial Killers can only kill during the night.";

		// You can't kill yourself
		if (references.get(0) == user)
			return "You can't kill yourself.";

		if (!references.get(0).isAlive())
			return "You can't kill a dead guy.";

		String msg = "";

		if (user.getTownEvent() != null)
		{
			msg += "You've changed your mind.\n";
			user.clearTownEvent();
		}

		user.setTownEvent(new MurderTownEvent(user.getGame(), user, references.get(0)));
		user.getGame().addEvent(user.getTownEvent());

		return msg + String.format("You will kill <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public List<DiscordGamePerson> getPossibleTargets(DiscordGamePerson user)
	{
		List<DiscordGamePerson> targets = user.getGame().getAlivePlayers();
		targets.remove(user);
		return targets;
	}

	@Override
	public String getHelp()
	{
		return "SERIAL KILLER (SK)\n" +
				"Serial Killer wins with other serial killers. His goal is to kill anyone who isn't an SK.\n" +
				"Ability: Can kill one person every night. Ex: `!ability 2` kills person number two. Check a person's number with !party";
	}

	@Override
	public Attributes getAttributes()
	{
		return attr;
	}

	@Override
	public Faction getFaction()
	{
		return Faction.SERIAL_KILLER;
	}

	@Override
	public RoleData getInitialRoleData()
	{
		return new EmptyRoleData();
	}

	@Override
	public int getPriority()
	{
		return 5;
	}

	@Override
	public ArrayList<DiscordGamePerson> getPlayers()
	{
		return players;
	}
}
