package town.persons;

import town.DiscordGame;

//the Serial Killer can kill a person each night
public class SerialKiller extends Person{

	public SerialKiller(DiscordGame game, int num, String id) {
		super(game, num, id);
	}

	//actual name of this role
	@Override
	public String getRoleName() {
		return "Serial Killer";
	}

	@Override
	public int getAttackStat() {
		return 1;
	}

	@Override
	public int getDefenseStat() {
		return 1;
	}

	@Override
	public int getPriority() {
		return 3;
	}

}
