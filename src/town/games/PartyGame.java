package town.games;

import java.util.function.Consumer;

import town.DiscordGame;

public enum PartyGame
{
	TALKING_GRAVES("Talking Graves", "Figure out who the killer is by talking to the dead!", 1, 4, TalkingGraves::build, TalkingGraves::buildRand),
	MEDIC("Medic!", "Like talking graves, but instead of a medium, there's a doctor.", 2, 4, Medic::build, Medic::buildRand),
	MASHUP("Mashup", "Play with all the roles!", 3, 4, Mashup::build);

	private final String name, description;
	private final int reference, minimum;
	private final Consumer<DiscordGame> gameBuild, randBuild;

	PartyGame(String name, String descr, int ref, int minimum, Consumer<DiscordGame> gameBuild)
	{
		this(name, descr, ref, minimum, gameBuild, null);
	}

	PartyGame(String name, String descr, int ref, int minimum, Consumer<DiscordGame> gameBuild, Consumer<DiscordGame> random)
	{
		this.name = name;
		this.minimum = minimum;
		this.gameBuild = gameBuild;
		description = descr;
		reference = ref;
		randBuild = random;
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

	public boolean hasRandom()
	{
		return randBuild != null;
	}

	public void build(DiscordGame game, boolean buildRandom)
	{
		if (buildRandom && hasRandom()) randBuild.accept(game);
		else gameBuild.accept(game);
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
