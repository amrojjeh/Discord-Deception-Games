package io.github.dinglydo.town.games;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import io.github.dinglydo.town.games.parser.GameParser;
import io.github.dinglydo.town.util.JavaHelper;

public class GameModeLoader
{
	public static ArrayList<GameMode> loadedGameModes = null;
	public static ArrayList<GameMode> specialGameModes = new ArrayList<GameMode>();
	public static String defaultPath = "Games";

	public static ArrayList<GameMode> getGames(boolean forceReload)
	{
		loadedGameModes = new ArrayList<GameMode>();
		if (forceReload || loadedGameModes == null)
		{
			loadedGameModes.addAll(loadGameModesFromFolder(defaultPath));
			loadedGameModes.addAll(loadSpecialGameModes());

		}
		return loadedGameModes;
	}

	public static ArrayList<GameMode> loadGameModesFromFolder(String relativePath)
	{
		ArrayList<GameMode> games = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(Paths.get(relativePath)))
		{
			paths
			.filter(Files::isRegularFile)
			.filter(p -> p.toString().endsWith(".game"))
			.forEach(p -> {
				try {
					games.add(loadGame(p.toString()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return games;
	}

	public static GameMode loadGame(String path) throws FileNotFoundException
	{
		String str = JavaHelper.readFile(path.toString());
		return GameParser.parseGeneralGame(str);
	}

	public static GameMode getGameModeByName(String name, boolean forceReload)
	{
		if (forceReload || loadedGameModes == null) getGames(true);
		for (GameMode g : loadedGameModes)
		{
			if (g.getName().toLowerCase().strip().equals(name.toLowerCase().strip()))
				return g;
		}

		return null;
	}

	public static GameMode getGameModeByRef(int ref, boolean forceReload)
	{
		if (forceReload || loadedGameModes == null) getGames(true);
		if (ref > loadedGameModes.size()) return null;
		return loadedGameModes.get(ref - 1);
	}

	public static ArrayList<GameMode> loadSpecialGameModes()
	{
		specialGameModes.clear();
		// TODO: Add mashup
//		specialGameModes.add(new Mashup());
		return specialGameModes;
	}

	public static GameMode getGameMode(String name, boolean forceReload)
	{
		Integer ref = JavaHelper.parseInt(name);
		if (ref == null) return getGameModeByName(name, forceReload);
		return getGameModeByRef(ref, forceReload);
	}
}
