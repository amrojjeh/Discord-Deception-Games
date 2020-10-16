package town.mafia.roles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import town.persons.AttributeValue;
import town.persons.Attributes;
import town.persons.Person;
import town.roles.EmptyRoleData;
import town.roles.Faction;
import town.roles.Role;
import town.roles.RoleData;

public class Civilian implements Role
{
	public final Attributes attr = new Attributes(AttributeValue.NONE, AttributeValue.NONE);
	public final String name = "Civillian";

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
	public List<Person> getPossibleTargets(Person user)
	{
		return null;
	}

	@Override
	@Nonnull
	public String ability(@Nullable Person user, @Nullable List<Person> list)
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
	public String getName()
	{
		return name;
	}
}
