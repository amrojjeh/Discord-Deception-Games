package io.github.dinglydo.town.mafia.roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.events.MurderTownEvent;
import io.github.dinglydo.town.events.TownEvent;
import io.github.dinglydo.town.mafia.factions.Town;
import io.github.dinglydo.town.mafia.phases.Night;
import io.github.dinglydo.town.mafia.roles.data.DoctorData;
import io.github.dinglydo.town.persons.AttributeValue;
import io.github.dinglydo.town.persons.Attributes;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.Faction;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;

public class Doctor implements Role
{
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> players = new ArrayList<>();
	private final Faction faction;

	public Doctor(DiscordGame game)
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
		return TVMRole.DOCTOR;
	}

	public DoctorData getRoleDataFromPerson(DiscordGamePerson user)
	{
		RoleData data = user.getRoleData();
		if (data == null)
			throw new NullPointerException("RoleData cannot be null");
		if (data instanceof DoctorData)
			return (DoctorData)data;
		throw new IllegalArgumentException("User does not have type DoctorData");
	}

	@Override
	public String ability(DiscordGamePerson user, List<DiscordGamePerson> references)
	{
		String msg = "";
		DoctorData data = getRoleDataFromPerson(user);

		if (user.getTownEvent() != null)
		{
			msg += "You've changed your mind.\n";
			user.clearTownEvent();
		}

		if (references.isEmpty())
			return "There's no one to heal. `!ability 1` to protect the first person shown in `!party`.";
		if (references.size() > 1)
			return "Cannot heal more than one person at once. `!ability 1` to watch the first person show in `!party`.";
		if (!(user.getGame().getCurrentPhase() instanceof Night))
			return "You can only use your ability at night.";
		if (!user.isAlive())
			return "Doctors can't heal when dead";

		if (references.get(0) == user && !data.canSelfHeal())
			return "You cannot self heal anymore";
		else if(references.get(0) == user)
		{
			msg += "Remember, you only get one self-heal.";
		}

		user.setTownEvent(new DoctorTownEvent(user.getGame(), user, this, references.get(0)));

		return msg + String.format("You will heal <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public ArrayList<DiscordGamePerson> getPossibleTargets(DiscordGamePerson user)
	{
		DoctorData data = getRoleDataFromPerson(user);

		ArrayList<DiscordGamePerson> targets = user.getGame().getAlivePlayers();
		if (!data.canSelfHeal())
			targets.remove(user);
		return targets;
	}

	@Override
	public String getHelp()
	{
		return "DOCTOR\n" +
				"Doctors can heal a person each night.\n" +
				"Ability: Grants a person high defense. Ex: `!ability 2` heals person number two. Check a person's number with !party";
	}

	@Override
	@Nonnull
	public Faction getFaction()
	{
		return faction;
	}

	@Override
	public RoleData getInitialRoleData()
	{
		return new DoctorData(1);
	}

	@Override
	public ArrayList<DiscordGamePerson> getPlayers()
	{
		return players;
	}
}

class DoctorTownEvent implements TownEvent
{
	private final DiscordGamePerson user;
	private final Doctor role;
	private final DiscordGamePerson target;
	private final DiscordGame game;
	private final ArrayList<DiscordGamePerson> visitors = new ArrayList<>();

	public DoctorTownEvent(DiscordGame game, DiscordGamePerson user, Doctor role, DiscordGamePerson target)
	{
		this.game = game;
		this.user = user;
		this.target = target;
		this.role = role;
	}

	@Override
	public DiscordGamePerson getUser()
	{
		return getDoctor();
	}

	public DiscordGamePerson getDoctor()
	{
		return user;
	}

	public Doctor getRole()
	{
		return role;
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
	public int getPriority()
	{
		return role.getPriority();
	}

	@Override
	public void standard(DiscordGamePerson person)
	{
		if (person.isVisiting(getTarget()) && person.getTownEvent() instanceof MurderTownEvent)
			visitors.add(person);
		else if (getTarget() == person)
		{
			protect();
			if (person == getDoctor())
				getRole().getRoleDataFromPerson(user).useSelfHeal();
		}
	}

	@Override
	public void postDispatch()
	{
		if (visitors.size() == 0) getDoctor().sendMessage(String.format("No one attacked <@%d>.", getTarget().getID()));
		else getDoctor().sendMessage(String.format("Your target, <@%d>, was attacked.", getTarget().getID()));
	}

	public void protect()
	{
		getTarget().setTemporaryAttributes(new Attributes(AttributeValue.POWERFUL, AttributeValue.DEFAULT));
	}
}
