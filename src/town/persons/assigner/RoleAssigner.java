package town.persons.assigner;

import java.util.Random;

import town.DiscordGame;
import town.persons.Civilian;
import town.persons.Person;
import town.persons.SerialKiller;

public class RoleAssigner
{
	public static Person assignRole(DiscordGame g, int n, Long id)
	{
		Random random = new Random();
		int role = random.nextInt(2);
		switch(role)
		{
		case 0:
			if (Civilian.getMaxAmount() == 0 || Civilian.getAmount() < Civilian.getMaxAmount()) return new Civilian(g, n, id);
			else return assignRole(g, n, id);
		case 1:
			if (SerialKiller.getMaxAmount() == 0 || SerialKiller.getAmount() < SerialKiller.getMaxAmount()) return new SerialKiller(g, n, id);
			else return assignRole(g, n, id);
		default:
			if (Civilian.getMaxAmount() == 0 || Civilian.getAmount() < Civilian.getMaxAmount()) return new Civilian(g, n, id);
			else return assignRole(g, n, id);
		}
	}
}
