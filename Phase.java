import java.time.Instant;

public abstract class Phase
{
	Instant starting;

	abstract long getTime();
	abstract Phase nextPhase();
	abstract String getName();

	public void start()
	{
		starting = Instant.now();
	}

	public boolean isOver()
	{
		return Instant.now().minusSeconds(starting.getEpochSecond()).getEpochSecond() >= getTime();
	}
}
