package io.github.dinglydo.town.persons.assigner;

import java.util.ArrayList;
import java.util.Random;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.party.Party;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.persons.LobbyPerson;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleBuilder;

public class Assigner
{
	private ArrayList<RoleBuilder> roles = new ArrayList<>();
	private final int minimumRequiredPlayers;

	public Assigner(int minimumPlayers)
	{
		this.minimumRequiredPlayers = minimumPlayers;
	}

	public void addRole(RoleBuilder assigner)
	{
		roles.add(assigner);
	}

	public ArrayList<RoleBuilder> getRoles()
	{
		return roles;
	}

	public int getMinimumPlayers()
	{
		return minimumRequiredPlayers;
	}

	public boolean hasDefault()
	{
		for (RoleBuilder role : getRoles())
		{
			if (role.isDefault())
				return true;
		}
		return false;
	}

	public ArrayList<DiscordGamePerson> assignRoles(Party party, DiscordGame game)
	{
		ArrayList<DiscordGamePerson> people = game.getPlayersCache();
		people.clear();

		for (LobbyPerson person : party.getPlayersCache())
		{
			Role role = getRole(game, party.getPlayerSize());
			DiscordGamePerson player = new DiscordGamePerson(game, person.getID(), role);
			people.add(player);
		}
		return people;
	}

	private Role getRole(DiscordGame game, int total)
	{
		Random random = new Random();
		int randNum;
		do
		{
			randNum = random.nextInt(roles.size());
		} while (!roles.get(randNum).check(game, minimumRequiredPlayers, total));

		return roles.get(randNum).getRole(game);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getMinimumPlayers() + " ");
		for (int x = 0; x < roles.size(); ++x)
		{
			RoleBuilder rBuilder = roles.get(x);
			builder.append(rBuilder.getTVMRole().getName() + " " + rBuilder.getMinimum() + (rBuilder.isDefault() ? "+" : ""));
			if (x != roles.size() - 1)
				builder.append(", ");
		}
		return builder.toString();
	}
}
