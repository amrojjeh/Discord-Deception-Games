package town.games;

import town.DiscordGame;
import town.TownRole;
import town.persons.assigner.Assigner;
import town.persons.assigner.GeneralAssigner;

public class Mashup
{
	public static final TownRole[] townRoles = TownRole.values();

	public static void build(DiscordGame game)
	{
		Assigner assigner = getAssigner(game);
		game.getPlayersCache().replaceAll(person -> assigner.generatePerson(person.getNum(), person.getID()));
	}

	public static Assigner getAssigner(DiscordGame game)
	{
		Assigner assigner = new Assigner();
		for (TownRole role : townRoles)
			assigner.addRole(new GeneralAssigner(game, role));
		return assigner;
	}
}
