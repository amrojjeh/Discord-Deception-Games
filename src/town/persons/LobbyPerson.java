package town.persons;

import java.util.List;

import town.DiscordGame;

// This class is only used for when plays join the lobby, as I can't generate roles as people join.
public class LobbyPerson extends Person
{
	public LobbyPerson(DiscordGame game, int refNum, long id)
	{
		super(game, refNum, id);
	}

	@Override
	public String ability(List<Person> list)
	{
		return "Role has not been assigned yet.";
	}

	@Override
	public boolean hasWon()
	{
		return false;
	}

	@Override
	public boolean canWin()
	{
		return false;
	}

	@Override
	public void win() { }

	@Override
	public String getHelp()
	{
		return null;
	}
}
