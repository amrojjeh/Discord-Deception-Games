package town.logic.phases;

public class Trial extends Phase
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
		return new Night();
	}
}
