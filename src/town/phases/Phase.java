package town.phases;

import java.util.TimerTask;

import town.DiscordGame;

public abstract class Phase extends TimerTask
{
	//Phase Manager that drives the phases
	PhaseManager phaseManager;
	private DiscordGame game;

	public Phase(DiscordGame game, PhaseManager pm)
	{
		phaseManager = pm;
		this.game = game;
		getGame().getPlayersCache().forEach(person -> person.onPhaseChange(this));
	}

	public DiscordGame getGame()
	{
		return game;
	}

	public void start() { }
	public void end() { }

	//run() ends the current phase, and starts the next one through the phaseManager.
	@Override
	public void run()
	{
		end();
		phaseManager.startNextPhase(getNextPhase());
	}

	public abstract Phase getNextPhase();
	public abstract int getDurationInSeconds();
}
