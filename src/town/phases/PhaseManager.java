package town.phases;

import java.util.Timer;

import town.DiscordGame;

public class PhaseManager
{
	private Timer timer;
	private Phase currentPhase;
	private DiscordGame dg;
	
	//the PhaseManager cycles through each phase on a schedule, and does a few smaller things as well
	public PhaseManager(DiscordGame dg)
	{
		timer = new Timer("Phase timer");
		this.dg = dg;
	}
	
	public DiscordGame getGame() 
	{
		return dg;
	}
	
	//starts the phase cycle, initially with a new day.
	public void start() 
	{
		currentPhase = new Day(this);
		startNextPhase(currentPhase);
	}
	
	//starts the next phase in the cycle.
	public void startNextPhase(Phase phase) 
	{
		currentPhase = phase;
		//the start method of the phase type, not phase itself, is called
		phase.start();
		//schedule the next phase's run method AFTER the current phase is finished.
		timer.schedule(phase, phase.getDurationInSeconds() * 1000);
	}
	
	public Phase getCurrentPhase()
	{
		return currentPhase;
	}
}
