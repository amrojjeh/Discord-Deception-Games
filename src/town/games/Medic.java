package town.games;

import town.DiscordGame;
import town.TownRole;
import town.persons.assigner.Assigner;
import town.persons.assigner.GeneralAssigner;

public class Medic
{
	public static final TownRole[] townRoles = {TownRole.CIVILIAN, TownRole.LOOKOUT, TownRole.DOCTOR, TownRole.SERIAL_KILLER};

	public static void build(DiscordGame game)
	{
		int totalPlayers = game.getPlayers().size();
		Assigner assigner;
		if (totalPlayers < 8) assigner = lowAmount(game, totalPlayers);
		else assigner = medAmount(game, totalPlayers);

		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(person.getNum(), person.getID()));
	}

	public static void buildRand(DiscordGame game)
	{
		Assigner assigner = random(game);
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(person.getNum(), person.getID()));
	}

	private static Assigner random(DiscordGame game)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Doctors | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |      X        |      RAND      |   RAND   |   RAND  |    RAND   |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();

		for (TownRole role : townRoles)
			assigner.addRole(new GeneralAssigner(game, role));

		return assigner;
	}

	private static Assigner medAmount(DiscordGame game, int totalPlayers)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Doctors | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |     10        |       2        |     2    |    1    |     5     |
		//    |      9        |       2        |     2    |    1    |     4     |
		//    |      8        |       2        |     2    |    1    |     3     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();

		assigner.addRole(new GeneralAssigner(game, 2, TownRole.SERIAL_KILLER));
		assigner.addRole(new GeneralAssigner(game, 2, TownRole.LOOKOUT));
		assigner.addRole(new GeneralAssigner(game, 1, TownRole.DOCTOR));
		int civAmount = totalPlayers > 7 ? totalPlayers - 5 : 1;
		assigner.addRole(new GeneralAssigner(game, civAmount, TownRole.CIVILIAN));

		return assigner;
	}

	private static Assigner lowAmount(DiscordGame game, int totalPlayers)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Doctors | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |      7        |       1        |     1    |    1    |     4     |
		//    |      6        |       1        |     1    |    1    |     3     |
		//    |      5        |       1        |     1    |    1    |     2     |
		//    |      4        |       1        |     1    |    1    |     1     |
		//    |     <4        |       1        |     1    |    1    |     1     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();
		assigner.addRole(new GeneralAssigner(game, 1, TownRole.SERIAL_KILLER));
		assigner.addRole(new GeneralAssigner(game, 1, TownRole.LOOKOUT));
		assigner.addRole(new GeneralAssigner(game, 1, TownRole.DOCTOR));
		int civAmount = totalPlayers > 3 ? totalPlayers - 3 : 1;
		assigner.addRole(new GeneralAssigner(game, civAmount, TownRole.CIVILIAN));
		return assigner;
	}
}
