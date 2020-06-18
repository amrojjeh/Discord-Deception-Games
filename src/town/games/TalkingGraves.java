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
		if (totalPlayers <= 5) assigner = lowAmount(game, totalPlayers);
		else if (totalPlayers <= 8) assigner = medAmount(game, totalPlayers);
		else assigner = random(game);

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
		//    |      8        |      2-3       |    2-3   |    1    |     2     |
		//    |      7        |      2-3       |    1-2   |    1    |     2     |
		//    |      6        |      1-2       |    1-2   |    1    |     2     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();
		int lookoutAndSK = totalPlayers - 3;
		int skAmount = (int)Math.round(Math.random()) + lookoutAndSK / 2;
		int lookoutAmount = lookoutAndSK - skAmount;

		assigner.addRole(new SerialKillerAssigner(game, skAmount));
		assigner.addRole(new LookoutAssigner(game, lookoutAmount));
		assigner.addRole(new MediumAssigner(game, 1));
		assigner.addRole(new CivilianAssigner(game, 2));

		return assigner;
	}

	private static Assigner lowAmount(DiscordGame game, int totalPlayers)
	{
		//    +---------------+----------------+----------+---------+-----------+
		//    | Total Players | Serial Killers | Lookouts | Mediums | Civilians |
		//    +---------------+----------------+----------+---------+-----------+
		//    |      5        |       1        |     1    |    1    |     2     |
		//    |      4        |       1        |     1    |    1    |     1     |
		//    |     <4        |       1        |     1    |    1    |     1     |
		//    +---------------+----------------+----------+---------+-----------+

		Assigner assigner = new Assigner();
		assigner.addRole(new SerialKillerAssigner(game, 1));
		assigner.addRole(new LookoutAssigner(game, 1));
		assigner.addRole(new MediumAssigner(game, 1));
		// TODO: Add back civilian
//		int civAmount = totalPlayers > 3 ? totalPlayers - 3 : 1;
//		assigner.addRole(new CivilianAssigner(game, civAmount));
		return assigner;
	}
}
