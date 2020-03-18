package town.logic.phases;
import java.time.Instant;

public abstract class Phase
{
	Instant starting;

	public abstract long getTime();
	public abstract Phase nextPhase();
	public abstract String getName();

	public void start()
	{
		starting = Instant.now();
	}

	public boolean isOver()
	{
		return Instant.now().minusSeconds(starting.getEpochSecond()).getEpochSecond() >= getTime();
	}
}
