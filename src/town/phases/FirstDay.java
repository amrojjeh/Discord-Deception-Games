package town.phases;

import town.DiscordGame;

//Daytime is the phase where players can discuss what is happening. There are no features other than
//a voice and text chat that all can use.
public class FirstDay extends Day
{
	public FirstDay(DiscordGame game, PhaseManager pm)
	{
		super(game, pm);
	}

	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "Day " + getGame().getDayNum() + " started")
		.flatMap(msg -> getGame().setChannelVisibility("player", "daytime_discussion", true, true))
		.queue();
		getGame().getPlayers().forEach((person) -> checkVictory(person));
		phaseManager.setWarningInSeconds(5);
	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(getGame(), phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}
}
