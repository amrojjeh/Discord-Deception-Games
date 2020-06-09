package town.persons;

import java.util.List;

import town.DiscordGame;
import town.TownFaction;
import town.TownRole;
import town.phases.Night;

//Medium can talk to the dead at night.
public class Medium extends Person{
	public Medium(DiscordGame game, int num, Long id)
	{
		super(game, num, id, TownRole.MEDIUM);
	}

	@Override
	public boolean canWin()
	{
		// TODO: We can put commonly used victories in a static class
		return getGame().getPlayers().stream().filter((person) -> person.getType().getFaction() == TownFaction.TOWN
				&& person.alive).count() == getGame().getPlayers().stream().filter((person) -> person.alive).count();
	}

	@Override
	public boolean hasWon()
	{
		return getGame().hasTownFactionWon(getType().getFaction());
	}

	@Override
	public void win()
	{
		getGame().winTownFaction(getType().getFaction());
		getGame().sendMessageToTextChannel("daytime_discussion", "**Town has won!**", (msg) -> getGame().endGame());
	}

	@Override
	public String ability(List<Person> references)
	{
		/*
		if ((getGame().getCurrentPhase() instanceof Night)) {
			getGame().setChannelVisibility(this, "the_afterlife", true, true);
		}
		else {
			getGame().setChannelVisibility(this, "the_afterlife", false, false);
		}
		*/
		return "Mediums don't have an ability!";
	}

	@Override
	public String cancel()
	{
		return "The Medium has no action.";
	}

	@Override
	public String getHelp()
	{
		return "You can talk to the dead during the night.";
	}
}
