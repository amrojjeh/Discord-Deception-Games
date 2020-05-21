package town.persons;

import town.DiscordGame;
import town.events.MurderTownEvent;

//the Serial Killer can kill a person each night
public class SerialKiller extends Person
{
	public SerialKiller(DiscordGame game, int num, String id)
	{
		super(game, num, id);
	}
	
	@Override
	public void onMurder(MurderTownEvent e)
	{
		e.standard(this);
	}

	// Name of this role
	@Override
	public String getRoleName()
	{
		return "Serial Killer";
	}

	@Override
	public int getAttackStat()
	{
		return 1;
	}

	@Override
	public int getDefenseStat()
	{
		return 1;
	}

	@Override
	public int getPriority()
	{
		return 3;
	}

}
