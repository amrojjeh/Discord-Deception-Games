package town.mafia.phases;

import town.discordgame.DiscordGame;
import town.persons.DiscordGamePerson;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.roles.Role;
import town.util.RestHelper;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class Day extends Phase
{
	public Day(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "Day " + getGame().getDayNum() + " started")
		.flatMap(msg -> getGame().setChannelVisibility("player", "daytime_discussion", true, true))
		.queue();

		getGame().toggleVC("Daytime", true).queue();

		getGame().getPlayersCache().forEach((person) -> checkVictory(person));
		RestHelper.queueAll(getGame().getRole("dead").muteAllInRole(true));
		getPhaseManager().setWarningInSeconds(5);
	}

	public void checkVictory(DiscordGamePerson person)
	{
		Role role = person.getRole();
		if (!role.hasWon(person) && role.canWin(person))
			role.win(person);
	}

	@Override
	public Phase getNextPhase()
	{
		if (getGame().getAlivePlayers().size() > 2)
		return new Accusation(getGame(), getPhaseManager(), 3);
		else
		{
			getGame().sendMessageToTextChannel("daytime_discussion", "Accusation was skipped because there were only two people.").queue();
			return new Night(getGame(), getPhaseManager());
		}
	}

	@Override
	public int getDurationInSeconds()
	{
		return 60;
	}
}
