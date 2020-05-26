package town.persons;

import town.DiscordGame;
import town.events.MurderTownEvent;

// A Serial Killer can kill a person each night.
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

	@Override
	public boolean hasWon()
	{
		return getGame().getPlayers().stream().filter((person) -> person instanceof SerialKiller && person.alive).count() ==
				getGame().getPlayers().stream().filter((person) -> person.alive).count();
	}

	@Override
	public void win()
	{
		getGame().sendMessageToTextChannel("system", "**Serial Killers have won!**");
		getGame().endGame();
	}
}
