package town.persons;

import town.events.DeathTownEvent;
import town.events.MurderTownEvent;
import town.DiscordGame;
import town.events.TownEvent;

public abstract class Person
{
	String ID; // Used to identify each person. For Discord, it's a snowflake
	DiscordGame game; // Should be put into its own interface to seperate the game and discord
	private int refNum; // This is how players can refer to other players without mentioning them
	
	Person(DiscordGame game, int refNum, String id)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
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
		return game.getGuild().getMemberById(ID).getEffectiveName();
	}
	
	public void sendMessage(String msg)
	{
		// TODO: Should check if user has private channel first
		game.sendDMTo(this, msg);
	}
	
	public void onDeath(DeathTownEvent event) { event.standard(this); } // Returns 1 to skip standard, 0 to continue normally
	public void onMurder(MurderTownEvent event) {  } // By default, a person cannot kill
	
	
	public void onEvent(TownEvent event) 
	{
		if (event instanceof DeathTownEvent) 
			onDeath((DeathTownEvent)event);
		else if (event instanceof MurderTownEvent)
			onMurder((MurderTownEvent)event);
	}
	
	public abstract String getRoleName();
	public abstract int getAttackStat();
	public abstract int getDefenseStat();
	public abstract int getPriority();
}
