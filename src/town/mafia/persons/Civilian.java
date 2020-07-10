package town.mafia.persons;

import town.DiscordGame;
import town.persons.Person;
import town.roles.GameRole;

// Civilian is NOT A REAL ROLE. This is a temporary useless town role to simulate games
public class Civilian extends Person
{
	public Civilian(DiscordGame game, int num, Long id)
	{
		super(game, num, id, GameRole.CIVILIAN);
	}

	@Override
	public String getHelp()
	{
		return "Civilian. You can't do anything, so wish best of luck.";
	}
}
