package town.phases;

public class Initial extends Phase
{
	// TODO: Check for members as they join, this could actually be done without this class
	public Initial(PhaseManager pm)
	{
		super(pm);
		getGame().sendMessageToTextChannel("daytime_discussion", "Waiting for players...");
	}

	@Override
	public Phase getNextPhase()
	{
		if (getGame().getPlayers().size() == getGame().getGameGuild().getMemberCount() - 1)
			return new FirstDay(phaseManager);
		return new Initial(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 10;
	}
}
