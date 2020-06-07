package town.phases;

import town.persons.Person;

//Trial occurs when the Town agrees to put someone under suspicion. They are given this phase, a small window,
//to defend themselves without any outside noise. After this, their fate is judged by the town.
public class Trial extends Phase
{
	Person defendant;

	public Trial(PhaseManager pm, Person p)
	{
		super(pm);
		defendant = p;
	}

	public Person getDefendant()
	{
		return defendant;
	}

	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", defendant.getNickName() + ", your trial has begun. All "
				+ "other players are muted. What is your defense? You have 30 seconds.");
		//mute all but the defendant in text / voice daytime channel
		getGame().muteExcept("Daytime", defendant);
		getGame().setChannelVisibility("daytime_discussion", true, false);
		getGame().setChannelVisibility(defendant, "daytime_discussion", true, true);
		phaseManager.setWarningInSeconds(5);
	}

	// ends the phase, sending out a global message of this fact.
	@Override
	public void end()
	{
		getGame().restoreTalking("Daytime");
		getGame().resetVisibility(defendant, "daytime_discussion");
		getGame().setChannelVisibility("daytime_discussion", true, true);
	}

	//After the defendant has spoken, players briefly discuss what to do and their fate is voted upon
	@Override
	public Phase getNextPhase()
	{
		return new Judgment(phaseManager, defendant);
	}

	//Duration: 20-30 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}