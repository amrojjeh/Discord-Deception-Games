package town.games;

import town.DiscordGame;
import town.persons.assigner.Assigner;
import town.persons.assigner.CivilianAssigner;
import town.persons.assigner.DoctorAssigner;
import town.persons.assigner.LookoutAssigner;
import town.persons.assigner.MediumAssigner;
import town.persons.assigner.SerialKillerAssigner;

public class Mashup
{
	public static void build(DiscordGame game)
	{
		Assigner assigner = getAssigner(game);
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(person.getNum(), person.getID()));
	}

	public static Assigner getAssigner(DiscordGame game)
	{
		// Add default roles
		Assigner assigner = new Assigner();
		assigner.addRole(new CivilianAssigner(game));
		assigner.addRole(new SerialKillerAssigner(game));
		assigner.addRole(new LookoutAssigner(game));
		assigner.addRole(new MediumAssigner(game));
		assigner.addRole(new DoctorAssigner(game));
		return assigner;
	}
}
