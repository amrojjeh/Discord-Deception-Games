package town.persons;

import java.util.List;

import town.DiscordGame;
import town.TownRole;

// Civilian is NOT A REAL ROLE. This is a temporary useless town role to simulate games
public class Civilian extends Person
{
	private static boolean hasWon = false;

	public Civilian(DiscordGame game, int num, Long id)
	{
		super(game, num, id, TownRole.CIVILIAN);
	}

	@Override
	public boolean canWin()
	{
		// TODO: We can put commonly used victories in a static class
		return getGame().getPlayers().stream().filter((person) -> person instanceof Civilian && person.alive).count() ==
				getGame().getPlayers().stream().filter((person) -> person.alive).count();
	}

	@Override
	public boolean hasWon()
	{
		return hasWon;
	}

	@Override
	public void win()
	{
		hasWon = true;
		getGame().sendMessageToTextChannel("daytime_discussion", "**Civilians have won!**", (msg) -> getGame().endGame());
	}

	@Override
	public String ability(List<Person> references)
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
