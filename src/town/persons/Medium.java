package town.persons;

import town.DiscordGame;
import town.GameFaction;
import town.GameRole;
import town.phases.Morning;
import town.phases.Night;
import town.phases.Phase;

//Medium can talk to the dead at night.
public class Medium extends Person{
	public Medium(DiscordGame game, int num, Long id)
	{
		super(game, num, id, GameRole.MEDIUM);
	}

	@Override
	public boolean canWin()
	{
		// TODO: We can put commonly used victories in a static class
		return getGame().getPlayers().stream().filter((person) -> person.getType().getFaction() == GameFaction.TOWN
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
		getGame().sendMessageToTextChannel("daytime_discussion", "**Town has won!**").queue();
		getGame().endGame();
	}

	@Override
	public String getHelp()
	{
		return "You can talk to the dead during the night.";
	}

	@Override
	public void onPhaseChange(Phase phase)
	{
		super.onPhaseChange(phase);
		if (phase instanceof Night && isAlive())
			getGame().setChannelVisibility(this, "the_afterlife", true, true).queue();
		else if (phase instanceof Morning && isAlive())
			getGame().getTextChannel("the_afterlife").getPermissionOverride(getGame().getMemberFromGame(this)).delete().queue();
	}
}
