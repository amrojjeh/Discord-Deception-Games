package town.persons.assigner;

import town.DiscordGame;
import town.persons.Doctor;
import town.persons.Person;

public class DoctorAssigner extends RoleAssigner
{
	public DoctorAssigner(DiscordGame g)
	{
		super(g);
	}

	public DoctorAssigner(DiscordGame g, int maxAmount)
	{
		super(g, maxAmount);
	}

	@Override
	public Person getPerson(int ref, long ID)
	{
		amount++;
		return new Doctor(game, ref, ID);
	}
}
