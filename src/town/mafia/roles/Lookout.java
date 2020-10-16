package town.mafia.roles;

import java.util.List;

import javax.annotation.Nonnull;

import town.events.LookoutTownEvent;
import town.mafia.phases.Night;
import town.persons.AttributeValue;
import town.persons.Attributes;
import town.persons.Person;
import town.roles.EmptyRoleData;
import town.roles.Faction;
import town.roles.Role;
import town.roles.RoleData;

public class Lookout implements Role
{
	public final Attributes attr = new Attributes(AttributeValue.NONE, AttributeValue.NONE);
	public final String name = "Lookout";

	@Override
	@Nonnull
	public String ability(Person user, List<Person> references)
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

		user.setTownEvent(new LookoutTownEvent(user.getGame(), user, references.get(0)));
		user.getGame().addEvent(user.getTownEvent());

		return msg + String.format("You will watch <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public List<Person> getPossibleTargets(Person user)
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
	public Attributes getAttributes()
	{
		return attr;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return name;
	}

	@Override
	@Nonnull
	public Faction getFaction()
	{
		return Faction.TOWN;
	}

	@Override
	@Nonnull
	public RoleData getInitialRoleData()
	{
		return new EmptyRoleData();
	}
}
