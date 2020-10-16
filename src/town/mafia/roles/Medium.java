package town.mafia.persons;

import town.DiscordGame;
import town.mafia.phases.Morning;
import town.mafia.phases.Night;
import town.persons.Person;
import town.phases.Phase;
import town.roles.GameRole;

//Medium can talk to the dead at night.
public class Medium extends Person
{
	public Medium(DiscordGame game, int num, Long id)
	{
		super(game, num, id, GameRole.MEDIUM);
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
