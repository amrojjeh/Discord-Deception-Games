package town.phases;

import town.DiscordGame;
import town.persons.Person;
import town.util.RestHelper;

public class Night extends Phase
{
	public Night(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public void start()
	{
		getGame().getPlayers().forEach(person -> checkVictory(person));

		if (getGame().hasEnded())
			return;

		RestHelper.queueAll
		(
				getGame().toggleVC("Daytime", false),
				getGame().setChannelVisibility("player", "daytime_discussion", true, false)
		);

		getGame().getPlayers().forEach(person -> person.sendMessage("Night " + getGame().getDayNum() + " started"));
		phaseManager.setWarningToAll(5);
	}

	public void checkVictory(Person person)
	{
		if (!person.hasWon() && person.canWin())
			person.win();
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
		getGame().nextDayStarted();
	}

	@Override
	public Phase getNextPhase()
	{
		return new Morning(getGame(), phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 40;
	}
}
