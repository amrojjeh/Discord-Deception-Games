package town.persons;

import java.util.ArrayList;

import town.DiscordGame;
import town.events.MurderTownEvent;
import town.events.TownEvent;
import town.phases.Night;

// A Serial Killer can kill a person each night.
public class SerialKiller extends Person
{
	static int amount = 0;
	private TownEvent event;


	public SerialKiller(DiscordGame game, int num, Long id)
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
		getGame().sendMessageToTextChannel("system", "**Serial Killers have won!**").queue((msg) -> getGame().endGame());
	}

	@Override
	public String ability(ArrayList<Person> references)
	{
		if (references.isEmpty())
			return "There's no person to kill. `tos.kill 1` to kill the first person shown in `tos.party`.";
		if (references.size() > 1)
			return "Cannot kill more than one person at once. `tos.kill 1` to kill the first person show in `tos.party`.";
		if (!(getGame().getCurrentPhase() instanceof Night))
			return "Serial Killers can only kill during the night.";

		// You can't kill yourself
		if (references.get(0) == this)
			return "You can't kill yourself.";

		String msg = "";

		if (event != null)
		{
			msg += "You've changed your mind.\n";
			cancel();
		}

		event = new MurderTownEvent(getGame(), this, references.get(0));
		getGame().addEvent(event);

		return msg + String.format("You will kill <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public String cancel()
	{
		if (event == null) return "There's no action to cancel";
		getGame().removeEvent(event);
		return "Action canceled";
	}

	@Override
	public String getHelp()
	{
		return "SERIAL KILLER (SK)\n" +
				"Serial Killer wins with other serial killers. His goal is to kill anyone who isn't an SK.\n" +
				"Ability: Can kill one person every night. Ex: `tos.ability 2` kills person number two. Check a person's number with tos.party";
	}
}
