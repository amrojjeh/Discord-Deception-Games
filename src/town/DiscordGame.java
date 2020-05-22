package town;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import town.events.TownEvent;
import town.events.MurderTownEvent;
import town.persons.Person;
import town.persons.RoleAssigner;

public class DiscordGame
{
	JDA jda;
	String guildID;
	String gameGuildID;
	ArrayList<Person> persons; // TODO: Sort based on priority also (SortedSet?)
	LinkedList<TownEvent> events; // TODO: PriorityQueue<E>
	PhaseManager phaseManager;
	boolean started;
	
	// Important channels
	HashMap<String, String> textChannels;
	HashMap<String, String> voiceChannels;
	
	String partyLeaderID;
	String prefix;
	
	public DiscordGame(JDA jda, String guildId, String partyLeaderId) 
	{
		this.jda = jda;
		guildID = guildId;
		partyLeaderID = partyLeaderId;
		prefix = "tos.";
		
		phaseManager = new PhaseManager();
		persons = new ArrayList<>();
		events = new LinkedList<>();
		textChannels = new HashMap<>();
		voiceChannels = new HashMap<>();
		
		started = false;
	}
	
	public void processMessage(Message message)
	{
		// NOTE: Who is the party leader?
		// TODO: When game starts, allow ! as a prefix also
		if (message.getContentRaw().contentEquals(prefix + "startGame"))
		{
			if (started)
				message.getChannel().sendMessage("Game is already running!").queue();
			else if (persons.isEmpty())
				message.getChannel().sendMessage("Not enough players to start a server!").queue();
			else if (!message.getMember().getId().contentEquals(partyLeaderID))
				message.getChannel().sendMessage(String.format("Only party leader (<@%s>) can start the game!", partyLeaderID));
			else
			{
				message.getChannel().sendMessage("Game has started! Creating server...").queue();
				startGame();
			}
		}

		// TODO: Block people if they occupy a certain role
		else if (message.getContentRaw().contentEquals(prefix + "party"))
			displayParty(message.getChannel());
		else if (message.getContentRaw().contentEquals(prefix + "join"))
			joinGame(message.getMember().getId(), message.getChannel());
		
		// TODO: Might want to handle commands better (Seperate function? Classes? ArrayLists?)
		else if (started && message.getContentRaw().startsWith(prefix + "kill"))
		{
			// TODO: Check if there is more than one mention
			Person deadPerson = getPerson(message.getMentionedMembers().get(0));
			Person murderer = getPerson(message.getMember());
			if (deadPerson != null && murderer != null)
			{
				events.add(new MurderTownEvent(this, murderer, deadPerson));
			}
			// TODO: Send message when person isn't in the lobby
			else System.out.println("Didn't get person");
		}
		
		dispatchEvents();
	}

	public void displayParty(MessageChannel channelUsed)
	{
		String description = "";
		String format = "%d. %s (%s)\n";
		for (Person p : persons)
			description += String.format(format, p.getNum(), p.getRealName(), p.getNickName());
		MessageEmbed embed = new EmbedBuilder().setColor(Color.YELLOW).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
	}

    public void createNewChannels(GuildAction g)
    {
    	// this channel used for general game updates
    	textChannels.put("system", "");
    	// players discussing during the day
    	textChannels.put("daytime_discussion", "");
    	textChannels.put("the_hideout", "");
    	textChannels.put("the_underground", "");
    	//the jailor's private text channel, where he can talk to the bot
        textChannels.put("jailor", "");
      //the "jail" where the bot transfers the jailor's text anonymously, and the jailed player can respond
        textChannels.put("jail", "");
    	
    	voiceChannels.put("Daytime", "");
    	// mafia private chat at night
    	voiceChannels.put("Mafia", "");
    	// vampire private chat at night
        voiceChannels.put("Vampires", "");
    	
        
        //for dead players
        voiceChannels.put("The Dead", "");
        textChannels.put("the_afterlife", "");
    	
        textChannels.forEach((name, id) -> g.newChannel(ChannelType.TEXT, name));
        voiceChannels.forEach((name, id) -> g.newChannel(ChannelType.VOICE, name));
    }
	
	public void addEvent(TownEvent event)
	{
		events.add(event);
	}
	
	public void startPhase() 
	{
		phaseManager.start();
	}
	
	public void startGame()
	{	
		startPhase();
		started = true;
		
		// TODO: Add an icon to the server
		
		GuildAction ga = getJDA().createGuild("Town of Salem");
		createNewChannels(ga);
		ga.newRole().setName(guildID);
		ga.queue();
	}

	public void assignChannel(GuildChannel channel) 
	{
		if (channel.getType().equals(ChannelType.TEXT))
			textChannels.replace(channel.getName(), channel.getId());
		else
			voiceChannels.replace(channel.getName(), channel.getId());
	}
	
	public void getNewGuild(Guild guild)
	{
		guild.getChannels().get(0).createInvite().queue((invite) -> persons.forEach((person) -> person.sendMessage(invite.getUrl())));
		gameGuildID = guild.getId();
		guild.getChannels(true).forEach((channel) -> assignChannel(channel));
		
		getJDA().getTextChannelById(textChannels.get("system")).sendMessage("This is a test").queue();
		
		// TODO: Remove timer
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {public void run() {guild.delete().queue();}}, 5 * 1000);    
		
        persons.forEach((person) -> person.sendMessage("Your role is " + person.getRoleName()));
	}
	
	public void joinGame(String id, MessageChannel channelUsed)
	{
		if (started)
		{
			String message = String.format("Can't join game until session is over <@%s>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}
		
		// Civilian is a temporary role. Once the game starts, they should get their actual roles
		persons.add(RoleAssigner.assignRole(this, persons.size() + 1, id));
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
