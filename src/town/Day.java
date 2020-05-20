package town;

public class Day extends Phase
{
	public Day(PhaseManager pm) 
	{
		super(pm);
	}
	
	@Override
	public void start() 
	{
		System.out.println("Starting day...");
	}
	
	@Override
	public void end() 
	{
		System.out.println("Ending day...");
	}
	
	@Override
	public Phase getNextPhase(PhaseManager pm) 
	{
		return new Accusation(pm);
	}
	
	@Override
	public int getDuration()
	{
		return 3000;
	}
}
