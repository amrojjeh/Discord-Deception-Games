package io.github.dinglydo.town.mafia.roles;

import java.util.function.Function;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.AttributeValue;
import io.github.dinglydo.town.persons.Attributes;
import io.github.dinglydo.town.roles.Role;

/**
 * A list of all the TVM roles supported. Names are all lowercase and contain no spaces.
 * @author Amr Ojjeh
 *
 */
public enum TVMRole
{
	CIVILIAN("Civilian", AttributeValue.NONE, AttributeValue.NONE, 0, Civilian::new),
	MEDIUM("Medium", AttributeValue.NONE, AttributeValue.NONE, 0, Medium::new),
	LOOKOUT("Lookout", AttributeValue.NONE, AttributeValue.NONE, 0, Lookout::new),
	DOCTOR("Doctor", AttributeValue.NONE, AttributeValue.NONE, 3, Doctor::new),
	SERIAL_KILLER("Serial Killer", AttributeValue.BASIC, AttributeValue.BASIC, 5, SerialKiller::new);

	private final Function<DiscordGame, Role> roleGetter;
	private final String name;
	private final Attributes attrib;
	private final int priority;

	private TVMRole(String name, AttributeValue defense, AttributeValue attack, int priority, Function<DiscordGame, Role> roleGetter)
	{
		this.name = name;
		this.roleGetter = roleGetter;
		this.priority = priority;
		this.attrib = new Attributes(defense, attack);
	}

	public Role getRole(DiscordGame game)
	{
		return roleGetter.apply(game);
	}

	public String getName()
	{
		return name;
	}

	public int getPriority()
	{
		return priority;
	}

	public Attributes getAttributes()
	{
		return attrib;
	}
}
