package town.phases;

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
		getGame().sendMessageToTextChannel("daytime_discussion", "<@" + defendant.getID() + ">, your trial has begun. All "
				+ "other players are muted. What is your defense? You have 30 seconds.");
		//mute all but the defendant in text / voice daytime channel
		getGame().muteExcept("Daytime", defendant);
		getGame().removeReadExcept(defendant, "daytime_discussion");
		phaseManager.setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		getGame().restoreTalking("Daytime");
		getGame().restoreRead(defendant, "daytime_discussion");
	}

	@Override
	public Phase getNextPhase()
	{
		return new Judgment(phaseManager, defendant, numTrials);
	}

	//Duration: 20-30 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}