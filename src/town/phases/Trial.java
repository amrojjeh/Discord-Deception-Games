package town.phases;

import net.dv8tion.jda.api.requests.RestAction;
import town.persons.Person;

//Trial occurs when the Town agrees to put someone under suspicion. They are given this phase, a small window,
//to defend themselves without any outside noise. After this, their fate is judged by the town.
public class Trial extends Phase
{
	Person defendant;
	int numTrials;

	public Trial(PhaseManager pm, Person p, int numTrials)
	{
		super(pm);
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
		//mute all but the defendant in text / voice daytime channel
		getGame().sendMessageToTextChannel("daytime_discussion", "<@" + defendant.getID() + ">, your trial has begun. All "
				+ "other players are muted. What is your defense? You have 30 seconds.")
		.flatMap(message -> getGame().muteExcept("Daytime", defendant))
		.flatMap(nothing -> getGame().removeReadExcept(defendant, "daytime_discussion"))
		.queue();

		phaseManager.setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		getGame().restoreTalking("Daytime", false)
		.queue();

		RestAction<?> action1 = getGame().resetPermissions(defendant, "daytime_discussion");
		if (action1 != null) action1.queue();
		action1 = getGame().setChannelVisibility("daytime_discussion", true, true);
		if (action1 != null) action1.queue();

	}

	@Override
	public Phase getNextPhase()
	{
		return new Judgment(phaseManager, defendant, numTrials);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 20;
	}
}