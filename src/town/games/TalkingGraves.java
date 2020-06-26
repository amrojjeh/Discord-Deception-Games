package town.games;

import town.DiscordGame;
import town.persons.assigner.Assigner;
import town.persons.assigner.CivilianAssigner;
import town.persons.assigner.LookoutAssigner;
import town.persons.assigner.MediumAssigner;
import town.persons.assigner.SerialKillerAssigner;

public class TalkingGraves
{
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
		//    | Total Players | Serial Killers | Lookouts | Mediums | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |      X        |      RAND      |   RAND   |   RAND  |    RAND   |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();

		assigner.addRole(new SerialKillerAssigner(game));
		assigner.addRole(new LookoutAssigner(game));
		assigner.addRole(new MediumAssigner(game));
		assigner.addRole(new CivilianAssigner(game));

		return assigner;
	}

	private static Assigner medAmount(DiscordGame game, int totalPlayers)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Mediums | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |     10        |       2        |     2    |    1    |     5     |
		//    |      9        |       2        |     2    |    1    |     4     |
		//    |      8        |       2        |     2    |    1    |     3     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();

		assigner.addRole(new SerialKillerAssigner(game, 2));
		assigner.addRole(new LookoutAssigner(game, 2));
		assigner.addRole(new MediumAssigner(game, 1));
		int civAmount = totalPlayers > 7 ? totalPlayers - 5 : 1;
		assigner.addRole(new CivilianAssigner(game, civAmount));

		return assigner;
	}

	private static Assigner lowAmount(DiscordGame game, int totalPlayers)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Mediums | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |      7        |       1        |     1    |    1    |     4     |
		//    |      6        |       1        |     1    |    1    |     3     |
		//    |      5        |       1        |     1    |    1    |     2     |
		//    |      4        |       1        |     1    |    1    |     1     |
		//    |     <4        |       1        |     1    |    1    |     1     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();
		assigner.addRole(new SerialKillerAssigner(game, 1));
		assigner.addRole(new LookoutAssigner(game, 1));
		assigner.addRole(new MediumAssigner(game, 1));
		int civAmount = totalPlayers > 3 ? totalPlayers - 3 : 1;
		assigner.addRole(new CivilianAssigner(game, civAmount));
		return assigner;
	}
}
