package town.mafia.persons;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import town.persons.AttributeValue;
import town.persons.Attributes;
import town.persons.Person;
import town.roles.GameFaction;
import town.roles.GameRole;

public class Civilian implements GameRole
{
	private Attributes attr = new Attributes(AttributeValue.NONE, AttributeValue.NONE);

	@Override
	public String getHelp()
	{
		return "Civilian. You can't do anything, so wish best of luck.";
	}

	@Override
	public Attributes getAttributes()
	{
		return attr;
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
	public GameFaction getFaction()
	{
		return GameFaction.TOWN;
	}

	@Override
	public boolean hasWon(@Nonnull Person user)
	{
		if (user == null) throw new NullPointerException("User cannot be an exception");
		return user.getGame().hasTownFactionWon(getFaction());
	}

	@Override
	public boolean canWin(@Nullable Person user)
	{
		return true;
	}

	@Override
	public void win(Person user)
	{
		getFaction().win(user.getGame());
	}

	@Override
	public String getName()
	{
		return "Civillian";
	}
}
