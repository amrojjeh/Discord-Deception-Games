package town.persons;

import town.DiscordGame;
import town.events.MurderTownEvent;

//the Serial Killer can kill a person each night
public class SerialKiller extends Person
{
	static int amount = 0;
	
	public SerialKiller(DiscordGame game, int num, String id)
	{
		super(game, num, id, "Serial Killer", 1, 1, 3);
		amount++;
	}
	
	@Override
	public void onMurder(MurderTownEvent e)
	{
		e.standard(this);
	}

	public static int getAmount()
	{
		return amount;
	}
	
	public static int getMaxAmount()
	{
		return 0;
	}
}
