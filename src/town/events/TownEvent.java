package town.events;

import javax.annotation.Nullable;

import town.DiscordGame;
import town.persons.Person;

public interface TownEvent extends Comparable<TownEvent>
{
	DiscordGame getGame();
	void standard(Person person);
	@Nullable Person getTarget();
	int getPriority();

	default void postDispatch() { }

	@Override
	default int compareTo(TownEvent e)
	{
		return getPriority() - e.getPriority();
	}
}
