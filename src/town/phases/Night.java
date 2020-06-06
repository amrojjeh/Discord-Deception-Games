package town.phases;

public class Night extends Phase
{
	public Night(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public void start()
	{
		getGame().setChannelVisibility("daytime_discussion", true, false);
		getGame().setChannelVisibility("Daytime", false, false);
		getGame().discconectEveryoneFromVC("Daytime");
		getGame().sendMessageToTextChannel("daytime_discussion", "The night has started");
		phaseManager.setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
		getGame().sendMessageToTextChannel("daytime_discussion", "The night has ended");
	}

	@Override
	public Phase getNextPhase()
	{
		return new Day(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}
