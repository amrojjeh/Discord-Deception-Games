package town.persons;

import town.DiscordGame;
import town.events.MurderTownEvent;
import town.events.TownEvent;

public abstract class Person
{
	String ID; // Used to identify each person. For Discord, it's a snowflake
	DiscordGame game; // Should be put into its own interface to seperate the game and discord
	private int refNum; // This is how players can refer to other players without mentioning them
	String roleName;
	int attackStat;
	int defenseStat;
	int priority;
	boolean alive = true;

	Person(DiscordGame game, int refNum, String id, String roleName, int attack, int defense, int priority)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
		this.roleName = roleName;
		attackStat = attack;
		defenseStat = defense;
		this.priority = priority;
	}

	public String getID()
	{
		return ID;
	}

	public int getNum()
	{
		return refNum;
	}

	public DiscordGame getGame()
	{
		return game;
	}

	public String getRealName()
	{
		return game.getJDA().getUserById(ID).getName();
	}

	public String getNickName()
	{
		if (game.getGameGuild() != null)
			return game.getGameGuild().getMemberById(ID).getEffectiveName();
		else return game.getPartyGuild().getMemberById(ID).getEffectiveName();
	}

	public String getRoleName()
	{
		return roleName;
	}

	public int getAttackStat()
	{
		return attackStat;
	}

	public int getDefenseStat()
	{
		return defenseStat;
	}

	public int getPriority()
	{
		return priority;
	}

	public void sendMessage(String msg)
	{
		// TODO: Should check if user has private channel first
		game.sendDMTo(this, msg);
	}

	public void die()
	{
		alive = false;
	}

	public void onMurder(MurderTownEvent event) {  } // By default, a person cannot kill


	public void onEvent(TownEvent event)
	{
		if (event instanceof MurderTownEvent)
			onMurder((MurderTownEvent)event);
	}

	public abstract boolean hasWon();

	public abstract void win();
}
