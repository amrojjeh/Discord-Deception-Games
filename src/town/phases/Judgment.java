package town.phases;

import town.persons.Person;

//Judgment is the moment of fate. All players still alive vote on the life of the defendant.
public class Judgment extends Phase {
	Person defendant;
	
	public Judgment(PhaseManager pm, Person p)
	{
		super(pm);
		defendant = p;
	}

	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", defendant.getNickName() + " has been put on trial!");
		//TODO: Open daytime text/voice channel back up again.
		//TODO: Implement the vote of fate.
	}

	//ends the phase, sending out a global message of this fact.
	@Override
	public void end()
	{
		//		System.out.println("Ending day...");
	}

	//After Judgment, the results are revealed (Verdict)
	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Verdict(pm, defendant);
	}

	//Duration: 15-20 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}