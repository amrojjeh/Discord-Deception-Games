package town;

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
	public void start() 
	{
//		System.out.println("Starting day...");
	}
	
	//ends the phase, sending out a global message of this fact.
	@Override
	public void end() 
	{
//		System.out.println("Ending day...");
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
