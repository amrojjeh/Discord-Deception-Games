package town.phases;

import java.util.Timer;

import town.DiscordGame;

public class PhaseManager
{
	private Timer timer;
	private Phase currentPhase;
	private DiscordGame dg;
	private boolean cancelled = false;

	//the PhaseManager cycles through each phase on a schedule, and does a few smaller things as well
	public PhaseManager(DiscordGame dg)
	{
		this.dg = dg;
	}

	public DiscordGame getGame()
	{
		return dg;
	}

	//starts the phase cycle, initially with a new day.
	public void start()
	{
		start(new Initial(this));
	}

	public void start(Phase startingPhase)
	{
		currentPhase = startingPhase;
		timer = new Timer("Phase timer");
		cancelled = false;
		startNextPhase(currentPhase);
	}

	//starts the next phase in the cycle.
	public void startNextPhase(Phase phase)
	{
		if (cancelled) return;
		currentPhase = phase;
		//schedule the next phase's run method AFTER the current phase is finished.
		timer.schedule(phase, phase.getDurationInSeconds() * 1000);
		//the start method of the phase type, not phase itself, is called
		phase.start();

	}

	public Phase getCurrentPhase()
	{
		return currentPhase;
	}

	public void end()
	{
		timer.cancel();
		timer.purge();
		cancelled = true;
	}

	public boolean hasEnded()
	{
		return cancelled;
	}
}
