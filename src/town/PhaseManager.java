package town;

import java.util.Timer;

import net.dv8tion.jda.api.entities.MessageChannel;

public class PhaseManager
{
	MessageChannel system;
	private Timer timer;
	
	//the PhaseManager cycles through each phase on a schedule, and does a few smaller things as well
	public PhaseManager(DiscordGame dg)
	{
		timer = new Timer("Phase timer");
		//ERROR: textChannels doesn't exist yet
		this.system = dg.getJDA().getTextChannelById(dg.textChannels.get("system"));
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
		phase.start(system);
		//schedule the next phase's run method AFTER the current phase is finished.
		timer.schedule(phase, phase.getDuration());
	}
}
