package town;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Phase extends TimerTask{
	int time;
	Phase next;
	
	public void startPhase() {
		Timer timer = new Timer("Phase Timer");
		timer.schedule(next, time);
	}
}
