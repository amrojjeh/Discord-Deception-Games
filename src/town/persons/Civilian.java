package town.persons;

import java.util.ArrayList;

import town.DiscordGame;

// Civilian is NOT A REAL ROLE. This is a temporary useless town role to simulate games
public class Civilian extends Person
{
	static int amount = 0;

	public Civilian(DiscordGame game, int num, Long id)
	{
		super(game, num, id, "Civilian", 0, 0, 6);
		amount++;
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
		// TODO: We can put commonly used victories in a static class
		return getGame().getPlayers().stream().filter((person) -> person instanceof Civilian && person.alive).count() ==
				getGame().getPlayers().stream().filter((person) -> person.alive).count();
	}

	@Override
	public void win()
	{
		getGame().sendMessageToTextChannel("system", "**Civilians have won!**").queue((msg) -> getGame().endGame());
	}

	@Override
	public String ability(ArrayList<Person> references)
	{
		return "Civilian has no ability";
	}

	@Override
	public String cancel()
	{
		return "Civilian has no ability";
	}

	@Override
	public String getHelp()
	{
		return "Civilian is temporary. No commands, you just kind of die no matter what.";
	}
}
