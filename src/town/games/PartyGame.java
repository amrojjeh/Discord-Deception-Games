package town.games;

import java.util.function.Consumer;

import town.DiscordGame;

public enum PartyGame
{
	TALKING_GRAVES("Talking Graves", 4, TalkingGraves::build);

	private final String name;
	private final int minimum;
	private final Consumer<DiscordGame> gameBuild;

	PartyGame(String name, int minimum, Consumer<DiscordGame> gameBuild)
	{
		this.name = name;
		this.minimum = minimum;
		this.gameBuild = gameBuild;
	}

	public String getName()
	{
		return name;
	}

	public int getMinimum()
	{
		return minimum;
	}

	public void build(DiscordGame game)
	{
		gameBuild.accept(game);
	}
}
