package io.github.dinglydo.town.events;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.roles.Role;

public class MurderTownEvent implements TownEvent
{
	private DiscordGamePerson user;
	private Role role;
	private DiscordGamePerson victim;
	private DiscordGame game;

	public MurderTownEvent(DiscordGame game, DiscordGamePerson m, DiscordGamePerson v)
	{
		this.game = game;
		this.user = m;
		this.victim = v;
	}

	public Role getRole()
	{
		return role;
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
			victim.die(String.format("<@%d> was murdered by a serial killer.", getVictim().getID()));
	}
}
