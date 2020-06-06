package town.phases;

import town.persons.Person;

//the Verdict phase is short, it reveals who voted what- and what will happen to the defendant.
public class Verdict extends Phase{
	Person defendant;

	public Verdict(PhaseManager pm, Person p)
	{
		super(pm);
		defendant = p;
	}

	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "And now, the verdict...");
		//TODO: Reveal the votes and the fate of the defendant.
	}

	//ends the phase, sending out a global message of this fact.
	@Override
	public void end()
	{
		//		System.out.println("Ending day...");
	}

	//After the Verdict, either the Accusation phase restarts (Innocent) or the defendant is executed (Guilty)
	@Override
	public Phase getNextPhase()
	{
		//TODO: Move to Accusation or Last Words depending on the outcome.
		return new Night(phaseManager);
	}

	//Duration: very short
	@Override
	public int getDurationInSeconds()
	{
		return 5;
	}
}
