package town;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import town.commands.PartyCommands;
import town.games.parser.Rule;
import town.persons.LobbyPerson;
import town.persons.Person;

public class GameParty extends ListenerAdapter
{
	private final JDA jda;
	private final PartyCommands commands;
	private final long channelId;

	DiscordGameConfig config;
	private String prefix;
	private Person gameLeader;
	private ArrayList<LobbyPerson> persons = new ArrayList<>();
	private boolean registeredListener;

	private GameParty(JDA jda, long channelId, long partyLeaderId)
	{
		this.jda = jda;
		this.channelId = channelId;
		// TODO: Join the party leader and make him leader since no one's in the list
		config = new DiscordGameConfig();
		config.setGameMode("1");
		prefix = "pg.";
		registeredListener = false;
		commands = new PartyCommands();
	}

	public JDA getJDA()
	{
		return jda;
	}

	public static GameParty createParty(JDA jda, TextChannel tc, Member member)
	{
		GameParty gp = new GameParty(jda, tc.getIdLong(), member.getIdLong());
		gp.registerAsListener(true);
		return gp;
	}

	public int getPlayerSize()
	{
		return getPlayersCache().size();
	}

	public ArrayList<LobbyPerson> getPlayersCache()
	{
		return persons;
	}

	public ArrayList<LobbyPerson> getPlayers()
	{
		return new ArrayList<>(persons);
	}

	public Person getGameLeader()
	{
		return gameLeader;
	}

	public void setGameLeader(Person p)
	{
		gameLeader = p;
	}

	@Nullable
	public LobbyPerson getPerson(Member member)
	{
		return getPerson(member.getIdLong());
	}

	@Nullable
	public LobbyPerson getPerson(long id)
	{
		for (LobbyPerson person : getPlayersCache())
			if (person.getID() == id)
				return person;
		return null;
	}

	public boolean hasPersonJoined(Person person)
	{
		return hasPersonJoined(person.getID());
	}

	public boolean hasPersonJoined(Member member)
	{
		return hasPersonJoined(member.getIdLong());
	}

	public boolean hasPersonJoined(long id)
	{
		return getPerson(id) != null;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public DiscordGameConfig getConfig()
	{
		return config;
	}

	public long getChannelId()
	{
		return channelId;
	}

	public void registerAsListener(boolean register)
	{
		if (register && !registeredListener)
		{
			jda.addEventListener(this);
			registeredListener = true;
		}
		else if (!register && registeredListener)
		{
			jda.removeEventListener(this);
			registeredListener = false;
		}
	}

	public boolean isRegisteredListener()
	{
		return registeredListener;
	}

	public User getUser(Person person)
	{
		return jda.getUserById(person.getID());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent message)
	{
		if (message.getChannel().getIdLong() == channelId)
			processMessage(message.getMessage());
	}

	public void processMessage(Message message)
	{
		processMessage(getPrefix(), message);
	}

	public void processMessage(String prefix, Message message)
	{
		commands.executeCommand(this, prefix, message);
	}

	public boolean isPartyFull()
	{
		int size = getPlayerSize();
		Rule rule = getConfig().getGameMode().getClosestRule(size);
		return !rule.hasDefault() && !getConfig().isRandom() && rule.totalPlayers < size + 1;
	}

	public boolean isPartyEmpty()
	{
		return getPlayerSize() == 0;
	}

	public void joinGame(Member member) throws PartyIsFullException
	{
		joinGame(new LobbyPerson(this, member.getIdLong()));
	}

	public void joinGame(LobbyPerson person) throws PartyIsFullException
	{
		// Has person already joined?
		if (hasPersonJoined(person)) return;

		// Throw an exception if the lobby is already full
		if (isPartyFull()) throw new PartyIsFullException(this, person);

		// Add the person if everything succeeds
		getPlayersCache().add(person);
	}

	public void leaveGame(Member member) throws PartyIsEmptyException
	{
		if (isPartyEmpty()) throw new PartyIsEmptyException(this);
		getPlayersCache().remove(getPerson(member));
	}

	public void leaveGame(Person person) throws PartyIsEmptyException
	{
		if (isPartyEmpty()) throw new PartyIsEmptyException(this);
		getPlayersCache().remove(person);
	}
}
