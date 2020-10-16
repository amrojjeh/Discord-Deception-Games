package town.mafia.roles;

import java.util.List;

import javax.annotation.Nonnull;

import town.events.DoctorTownEvent;
import town.mafia.phases.Night;
import town.mafia.roles.data.DoctorData;
import town.persons.AttributeValue;
import town.persons.Attributes;
import town.persons.Person;
import town.roles.Faction;
import town.roles.Role;
import town.roles.RoleData;

public class Doctor implements Role
{
	public final Attributes attr = new Attributes(AttributeValue.NONE, AttributeValue.NONE);
	public final String name = "Civillian";

	public DoctorData getRoleDataFromPerson(Person user)
	{
		RoleData data = user.getRoleData();
		if (data == null)
			throw new NullPointerException("RoleData cannot be null");
		if (data instanceof DoctorData)
			return (DoctorData)data;
		throw new IllegalArgumentException("User does not have type DoctorData");
	}

	@Override
	public String ability(Person user, List<Person> references)
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

		user.setTownEvent(new DoctorTownEvent(user.getGame(), this, references.get(0)));
		user.getGame().addEvent(user.getTownEvent());

		return msg + String.format("You will heal <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public List<Person> getPossibleTargets(Person user)
	{
		DoctorData data = getRoleDataFromPerson(user);

		List<Person> targets = user.getGame().getAlivePlayers();
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
	public RoleData getInitialRoleData()
	{
		return new DoctorData(1);
	}
}
