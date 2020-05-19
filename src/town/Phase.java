package town;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Phase extends TimerTask{
	int time;
	Phase next;
	
	public Phase(int time, Phase next)
	{
		this.time = time;
		this.next = next;
	}
	
	public void startPhase() { }
	
	public void run()
	{
		Timer timer = new Timer("Phase Timer");
		timer.schedule(next, time);
	}
}
