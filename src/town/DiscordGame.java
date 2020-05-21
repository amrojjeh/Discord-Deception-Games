package town;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import town.events.TownEvent;
import town.events.MurderTownEvent;
import town.persons.DummyPerson;
import town.persons.Person;

public class DiscordGame
{
	private JDA jda;
	private String guildID;
	private String gameGuildID;
	private ArrayList<Person> persons; // TODO: Sort based on priority also (SortedSet?)
	private LinkedList<TownEvent> events; // TODO: PriorityQueue<E>
	private PhaseManager phaseManager;
	private boolean started;
	
	// Important channels
	// TODO: Possible make it possible to play with either DMs or text channels?
	// We can make it possible depending on whether players choose to make their DMs private
	private String dayTextChannelID;
	private String dayVoiceChannelID;
	private String logTextChannelID;
	private String deadTextChannelID;
	private String morgueTextChannelID;
	
	public DiscordGame(JDA jda, String ID) 
	{
		this.jda = jda;
		guildID = ID;
		
		phaseManager = new PhaseManager();
		persons = new ArrayList<>();
		events = new LinkedList<>();
		started = false;
	}
	
	public void processMessage(Message message)
	{		
		if (message.getContentRaw().contentEquals("!start"))
			startGame(message.getChannel());

		// TODO: Block people if they occupy a certain role
		// TODO: Display party with !party
		else if (message.getContentRaw().contentEquals("!join"))
			joinGame(message.getMember().getId(), message.getChannel());
		
		else if (started && message.getContentRaw().startsWith("!kill"))
		{
			Person deadPerson = getPerson(message.getMentionedMembers().get(0));
			Person murderer = getPerson(message.getMember());
			if (deadPerson != null && murderer != null)
			{
				events.add(new MurderTownEvent(this, murderer, deadPerson));
			}
			
			else System.out.println("Didn't get person");
		}
		
		dispatchEvents();
		
	}
	
	public void addEvent(TownEvent event)
	{
		events.add(event);
	}
	
	public void startPhase() 
	{
		phaseManager.start();
	}
	
	public void startGame(MessageChannel channelUsed)
	{
		if (started) 
		{
			channelUsed.sendMessage("Game is already running!").queue();
			return;
		}
		
		else if (persons.isEmpty())
		{
			channelUsed.sendMessage("Not enough players to start a server!").queue();
			return;
		}
		
		startPhase();
		started = true;
		channelUsed.sendMessage("Game has started! Creating server...").queue();
		
		// TODO: Create channels
		GuildAction ga = getJDA().createGuild("Town of Salem ");
		ga.newChannel(ChannelType.TEXT, "default");
		ga.newRole().setName(guildID);
		ga.queue();
	}
	
	public void getNewGuild(Guild guild)
	{
		guild.getChannels().get(0).createInvite().queue((invite) -> persons.forEach((person) -> person.sendMessage(invite.getUrl())));
		// TODO: Remove timer
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {public void run() {guild.delete().queue();}}, 15000);
	}
	
	public void joinGame(String id, MessageChannel channelUsed)
	{
		persons.add(new DummyPerson(this, id));
		String message = String.format("<@%s> joined the lobby", id);
		channelUsed.sendMessage(message).queue();
	}
	
	public Person getPerson(Member member)
	{
		for (Person person : persons)
			if (person.getID().equals(member.getId()))
				return person;
		return null;
	}
	
	public void dispatchEvents()
	{
		if (events.size() == 0) return;
		TownEvent event = events.remove();
		for (Person person : persons) 
			person.onEvent(event);
		
		dispatchEvents();
	}
	
	public JDA getJDA() 
	{
		return jda;
	}
	
	public String getID() 
	{
		return guildID;
	}
	
	public Guild getGuild()
	{
		return getJDA().getGuildById(getID());
	}

	public void sendDMTo(Person person, String msg)
	{
		jda.getUserById(person.getID()).openPrivateChannel().queue((channel) -> channel.sendMessage(msg).queue());
	}
	
}
