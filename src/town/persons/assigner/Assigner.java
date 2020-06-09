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

	public Person generatePerson(int refNum, long id)
	{
		Random random = new Random();
		int randNum;
		do
		{
			randNum = random.nextInt(roles.size());
		} while (!roles.get(randNum).check());
		return roles.get(randNum).getPerson(refNum, id);
	}

	public static Assigner buildDefault(DiscordGame game)
	{
		// Add default roles
		Assigner assigner = new Assigner();
		//		assigner.addRole(new CivilianAssigner(game));
		assigner.addRole(new SerialKillerAssigner(game));
		assigner.addRole(new LookoutAssigner(game));
		assigner.addRole(new MediumAssigner(game));
		return assigner;
	}
}
