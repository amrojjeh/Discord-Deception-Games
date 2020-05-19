package town;

import java.util.Timer;
import java.util.TimerTask;

public class Day extends Phase
{	
	public Day()
	{
		super(4000, new Accusation());
	}
	
	@Override
	public void run()
	{
		Timer timer = new Timer("Phase Timer");
		timer.schedule(next, time);	
	}
}
