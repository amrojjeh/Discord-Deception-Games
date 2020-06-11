package town.games;

import java.util.function.Consumer;

import town.DiscordGame;

public enum PartyGame
{
	TALKING_GRAVES("Talking Graves", "Figure out who the killer is by talking to the dead!", 1, 4, TalkingGraves::build),
	MASHUP("Mashup", "Play with all the roles!", 2, 4, Mashup::build);

	private final String name, description;
	private final int reference, minimum;
	private final Consumer<DiscordGame> gameBuild;

	PartyGame(String name, String descr, int ref, int minimum, Consumer<DiscordGame> gameBuild)
	{
		this.name = name;
		this.minimum = minimum;
		this.gameBuild = gameBuild;
		description = descr;
		reference = ref;
	}

	public String getName()
	{
		return name;
	}

	public int getMinimum()
	{
		return minimum;
	}

	public int getReference()
	{
		return reference;
	}

	public String getDescription()
	{
		return description;
	}

	public void build(DiscordGame game)
	{
		gameBuild.accept(game);
	}

	public static PartyGame getGameFromName(String name)
	{
		for (PartyGame party : PartyGame.values())
		{
			if (party.getName().toLowerCase().contentEquals(name.toLowerCase()))
					return party;
		}
		return null;
	}

	public static PartyGame getGameFromNum(int refNum)
	{
		for (PartyGame party : PartyGame.values())
		{
			if (party.getReference() == refNum)
					return party;
		}
		return null;
	}

	public static PartyGame getGame(String nameOrRef)
	{
		PartyGame gameMode;
		gameMode = getGameFromName(nameOrRef);
		if (gameMode != null)
			return gameMode;
		try
		{
			int ref = Integer.parseInt(nameOrRef);
			gameMode = getGameFromNum(ref);
			return gameMode;
		}
		catch (NumberFormatException e) {}
		return null;
	}

}
