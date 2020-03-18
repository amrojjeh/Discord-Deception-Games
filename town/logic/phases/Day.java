package town.logic.phases;

public class Day extends Phase
{
	public long getTime()
	{
		return 40;
	}

	public String getName()
	{
		return "Day";
	}

	public Phase nextPhase()
	{
		return null;
	}
}
