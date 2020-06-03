package town.persons;

import java.util.ArrayList;

import town.DiscordGame;
import town.events.MurderTownEvent;
import town.events.TownEvent;

public abstract class Person
{
	Long ID; // Used to identify each person. For Discord, it's a snowflake
	DiscordGame game; // Should be put into its own interface to seperate the game and discord
	private int refNum; // This is how players can refer to other players without mentioning them
	String roleName;
	int attackStat;
	int defenseStat;
	int priority;
	boolean alive = true;
	private Long privateChannelID;

	Person(DiscordGame game, int refNum, Long id, String roleName, int attack, int defense, int priority)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
		this.roleName = roleName;
		attackStat = attack;
		defenseStat = defense;
		this.priority = priority;
	}

	public Long getID()
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
		return game.getUser(this).getName();
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

	public boolean isAlive()
	{
		return alive;
	}

	public void sendMessage(String msg)
	{
		if (privateChannelID != null)
			game.sendMessageToTextChannel(privateChannelID, msg).queue();
		else
			System.out.println("Could not send to private channel");
	}

	public void assignPrivateChannel(Long channelID)
	{
		privateChannelID = channelID;
	}

	public Long getChannelID()
	{
		return privateChannelID;
	}

	public void die()
	{
		alive = false;
	}

	public void onMurder(MurderTownEvent event) { event.standard(this); }


	public void onEvent(TownEvent event)
	{
		if (event instanceof MurderTownEvent)
			onMurder((MurderTownEvent)event);
	}

	public void setChannelID(Long id)
	{
		privateChannelID = id;
	}

	public abstract String ability(ArrayList<Person> references);

	public abstract String cancel();

	public abstract boolean hasWon();

	public abstract void win();
}
