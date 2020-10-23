package town.mafia.phases;

import town.DiscordGame;
import town.persons.DiscordGamePerson;
import town.phases.Phase;
import town.phases.PhaseManager;
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

		getGame().getPlayers().forEach(person -> person.sendMessage("Night " + getGameMode().getDayNum() + " started"));
		getPhaseManager().setWarningToAll(5);
	}

	public void checkVictory(DiscordGamePerson person)
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
		return new Morning(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 40;
	}
}
