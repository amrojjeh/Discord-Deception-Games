package town.phases;

//Accusation is the phase where players can vote to put another player on trial. If a player receives enough
//votes, the defense phase begins for that player. There can be 3 trials in a day. Night phase happens otherwise.
public class Accusation extends Phase
{
	public Accusation(PhaseManager pm)
	{
		super(pm);
	}

	//begins Accusation. Players can now use the command to accuse other players. One accusation at a time!!
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("system", "Accusation started");
	}

	//ends the Accusation phase. HOWEVER, the phase may be resumed later, depending on if a trial has begun.
	@Override
	public void end()
	{
		getGame().sendMessageToTextChannel("system", "Accusation has ended");
	}

	//gets the next phase in line: could be Defense (if a player has enough votes) or Night
	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Night(pm);
	}

	//Duration: 30 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 30;
	}
}
