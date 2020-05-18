package town;

import java.util.Timer;

public class Accusation extends Phase{
	int time = 5000;
	Phase next = new Day();

	@Override
	public void run() {
		Timer timer = new Timer("Phase Timer");
		timer.schedule(next, time);
		
	}
	
}
