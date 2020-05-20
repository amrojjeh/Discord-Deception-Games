package town;

import java.util.TimerTask;

public abstract class Phase extends TimerTask
{
	PhaseManager phaseManager;
	
	public Phase(PhaseManager pm) 
	{
		phaseManager = pm;
	}
	
	public void start() { }
	public void end() { }
	
	@Override
	public void run() 
	{
		end();
		phaseManager.startNextPhase(getNextPhase(phaseManager));
	}
	
	public abstract Phase getNextPhase(PhaseManager pm);
	public abstract int getDuration();
}
