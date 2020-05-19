package town;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PhaseManager extends TimerTask{
	
	LinkedList<Phase> phases;
	
	public void run() {
		phases.addLast(new Day());
		phases.addLast(new Accusation());
		
		for(Phase p : phases) {
			p.run();
			phases.remove(p);
			
		}
	}
}
