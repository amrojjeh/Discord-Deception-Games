package town.events;

import javax.annotation.Nullable;

import town.discordgame.DiscordGame;
import town.persons.DiscordGamePerson;

public interface TownEvent extends Comparable<TownEvent>
{
	DiscordGame getGame();
	void standard(DiscordGamePerson person);
	@Nullable DiscordGamePerson getTarget();
	int getPriority();

	default void postDispatch() { }

	@Override
	default int compareTo(TownEvent e)
	{
		return getPriority() - e.getPriority();
	}

	default boolean isVisitingTarget(DiscordGamePerson person)
	{
		return person.getTownEvent() != null && person.getTownEvent().getTarget() == getTarget();
	}
}
