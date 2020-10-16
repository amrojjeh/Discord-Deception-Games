package town.roles;

import java.util.List;

import town.events.TownEvent;
import town.persons.Attributes;
import town.persons.Person;

public interface GameRole
{
	/**
	 * Get the planned event.
	 * @return The planned event.
	 */
	TownEvent getEvent(Person user);

	Attributes getAttributes();

	List<Person> getPossibleTargets(Person user);

	String ability(Person user, List<Person> list);

	boolean hasWon(Person user);

	boolean canWin(Person user);

	void win(Person user);

	String getHelp();

	String getName();

	/**
	 * Cancel the planned action.
	 * @return A string meant for the user to read.
	 */
	default String cancelAction(Person user)
	{
		if (getEvent(user) == null) return "There's no action to cancel";
		user.getGame().removeEvent(getEvent(user));
		return "Action canceled";
	}
}
