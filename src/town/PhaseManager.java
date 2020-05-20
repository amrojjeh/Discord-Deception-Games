package town;

import java.util.Timer;

public class PhaseManager
{
	private Timer timer;
	
	//the PhaseManager cycles through each phase on a schedule, and does a few smaller things as well
	public PhaseManager()
	{
		timer = new Timer("Phase timer");
	}
	
	//starts the phase cycle, initially with a new day.
	public void start() 
	{
		startNextPhase(new Day(this));
	}
	
	//starts the next phase in the cycle.
	public void startNextPhase(Phase phase) 
	{
		//the start method of the phase type, not phase itself, is called
		phase.start();
		//schedule the next phase's run method AFTER the current phase is finished.
		timer.schedule(phase, phase.getDuration());
	}
}
