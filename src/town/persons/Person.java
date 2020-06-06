package town.persons;

import java.util.List;

import town.DiscordGame;
import town.TownRole;
import town.events.MurderTownEvent;
import town.events.TownEvent;

public abstract class Person
{
	private final long ID; // Used to identify each person. For Discord, it's a snowflake
	private final DiscordGame game; // Should be put into its own interface to seperate the game and discord
	private final TownRole type;

	private long privateChannelID = 0;
	private int refNum; // This is how players can refer to other players without mentioning them

	protected boolean alive = true;

	Person(DiscordGame game, int refNum, long id, TownRole type)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
		this.type = type;
	}

	public long getID()
	{
		return ID;
	}

	public int getNum()
	{
		return refNum;
	}

	public void setNum(int val)
	{
		refNum = val;
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

	public TownRole getType()
	{
		return type;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void sendMessage(String msg)
	{
		if (privateChannelID != 0)
			game.sendMessageToTextChannel(privateChannelID, msg);
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

	public abstract String ability(List<Person> list);

	public abstract String cancel();

	public abstract boolean hasWon();

	public abstract boolean canWin();

	public abstract void win();

	public abstract String getHelp();
}
