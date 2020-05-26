package town;

import net.dv8tion.jda.api.entities.MessageChannel;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class Day extends Phase
{
	public Day(PhaseManager pm) 
	{
		super(pm);
	}
	
	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start(MessageChannel mc) 
	{
		mc.sendMessage("The Day phase has begun.");
	}
	
	//ends the phase, sending out a global message of this fact.
	@Override
	public void end(MessageChannel mc) 
	{
		mc.sendMessage("The Day phase has ended.");
	}
	
	//After Daytime, the Accusation phase begins.
	@Override
	public Phase getNextPhase(PhaseManager pm) 
	{
		return new Accusation(pm);
	}
	
	//Duration: 50 seconds
	@Override
	public int getDuration()
	{
		return 3000;
	}
}
