package town.mafia.roles;

import java.util.List;

import town.events.MurderTownEvent;
import town.mafia.phases.Night;
import town.persons.AttributeValue;
import town.persons.Attributes;
import town.persons.Person;
import town.roles.EmptyRoleData;
import town.roles.Faction;
import town.roles.Role;
import town.roles.RoleData;

// A Serial Killer can kill a person each night.
public class SerialKiller implements Role
{
	public final String name = "Serial Killer";
	public final Attributes attr = new Attributes(AttributeValue.BASIC, AttributeValue.BASIC);

	@Override
	public String ability(Person user, List<Person> references)
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
	public List<Person> getPossibleTargets(Person user)
	{
		List<Person> targets = user.getGame().getAlivePlayers();
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
	public String getName()
	{
		return name;
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
}
