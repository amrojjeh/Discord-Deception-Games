package town.persons.assigner;

import java.util.ArrayList;
import java.util.Random;

import town.DiscordGame;
import town.GameParty;
import town.persons.DiscordGamePerson;
import town.persons.LobbyPerson;
import town.roles.Role;

public class Assigner
{
	private ArrayList<RoleAssigner> roles = new ArrayList<>();
	private final int minimumRequiredPlayers;

	public Assigner(int minimumPlayers)
	{
		this.minimumRequiredPlayers = minimumPlayers;
	}

	public void addRole(RoleAssigner assigner)
	{
		roles.add(assigner);
	}

	public ArrayList<RoleAssigner> getRoles()
	{
		return roles;
	}

	public ArrayList<DiscordGamePerson> assignRoles(GameParty party, DiscordGame game)
	{
		ArrayList<DiscordGamePerson> people = new ArrayList<>();
		for (LobbyPerson person : party.getPlayersCache())
		{
			Role role = getRole(game, minimumRequiredPlayers, party.getPlayerSize(), person);
			DiscordGamePerson player = new DiscordGamePerson(game, person.getID(), role);
			people.add(player);
		}
		return people;
	}

	private Role getRole(DiscordGame game, int min, int total, LobbyPerson person)
	{
		Random random = new Random();
		int randNum;
		do
		{
			randNum = random.nextInt(roles.size());
		} while (!roles.get(randNum).check(min, total));

		return roles.get(randNum).useRole();
	}
}
