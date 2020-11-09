package io.github.dinglydo.town.mafia.roles;

import java.util.function.Function;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.roles.Role;

/**
 * A list of all the TVM roles supported. Names are all lowercase and contain no spaces.
 * @author Amr Ojjeh
 *
 */
public enum TVMRole
{
	CIVILIAN("Civilian", Civilian::new),
	DOCTOR("Doctor", Doctor::new),
	LOOKOUT("Lookout", Lookout::new),
	MEDIUM("Medium", Medium::new),
	SERIAL_KILLER("Serial Killer", SerialKiller::new);

	private final Function<DiscordGame, Role> roleGetter;
	private final String name;

	private TVMRole(String name, Function<DiscordGame, Role> roleGetter)
	{
		this.name = name;
		this.roleGetter = roleGetter;
	}

	public Role getRole(DiscordGame game)
	{
		return roleGetter.apply(game);
	}

	public String getName()
	{
		return name;
	}
}
