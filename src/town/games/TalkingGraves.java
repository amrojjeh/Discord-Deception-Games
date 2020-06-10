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

		game.getPlayers().replaceAll(person -> assigner.generatePerson(person.getNum(), person.getID()));
	}

	private static Assigner random(DiscordGame game)
	{
		Assigner assigner = new Assigner();

		assigner.addRole(new SerialKillerAssigner(game));
		assigner.addRole(new LookoutAssigner(game));
		assigner.addRole(new MediumAssigner(game));
		assigner.addRole(new CivilianAssigner(game));

		return assigner;
	}

	private static Assigner medAmount(DiscordGame game, int totalPlayers)
	{
		Assigner assigner = new Assigner();
		int serialKillerAmount = (int)Math.round(Math.random()) + 2;

		// TODO: Actually make it be able to 6
		assigner.addRole(new SerialKillerAssigner(game, serialKillerAmount)); // 2-3 Serial killers
		assigner.addRole(new LookoutAssigner(game, 5 - serialKillerAmount)); // 2-3 Lookouts (but 5 has to be the total)
		assigner.addRole(new MediumAssigner(game, 1));
		assigner.addRole(new CivilianAssigner(game, 2));

		return assigner;
	}

	private static Assigner lowAmount(DiscordGame game, int totalPlayers)
	{
		Assigner assigner = new Assigner();
		assigner.addRole(new SerialKillerAssigner(game, 1));
		assigner.addRole(new LookoutAssigner(game, 1));
		assigner.addRole(new MediumAssigner(game, 1));
		int civAmount = totalPlayers > 3 ? totalPlayers - 3 : 1;
		assigner.addRole(new CivilianAssigner(game, civAmount));
		return assigner;
	}
}
