import java.time.Instant;

public class GameStart extends Phase
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
		return null;
	}
}
