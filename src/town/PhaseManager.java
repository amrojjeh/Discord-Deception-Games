package town;

import java.util.Timer;
import java.util.TimerTask;

public class PhaseManager
{
	private Timer timer;
	
	public PhaseManager()
	{
		timer = new Timer("Phase timer");
		
	}
	
	public void start() 
	{
		startNextPhase(new Day(this));
	}
	
	public void startNextPhase(Phase phase) 
	{
		phase.start();
		timer.schedule(phase, phase.getDuration());
	}
}
