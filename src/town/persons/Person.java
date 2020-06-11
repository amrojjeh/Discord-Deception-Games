package town.persons;

import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import town.DiscordGame;
import town.TownRole;
import town.events.TownEvent;
import town.phases.Morning;
import town.phases.Night;
import town.phases.Phase;

public abstract class Person
{
	private final long ID; // Used to identify each person. For Discord, it's a snowflake
	private final DiscordGame game; // Should be put into its own interface to seperate the game and discord
	private final String realName;

	private long privateChannelID = 0;
	private int refNum; // This is how players can refer to other players without mentioning them
	private int tempDefense = -1;
	private int tempAttack = -1;

	protected TownRole type;
	protected boolean disconnected = false;
	protected boolean alive = true;
	protected String causeOfDeath = String.format("<@%d> is still alive.", getID());
	protected TownEvent event;

	// In case the role hasn't been yet figured out
	Person(DiscordGame game, int refNum, long id)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
		realName = game.getUser(this).getName();
	}

	Person(DiscordGame game, int refNum, long id, TownRole type)
	{
		this.game = game;
		ID = id;
		this.refNum = refNum;
		this.type = type;
		realName = game.getUser(this).getName();
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
		return realName;
	}

	public TownRole getType()
	{
		return type;
	}

	public void setType(TownRole role)
	{
		type = role;
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

	public void sendMessage(MessageEmbed msg)
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

	public void die(String reason)
	{
		if (getGame().getCurrentPhase() instanceof Night)
			die(reason, true);
		else
			die(reason, false);
	}

	public void die(String reason, boolean saveForMorning)
	{
		if (!alive) return;
		if (!reason.isEmpty()) causeOfDeath = reason;
		getGame().personDied(this, saveForMorning);
		alive = false;
	}

	public void onEvent(TownEvent event)
	{
		event.standard(this);
	}

	public TownEvent getEvent()
	{
		return event;
	}

	public void setChannelID(Long id)
	{
		privateChannelID = id;
	}

	public String getCauseOfDeath()
	{
		return causeOfDeath;
	}

	public int getDefense()
	{
		if (tempDefense < 0) return getType().getDefense();
		return tempDefense;
	}

	public void setDefense(int val)
	{
		tempDefense = val;
	}

	public int getAttack()
	{
		if (tempAttack < 0) return getType().getAttack();
		return tempAttack;
	}

	public void setAttack(int val)
	{
		tempAttack = val;
	}

	public boolean isDisconnected()
	{
		return disconnected;
	}

	public void disconnect()
	{
		disconnected = true;
	}

	public String cancel()
	{
		if (event == null) return "There's no action to cancel";
		getGame().removeEvent(event);
		return "Action canceled";
	}

	public void onPhaseChange(Phase phase)
	{
		if (phase instanceof Morning)
		{
			event = null;
			tempDefense = -1;
		}
	}

	public List<Person> getPossibleTargets()
	{
		return null;
	}

	public String ability(List<Person> list)
	{
		return type.getName() + " has no ability";
	}

	public abstract boolean hasWon();

	public abstract boolean canWin();

	public abstract void win();

	public abstract String getHelp();
}
