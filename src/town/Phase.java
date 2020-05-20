package town;

import java.util.TimerTask;

public abstract class Phase extends TimerTask
{
	//Phase Manager that drives the phases
	PhaseManager phaseManager;
	
	public Phase(PhaseManager pm) 
	{
		phaseManager = pm;
	}
	
	public void start() { }
	public void end() { }
	
	//run() ends the current phase, and starts the next one through the phaseManager.
	@Override
	public void run() 
	{
		end();
		phaseManager.startNextPhase(getNextPhase(phaseManager));
	}
	
	public abstract Phase getNextPhase(PhaseManager pm);
	public abstract int getDuration();
}
