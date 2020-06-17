package town.phases;

import net.dv8tion.jda.api.requests.RestAction;

public class Night extends Phase
{
	public Night(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public void start()
	{
		getGame().setChannelVisibility("daytime_discussion", true, false)
		.flatMap(perm -> getGame().setChannelVisibility("Daytime", false, false))
		.queue();

		RestAction<?> disconnect = getGame().discconectEveryoneFromVC("Daytime");
		if (disconnect != null) disconnect.queue();

		getGame().getPlayers().forEach(person -> person.sendMessage("Night " + getGame().getDayNum() + " started"));
		phaseManager.setWarningToAll(5);
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
		getGame().nextDayStarted();
	}

	@Override
	public Phase getNextPhase()
	{
		return new Morning(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 30;
	}
}
