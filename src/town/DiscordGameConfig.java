package town;

import town.games.GameMode;
import town.games.GameModeLoader;
import town.games.parser.GameParser;

public class DiscordGameConfig
{
	private String prefix = "pg.";
	private boolean noMinPlayers = false;
	private boolean isRand = false;
	private GameMode gameMode;

	public GameMode getGame()
	{
		return gameMode;
	}

	public String setGameMode(String gameName)
	{
		GameMode gameToChangeTo = GameModeLoader.getGameMode(gameName, false);
		if (gameToChangeTo == null)
			return "FAILED: Game **" + gameName + "** was not found.";

		gameMode = gameToChangeTo;
		return "Game mode was set to **" + gameMode.getName() + "**.";
	}

	public String setCustomGameMode(String rules)
	{
		try
		{
			GameMode gameMode = GameParser.parseGeneralGame(rules.strip());
			this.gameMode = gameMode;
		} catch(Exception e)
		{
			return "FAILED: " + e.getMessage();
		}
		return "Game mode was set to **Custom.**";
	}

	public String getPrefix()
	{
		return prefix;
	}

	public boolean isRandom()
	{
		return isRand;
	}

	public String setRandomMode(boolean randomVal)
	{
		isRand = randomVal;
		if (isRandom())
			return "Random mode was activated";
		return "Random mode was disabled";
	}

	public int getMin()
	{
		if (noMinPlayers) return 0;
		return gameMode.getMinimumTotalPlayers();
	}

	public void byPassMin(boolean value)
	{
		noMinPlayers = value;
	}
}
