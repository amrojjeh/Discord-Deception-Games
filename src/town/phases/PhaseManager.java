package town.phases;

import java.util.Timer;
import java.util.TimerTask;

import town.DiscordGame;

public class PhaseManager
{
	private Timer timer;
	private Phase currentPhase;
	private DiscordGame dg;
	private Warning currentWarning;
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
		start(new FirstDay(this));
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
		if (hasEnded()) return;
		currentPhase = phase;
		//schedule the next phase's run method AFTER the current phase is finished.
		timer.schedule(phase, phase.getDurationInSeconds() * 1000);
		//the start method of the phase type, not phase itself, is called
		phase.start();
	}

	public void setWarningInSeconds(int seconds)
	{
		if (cancelled) throw new IllegalStateException("PhaseManager is currently cancelled, can't set a warning.");
		if (currentWarning != null) throw new IllegalStateException("There's a warning already set.");
		if (currentPhase == null) throw new IllegalStateException("There's no phase to warn before ending.");
		currentWarning = new Warning(this);
		timer.schedule(currentWarning, (currentPhase.getDurationInSeconds() - seconds) * 1000);
	}

	public void cancelWarning()
	{
		currentWarning.cancel();
		currentWarning = null;
	}

	public Phase getCurrentPhase()
	{
		return currentPhase;
	}

	public void end()
	{
		if (hasEnded()) return;
		timer.cancel();
		timer.purge();
		currentPhase = null;
		currentWarning = null;
		cancelled = true;
	}

	public boolean hasEnded()
	{
		return cancelled;
	}
}

class Warning extends TimerTask
{
	PhaseManager pm;
	Warning(PhaseManager manager)
	{
		pm = manager;
	}

	@Override
	public void run()
	{
		pm.getGame().sendMessageToTextChannel("daytime_discussion", "5 seconds before the phase ends!");
		pm.cancelWarning();
	}
}
