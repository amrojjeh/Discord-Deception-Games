package town.persons.assigner;

import java.util.ArrayList;
import java.util.Random;

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
}
