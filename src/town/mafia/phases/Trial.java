package town.mafia.phases;

import town.DiscordGame;
import town.persons.Person;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.util.RestHelper;

//Trial occurs when the Town agrees to put someone under suspicion. They are given this phase, a small window,
//to defend themselves without any outside noise. After this, their fate is judged by the town.
public class Trial extends Phase
{
	Person defendant;
	int numTrials;

	public Trial(DiscordGame game, PhaseManager pm, Person p, int numTrials)
	{
		super(game, pm);
		defendant = p;
		this.numTrials = numTrials;
	}

	public Person getDefendant()
	{
		return defendant;
	}

	@Override
	public void start()
	{
		RestHelper.queueAll
		(
			getGame().sendMessageToTextChannel("daytime_discussion", "<@" + defendant.getID() + ">, your trial has begun. All "
					+ "other players are muted. What is your defense? You have 30 seconds."),
			getGame().setChannelVisibility("player", "daytime_discussion", true, false),
			getGame().muteAllInRoleExcept("player", true, defendant)
		);

		getPhaseManager().setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		RestHelper.queueAll
		(
			getGame().muteAllInRole("player", false),
			getGame().setChannelVisibility("player", "daytime_discussion", true, true)
		);
	}

	@Override
	public Phase getNextPhase()
	{
		return new Judgment(getGame(), getPhaseManager(), defendant, numTrials);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 30;
	}
}