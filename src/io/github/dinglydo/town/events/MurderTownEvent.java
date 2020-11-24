package io.github.dinglydo.town.events;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.Role;

public class MurderTownEvent implements TownEvent
{
	private final DiscordGamePerson user;
	private final Role role;
	private final DiscordGamePerson victim;
	private final DiscordGame game;

	public MurderTownEvent(DiscordGame game, DiscordGamePerson m, DiscordGamePerson v)
	{
		this.game = game;
		this.user = m;
		this.victim = v;
		this.role = user.getRole();
	}

	public Role getRole()
	{
		return role;
	}

	@Override
	public DiscordGamePerson getUser()
	{
		return getMurderer();
	}

	public DiscordGamePerson getMurderer()
	{
		return user;
	}

	public DiscordGamePerson getVictim()
	{
		return victim;
	}

	@Override
	public DiscordGame getGame()
	{
		return game;
	}

	@Override
	public void standard(DiscordGamePerson person)
	{
		if (person == getMurderer())
			attackVictim();
	}

	@Override
	public DiscordGamePerson getTarget()
	{
		return victim;
	}

	@Override
	public int getPriority()
	{
		return getRole().getPriority();
	}

	public void attackVictim()
	{
		getMurderer().sendMessage("You attacked <@" + victim.getID() + ">");
		victim.sendMessage("You were attacked");
		if(getMurderer().getAttributes().attack.getVal() > victim.getAttributes().defense.getVal())
		{
			System.out.println(getMurderer().getAttributes().attack.name() + " > " + victim.getAttributes().defense.name());
			victim.die(String.format("<@%d> was murdered by a serial killer.", getVictim().getID()));
		}
	}
}
