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
		getGame().sendMessageToTextChannel("daytime_discussion", "The night has started").queue();
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
		getGame().sendMessageToTextChannel("daytime_discussion", "The night has ended").queue();
	}

	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Day(pm);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}
