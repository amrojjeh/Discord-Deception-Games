//package town.games;
//
//import java.io.FileNotFoundException;
//import java.util.Set;
//
//import town.DiscordGame;
//import town.JavaHelper;
//import town.TownRole;
//import town.games.parser.GameParser;
//
//// TODO: Instead of storing enums, load all the game modes by searching for .game files
//public class PartyGame
//{
//	public static PartyGame loadFromFile(String fileName)
//	{
//
//	}
//
//	TALKING_GRAVES("Talking Graves", "Figure out who the killer is by talking to the dead!", 1, "TalkingGraves.game"),
//	MEDIC("Medic!", "", 2, "Medic!.game"),
//	MASHUP("Mashup", "Play with all the roles!", 3, new Mashup());
//
//	private final String name, description;
//	private final int reference;
//	private final GeneralGame game;
//
//	PartyGame(String name, String descr, int ref, GeneralGame game)
//	{
//		this.name = name;
//		description = descr;
//		reference = ref;
//		this.game = game;
//	}
//	PartyGame(String name, String descr, int ref, String fileName)
//	{
//		this.name = name;
//		description = descr;
//		reference = ref;
//
//		String fileContents = "";
//		try
//		{
//			fileContents = JavaHelper.readFile(fileName);
//		}
//		catch(FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//
//		this.game = GameParser.parseGeneralGame(fileContents);
//	}
//
//
//	public String getName()
//	{
//		return name;
//	}
//
//	public int getMinimum()
//	{
//		return game.getMinimumTotalPlayers();
//	}
//
//	public int getReference()
//	{
//		return reference;
//	}
//
//	public String getDescription()
//	{
//		return description;
//	}
//
//	public Set<TownRole> getTownRoles()
//	{
//		return game.getRoles();
//	}
//
//	public void build(DiscordGame discordGame, boolean buildRandom)
//	{
//		if (buildRandom)
//			game.buildRand(discordGame);
//		else
//			game.build(discordGame);
//	}
//
//	public static PartyGame getGameFromName(String name)
//	{
//		for (PartyGame party : PartyGame.values())
//		{
//			if (party.getName().toLowerCase().contentEquals(name.toLowerCase()))
//					return party;
//		}
//		return null;
//	}
//
//	public static PartyGame getGameFromNum(int refNum)
//	{
//		for (PartyGame party : PartyGame.values())
//		{
//			if (party.getReference() == refNum)
//					return party;
//		}
//		return null;
//	}
//
//	public static PartyGame getGame(String nameOrRef)
//	{
//		PartyGame gameMode;
//		gameMode = getGameFromName(nameOrRef);
//		if (gameMode != null)
//			return gameMode;
//		try
//		{
//			int ref = Integer.parseInt(nameOrRef);
//			gameMode = getGameFromNum(ref);
//			return gameMode;
//		}
//		catch (NumberFormatException e) {}
//		return null;
//	}
//
//}
