import java.util.ArrayList;

public class Tester
{
	public static void main(String[] args)
	{
		// Initialize Faction
		ArrayList<Player> players = new ArrayList<>();
		for (int x = 0; x < 50; ++x)
			players.add(new Player("" + x, new Doctor()));
		Faction town = new Faction("Town", players);
		town.onMemberDeath((player) -> System.out.println(player.getName() + " just died"));

		// Kill a random player
		int random = (int)Math.floor(Math.random() * 50);
		players.get(random).dies();
	}
}
