package town;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Phase{
	
	public void run() {
		Timer timer = new Timer("Phase Timer");
	}
	
	public abstract void start();
	public abstract void end();
	public abstract int duration();
}