package town.mafia.phases;

import town.DiscordGame;
import town.phases.Phase;
import town.phases.PhaseManager;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class FirstDay extends Day
{
	public FirstDay(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public void start()
	{
		getGame().toggleVC("Daytime", true).queue();
		getGame().sendMessageToTextChannel("daytime_discussion", "Day " + getGame().getDayNum() + " started")
		.flatMap(msg -> getGameMode().setChannelVisibility("player", "daytime_discussion", true, true))
		.queue();
		getGame().getPlayersCache().forEach((person) -> checkVictory(person));
		getPhaseManager().setWarningInSeconds(5);
	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}
