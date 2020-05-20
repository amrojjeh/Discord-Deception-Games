package town;

public class Accusation extends Phase
{
	public Accusation(PhaseManager pm) 
	{
		super(pm);
	}
	
	@Override
	public void start() 
	{
		System.out.println("Starting accusation...");
	}
	
	@Override
	public void end() 
	{
		System.out.println("Ending accusation...");
	}
	
	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Day(pm); // TODO: Replace with the actual next phase
	}
	
	@Override
	public int getDuration()
	{
		return 3000;
	}
}
