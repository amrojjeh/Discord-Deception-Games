package town.phases;

public class End extends Phase
{
	public End(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public Phase getNextPhase()
	{
		return new End(phaseManager);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 60;
	}

	@Override
	public void start()
	{
		getGame().getPlayers().forEach(player -> player.mute(false));
		getGame().sendMessageToTextChannel("daytime_discussion", "The game has ended! You can either `!delete` the server or `!transfer` the server. In 60 seconds if no choice is made, the server will delete itself. (To transfer, the party leader must be in the server)")
		.queue();
	}

	@Override
	public void end()
	{
		delete();
	}

	public void transfer()
	{
		phaseManager.end();
		getGame().transferOrDelete();
	}

	public void delete()
	{
		phaseManager.end();
		getGame().sendMessageToTextChannel("daytime_discussion", "!delete").queue();
	}
}
