package town.persons;

import town.DiscordGame;

//Civilian is NOT A REAL ROLE. This is a temporary useless town role to simulate games
public class Civilian extends Person {

	public Civilian(DiscordGame game, int num, String id) {
		super(game, num, id);
	}

	@Override
	public String getRoleName() {
		return "Civilian";
	}

	@Override
	public int getAttackStat() {
		return 0;
	}

	@Override
	public int getDefenseStat() {
		return 0;
	}

	@Override
	public int getPriority() {
		return 6;
	}
}
