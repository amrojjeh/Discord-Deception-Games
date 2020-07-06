package town.persons.assigner;

import java.util.ArrayList;
import java.util.Random;

import town.DiscordGame;
import town.persons.Person;

public class Assigner
{
	ArrayList<RoleAssigner> roles = new ArrayList<>();

	public void addRole(RoleAssigner assigner)
	{
		roles.add(assigner);
	}

	public ArrayList<RoleAssigner> getRoles()
	{
		return roles;
	}

	public Person generatePerson(DiscordGame game, int baseNumberOfPlayers, int refNum, long id)
	{
		Random random = new Random();
		int randNum;
		do
		{
			randNum = random.nextInt(roles.size());
		} while (!roles.get(randNum).check(baseNumberOfPlayers, refNum));
		System.out.println("Person generated");
		return roles.get(randNum).getPerson(game, refNum, id);
	}
}
