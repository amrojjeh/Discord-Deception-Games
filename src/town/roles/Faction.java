package town.roles;

import java.util.function.Consumer;
import java.util.function.Predicate;

import town.discordgame.DiscordGame;
import town.mafia.helper.FactionHelper;

public enum Faction
{
	TOWN("Town", FactionHelper::isTownLeft, FactionHelper::townWin),
	SERIAL_KILLER("Serial Killer", FactionHelper::isSKLeft, FactionHelper::skWin);

	private final String name;
	private final Predicate<DiscordGame> canWin;
	private final Consumer<DiscordGame> win;

	Faction(String name, Predicate<DiscordGame> canWin, Consumer<DiscordGame> win)
	{
		this.name = name;
		this.canWin = canWin;
		this.win = win;
	}

	public String getName()
	{
		return name;
	}

	public boolean canWin(DiscordGame game)
	{
		return canWin.test(game);
	}

	public void win(DiscordGame game)
	{
		win.accept(game);
	}
}
