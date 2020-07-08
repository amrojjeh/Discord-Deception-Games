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

	public DiscordGame getGame()
	{
		return dg;
	}

	public void start(DiscordGame game, Phase startingPhase)
	{
		dg = game;
		currentPhase = startingPhase;
		timer = new Timer("Phase timer");
		cancelled = false;
		startNextPhase(currentPhase);
	}

	public void startNextPhase(Phase phase)
	{
		if (hasEnded()) return;
		currentPhase = phase;
		timer.schedule(phase, phase.getDurationInSeconds() * 1000);
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

	public void setWarningToAll(int seconds)
	{
		if (cancelled) throw new IllegalStateException("PhaseManager is currently cancelled, can't set a warning.");
		if (currentPhase == null) throw new IllegalStateException("There's no phase to warn before ending.");
		timer.schedule(new Warning(this, true), (currentPhase.getDurationInSeconds() - seconds) * 1000);
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
	final PhaseManager pm;
	final boolean ALL_FLAG;

	Warning(PhaseManager manager)
	{
		this(manager, false);
	}

	Warning(PhaseManager manager, boolean sendToAll)
	{
		pm = manager;
		ALL_FLAG = sendToAll;
	}

	@Override
	public void run()
	{
		if (!ALL_FLAG)
		{
			pm.getGame().sendMessageToTextChannel("daytime_discussion", "5 seconds before the phase ends!").queue();
			pm.cancelWarning();
		}
		else
			pm.getGame().getPlayersCache().forEach(person -> person.sendMessage("5 seconds before the phase ends!"));
	}
}
