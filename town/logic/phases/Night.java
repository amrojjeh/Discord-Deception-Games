package town.logic.phases;

public class Night extends Phase
{
	public long getTime()
	{
		return 12;
	}

	public String getName()
	{
		return "Start";
	}

	public Phase nextPhase()
	{
		return new Day();
	}
}
