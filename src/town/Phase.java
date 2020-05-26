package town;

import java.util.TimerTask;

import net.dv8tion.jda.api.entities.MessageChannel;

public abstract class Phase extends TimerTask
{
	//Phase Manager that drives the phases
	PhaseManager phaseManager;
	MessageChannel msg;
	
	public Phase(PhaseManager pm) 
	{
		phaseManager = pm;
	}
	
	public void start(MessageChannel mc) { }
	public void end(MessageChannel mc) { }
	
	//run() ends the current phase, and starts the next one through the phaseManager.
	@Override
	public void run() 
	{
		end(msg);
		phaseManager.startNextPhase(getNextPhase(phaseManager));
	}
	
	public abstract Phase getNextPhase(PhaseManager pm);
	public abstract int getDuration();
}
