package io.github.dinglydo.town.discordgame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.dinglydo.town.DiscordGameConfig;
import io.github.dinglydo.town.MainListener;
import io.github.dinglydo.town.events.TownEvent;
import io.github.dinglydo.town.mafia.phases.End;
import io.github.dinglydo.town.mafia.roles.TVMRole;
import io.github.dinglydo.town.party.Party;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.persons.assigner.Assigner;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.phases.PhaseManager;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.util.RestHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction.RoleData;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

/**
 * Represents the actual DDG game
 * @author Amr Ojjeh
 *
 */
public class DiscordGame
{
	private final MainListener ml;
	private final DiscordGameConfig config;

	// Important channels (Name : id)
	private HashMap<String, Long> channels = new HashMap<>();
	private DiscordRoles discordRoles = new DiscordRoles(this);
	private PriorityQueue<TownEvent> events = new PriorityQueue<>();
	private PhaseManager phaseManager = new PhaseManager();

	private LinkedList<DiscordGamePerson> savedForMorning = new LinkedList<>();
	private ArrayList<DiscordGamePerson> players = new ArrayList<>();
	private ArrayList<Role> roles = new ArrayList<>();
	private FactionManager factionManager = new FactionManager(this);
	private Assigner assignerUsed;

	private int dayNum = 1;
	private boolean ended = false;

	// For listener
	DiscordGameListener listener = new DiscordGameListener(this);
	boolean serverCreated = false;
	boolean registeredListener = false;
	long identifier = 0;
	long gameGuildId;

	/**
	 * The discordGame constructor
	 * @param ml listener
	 * @param config configuration chosen
	 */
	private DiscordGame(MainListener ml, DiscordGameConfig config)
	{
		this.ml = ml;
		this.config = config;
	}

	/**
	 * Get the DiscordGame JDA
	 * @return the JDA
	 */
	public JDA getJDA()
	{
		return ml.getJDA();
	}

	/**
	 * The additional prefix that could be used besides pg.
	 * @return the String that represents the prefix
	 */
	public String getPrefix()
	{
		return "!";
	}

	/**
	 * The configuration that was chosen for the game
	 * @return the configuration
	 */
	public DiscordGameConfig getConfig()
	{
		return config;
	}

	/**
	 * The list of players in the game. Note that this is NOT a copy
	 * @return the list of players
	 */
	public ArrayList<DiscordGamePerson> getPlayersCache()
	{
		return players;
	}

	/**
	 * The TvM roles within the game
	 * @return the TvM roles used in the game
	 */
	public ArrayList<Role> getRoles()
	{
		return roles;
	}

	/**
	 * Add a TvM role to the game. Used only by RoleBuilder
	 * @param role
	 */
	public void addRole(Role role)
	{
		if (getRole(role.getRole()) == null)
			getRoles().add(role);
		else throw new IllegalArgumentException("Role already exists");
	}

	/**
	 * Gets the instance of the role based on the TVMRole enum
	 * @param role The TVMRole enum
	 * @return the Role instance specific to the game
	 */
	@Nullable
	public Role getRole(TVMRole role)
	{
		for (Role r : getRoles())
		{
			if (r.getRole() == role)
				return r;
		}
		return null;
	}

	/**
	 * Get the faction manager
	 * @return the faction manager
	 */
	public FactionManager getFactionManager()
	{
		return factionManager;
	}

	/**
	 * The assigner that was used
	 * @return the assigner used to assign the roles to each person
	 */
	public Assigner getAssignerUsed()
	{
		return assignerUsed;
	}

	/**
	 * Registers discordgame as a listener
	 * @param register if true, discordgame will listen for messages
	 */
	public void registerAsListener(boolean register)
	{
		if (register && !registeredListener)
		{
			getJDA().addEventListener(listener);
			registeredListener = true;
		}
		else if (!register && registeredListener)
		{
			getJDA().removeEventListener(listener);
			registeredListener = false;
		}
	}

	/**
	 * Is DiscordGame listening for messages?
	 * @return If true, then DiscordGame is listening for messages
	 */
	public boolean isRegisteredListener()
	{
		return registeredListener;
	}

	/**
	 * Create the Discord server to play the game. Also instantiates the DiscordGame object
	 * @param party The party that corresponds to the game
	 * @param identifier An identifier which will be used to mark the server. It's usually the message ID
	 * @return The DiscordGame created
	 */
	public static DiscordGame createServer(Party party, long identifier)
	{
		DiscordGame game = new DiscordGame(party.getMainListener(), party.getConfig());
		game.identifier = identifier;
		game.registerAsListener(true);
		game.assignerUsed = game.getConfig().getGameMode().build(party, game, game.getConfig().isRandom());
		game.createServer();
		return game;
	}

	private void createServer()
	{
		GuildAction ga = getJDA().createGuild(config.getGameMode().getName());
		createNewChannels(ga);
		ga.newRole().setName("" + identifier);
		ga.queue();
	}

	private void createNewChannels(GuildAction g)
	{
		// this channel used for general game updates
		for (Role role : getRoles())
			g.newRole().setName(role.getName()).setPermissionsRaw(0l);

		g.newRole().setName("Bot").addPermissions(Permission.ADMINISTRATOR).setColor(Color.YELLOW)
		.setHoisted(true);

		g.newRole().setName("Player").setColor(Color.CYAN)
		.setPermissionsRaw(QP.readPermissions() | QP.writePermissions() | QP.speakPermissions())
		.setHoisted(true);

		RoleData deadPlayerRoleData = g.newRole().setName("Dead").setColor(Color.GRAY)
				.setPermissionsRaw(QP.readPermissions())
				.setHoisted(false);

		RoleData defendantRoleData = g.newRole().setName("Defendant").setColor(Color.GREEN)
				.setPermissionsRaw(QP.speakPermissions() | QP.writePermissions() | QP.readPermissions())
				.setHoisted(true);

		// players discussing during the day
		g.newChannel(ChannelType.TEXT, "daytime_discussion")
		.setPosition(0)
		.addPermissionOverride(g.getPublicRole(), QP.readPermissions(), QP.writePermissions())
		.addPermissionOverride(defendantRoleData, QP.readPermissions() | QP.writePermissions(), 0)
		.addPermissionOverride(deadPlayerRoleData, QP.readPermissions(), QP.writePermissions());

		for (DiscordGamePerson p : getPlayersCache())
		{
			g.newChannel(ChannelType.TEXT, "private")
			.setPosition(1)
			.addPermissionOverride(g.getPublicRole(), 0, QP.readPermissions() | QP.writePermissions())
			.setTopic(p.getRealName()); // This will be used as an identifier
		}

		//for dead players
		g.newChannel(ChannelType.TEXT, "the_afterlife")
		.setPosition(2)
		.addPermissionOverride(g.getPublicRole(), 0, QP.readPermissions() | QP.writePermissions())
		.addPermissionOverride(deadPlayerRoleData, QP.readPermissions() | QP.writePermissions(), 0);
	}

	/**
	 * End discord game. Reset everything so that everyone can talk and message
	 */
	public void endGame()
	{
		ended = true;
		phaseManager.end();
		RestHelper.queueAll
		(
				getDiscordRole("player").muteAllInRole(false),
				setChannelVisibility("dead", "daytime_discussion", true, true),
				setChannelVisibility("player", "daytime_discussion", true, true),
				setChannelVisibility("player", "the_afterlife", true, true),
				sendMessageToTextChannel("daytime_discussion",
				"The game has ended! You can either `!delete` the server or `!transfer`" +
				" the server. In 60 seconds if no choice is made, the server will delete itself." +
				" (To transfer, the party leader must be in the server)")
		);
		openPrivateChannels();
		phaseManager.start(this, new End(this, phaseManager));
	}

	/**
	 * Has the game ended?
	 * @return True if the game has ended
	 */
	public boolean hasEnded()
	{
		return ended;
	}

	/**
	 * Was the Discord server created?
	 * @return True if the Discord server was created
	 */
	public boolean wasServerCreated()
	{
		return serverCreated;
	}

	/**
	 * Delete the Discord server
	 */
	public void deleteServer()
	{
		phaseManager.end();
		registerAsListener(false);
		ml.endDiscordGame(this);
		getGuild().delete().queue();
	}

	/**
	 * Transfer the Discord server to a member of the server
	 * @param member The member which is meant to become the owner of the Discord server
	 */
	public void transfer(@Nonnull Member member)
	{
		phaseManager.end();
		getGuild().transferOwnership(member).reason("The game has ended").queue();
		ml.endDiscordGame(this);
		// Listener is unregistered in DiscordGameListener::onGuildUpdateOwner
	}

	/**
	 * Get the DiscordGamePerson equivalent of the Discord Member
	 * @param member A Discord Member
	 * @return The DiscordGamePerson which corresponds to the Member. If null, the member doesn't correspond to any person
	 */
	@Nullable
	public DiscordGamePerson getPerson(Member member)
	{
		return getPerson(member.getIdLong());
	}

	@Nullable
	private DiscordGamePerson getPerson(long id)
	{
		for (DiscordGamePerson person : getPlayersCache())
			if (person.getID() == id)
				return person;
		return null;
	}

	/**
	 * Get the reference number which corresponds to the DiscordGamePerson
	 * @param person A discordGamePerson which belongs to this game
	 * @return The reference number which corresponds to the player
	 */
	public int getReferenceFromPerson(@Nonnull DiscordGamePerson person)
	{
		int ref = getPlayersCache().indexOf(person) + 1;
		if (ref <= 0)
			throw new IllegalArgumentException("Person does not exist");
		return ref;
	}

	/**
	 * Receive the DiscordGamePerson which corresponds to the reference number
	 * @param ref The reference number
	 * @return The DiscordGamePerson which corresponds to the number
	 */
	// TODO: Index out of range when calling "!a 12"
	public DiscordGamePerson getPersonFromReference(int ref)
	{
		return getPlayersCache().get(ref - 1);
	}

	/**
	 * Get all the alive players
	 * @return The alive players
	 */
	public ArrayList<DiscordGamePerson> getAlivePlayers()
	{
		ArrayList<DiscordGamePerson> alive = new ArrayList<>();
		getPlayersCache().stream().filter(p -> p.isAlive()).forEach(p -> alive.add(p));
		return alive;
	}

	/**
	 * Get textchannel based on the name
	 * @param channelName The name of the text channel
	 * @return The textchannel corresponding to the name
	 */
	public TextChannel getTextChannel(String channelName)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		Long channelID = channels.get(channelName);
		return getTextChannel(channelID);
	}

	/**
	 * Get textchannel based on id
	 * @param channelID The id of the text channel
	 * @return The textchannel corresponding to the id
	 */
	public TextChannel getTextChannel(long channelID)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		return getGuild().getTextChannelById(channelID);
	}

	/**
	 * Get VoiceChannel based on the name
	 * @param channelName The name of the text channel
	 * @return The VoiceChannel corresponding to the name
	 */
	public VoiceChannel getVoiceChannel(String channelName)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		return getGuild().getVoiceChannelsByName(channelName, false).get(0);
	}

	/**
	 * Add an event that'll be processed on dispatch
	 * @param event The event which will be processed on dispatch
	 */
	public void addEvent(TownEvent event)
	{
		events.add(event);
	}

	/**
	 * Remove the event from the queue
	 * @param event The event to be removed
	 */
	public void removeEvent(TownEvent event)
	{
		events.remove(event);
	}

	/**
	 * Dispatch all events added
	 */
	public void dispatchEvents()
	{
		if (events.size() == 0) return;
		TownEvent event = events.remove();
		System.out.println("DiscordGame.java:325 -> " + event.getUser());
		for (DiscordGamePerson person : getPlayersCache())
			person.onEvent(event);

		event.postDispatch();
		dispatchEvents();
	}

	/**
	 * Get the current phase
	 * @return The current phase
	 */
	public Phase getCurrentPhase()
	{
		return phaseManager.getCurrentPhase();
	}

	/**
	 * Get the user corresponding to the DiscordGamePerson
	 * @param person The person within the game
	 * @return The Discord user corresponding to the person
	 */
	public User getUser(DiscordGamePerson person)
	{
		return getJDA().getUserById(person.getID());
	}

	/**
	 * The id of the Discord Server
	 * @return The id of the Discord Server
	 */
	public long getGuildId()
	{
		return gameGuildId;
	}

	/**
	 * The Guild object representing the Discord Server
	 * @return The Guild object representing the Discord Server
	 */
	public Guild getGuild()
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		return getJDA().getGuildById(getGuildId());
	}

	/**
	 * Return discord role via name
	 * @param roleName Role name
	 * @return the DiscordRole corresponding to the name
	 */
	@Nullable
	public DiscordRole getDiscordRole(String roleName)
	{
		for (DiscordRole role : discordRoles)
		{
			if (role.getName().equalsIgnoreCase(roleName))
			{
				return role;
			}
		}
		return null;
	}

	/**
	 * Send a message to a text channel
	 * @param channelName Name of the channel
	 * @param msg Contents of the message
	 * @return The message action to be queued
	 */
	public MessageAction sendMessageToTextChannel(String channelName, String msg)
	{
		return getTextChannel(channelName).sendMessage(msg);
	}

	/**
	 * Send a message to a text channel
	 * @param channelID Channel id
	 * @param msg message content
	 * @return The MessageAction to be queued
	 */
	public MessageAction sendMessageToTextChannel(Long channelID, String msg)
	{
		return getTextChannel(channelID).sendMessage(msg);
	}

	/**
	 * Send a message to a text channel
	 * @param channelName Channel name
	 * @param embed message content
	 * @return The MessageAction to be queued
	 */
	public MessageAction sendMessageToTextChannel(String channelName, MessageEmbed embed)
	{
		return getTextChannel(channelName).sendMessage(embed);
	}

	/**
	 * Send a message to a text channel
	 * @param channelID Channel id
	 * @param embed message content
	 * @return The MessageAction to be queued
	 */
	public MessageAction sendMessageToTextChannel(Long channelID, MessageEmbed embed)
	{
		return getTextChannel(channelID).sendMessage(embed);
	}

	/**
	 * Get a DiscordMessage from a textchannel name
	 * @param channelName Then name of the textchannel
	 * @param messageID The message ID to be retrieved
	 * @return The RestAction to be queued
	 */
	public RestAction<Message> getMessage(String channelName, long messageID)
	{
		return getTextChannel(channelName).retrieveMessageById(messageID);
	}

	/**
	 * Start the game. Called after everyone joins by DiscordGameListener
	 */
	protected void startGame()
	{
		config.getGameMode().start(this, phaseManager);
	}

	/**
	 * Make or delete the voicechannel
	 * @param channelName The name of the channel
	 * @param show True if the VC showed be displayed. False if it should be deleted
	 * @return The RestAction to be queued
	 */
	public RestAction<?> toggleVC(String channelName, boolean show)
	{
		if (!show)
			return getVoiceChannel(channelName).delete();
		else
			return getGuild().createVoiceChannel("Daytime");
	}

	/**
	 * Set channel visibility for a Discord role
	 * @param roleName Name of the Discord role
	 * @param channelName Channel name
	 * @param read Can read messages if true
	 * @param write Can send messages if true
	 * @return The action to be queued
	 */
	public PermissionOverrideAction setChannelVisibility(String roleName, String channelName, boolean read, boolean write)
	{
		return setChannelVisibility(getDiscordRole(roleName).getRole(), channelName, read, write);
	}

	/**
	 * Open private channels to all players
	 */
	private void openPrivateChannels()
	{
		getPlayersCache().forEach(p -> setChannelVisibility(getGuild().getPublicRole(), p.getPrivateChannel(), true, false).queue());
	}

	/**
	 * Set the channel visiblity for one person
	 * @param p The person
	 * @param channelName The channel name
	 * @param read Can read messages if true
	 * @param write Can send messages if true
	 * @return
	 */
	public PermissionOverrideAction setChannelVisibility(DiscordGamePerson p, String channelName, boolean read, boolean write)
	{
		if (p.isDisconnected()) return null;
		Member member = p.getMember();
		if (member == null) throw new IllegalArgumentException("Invalid person.");
		return setChannelVisibility(member, channelName, read, write);
	}

	private PermissionOverrideAction setChannelVisibility(IPermissionHolder holder, String channelName, boolean read, boolean write)
	{
		return setChannelVisibility(holder, getTextChannel(channelName), read, write);
	}

	private PermissionOverrideAction setChannelVisibility(IPermissionHolder holder, TextChannel channel, boolean read, boolean write)
	{
		if (channel == null) throw new IllegalArgumentException("Channel name doesn't exist");
		PermissionOverrideAction action = null;
		if (channel.getType().equals(ChannelType.TEXT))
		{
			if (read && !write)
				action = channel.putPermissionOverride(holder).reset().setPermissions(QP.readPermissions(), QP.writePermissions());
			else if (read && write)
				action = channel.putPermissionOverride(holder).reset().setAllow(QP.readPermissions() | QP.writePermissions());
			else
				action = channel.putPermissionOverride(holder).reset().setDeny(QP.readPermissions() | QP.writePermissions());
		}

		return action;
	}

	/**
	 * Save a player's death for morning
	 * @param p The player
	 */
	public void saveForMorning(DiscordGamePerson p)
	{
		savedForMorning.add(p);
	}

	/**
	 * Remove dead person from the queue
	 * @return The player that was saved for morning
	 */
	public DiscordGamePerson getDeathForMorning()
	{
		if (savedForMorning.isEmpty())
			return null;
		return savedForMorning.pop();
	}

	/**
	 * Peek death without removing the person from the queue
	 * @return The player that was saved for morning
	 */
	public DiscordGamePerson peekDeathForMorning()
	{
		return savedForMorning.peek();
	}

	/**
	 * The day number
	 * @return the day number
	 */
	public int getDayNum()
	{
		return dayNum;
	}

	/**
	 * Increment the day number
	 */
	public void startNextDay()
	{
		dayNum++;
	}

	/**
	 * Add the discord roles to the Discord server recently joined
	 */
	protected void assignRoles()
	{
		Guild guild = getGuild();
		guild.addRoleToMember(getJDA().getSelfUser().getIdLong(), guild.getRolesByName("Bot", false).get(0)).queue();

		List<net.dv8tion.jda.api.entities.Role> guildRoles = guild.getRoles();
		for (int x = 0; x < getRoles().size(); ++x)
		{
			net.dv8tion.jda.api.entities.Role townRole = guildRoles.get(guildRoles.size() - x - 2);
			discordRoles.add(townRole.getName(), townRole.getIdLong());
		}

		DiscordRole playerRole = new DiscordRole(this, "player", guild.getRolesByName("Player", false).get(0).getIdLong());
		discordRoles.add(playerRole);

		getPlayersCache().forEach(person -> person.addDiscordRole(playerRole));

		discordRoles.add("dead", guild.getRolesByName("Dead", false).get(0).getIdLong());
		discordRoles.add("defendant", guild.getRolesByName("Defendant", false).get(0).getIdLong());

		for (DiscordGamePerson p : getPlayersCache())
		{
			p.sendMessage("Your role is " + p.getRole().getName());
			p.sendMessage(p.getRole().getHelp());
		}

	}

	protected void assignChannel(GuildChannel channel)
	{
		if (!channel.getName().contentEquals("private")) channels.put(channel.getName(), channel.getIdLong());
		else
			for (DiscordGamePerson p : getPlayersCache())
			{
				TextChannel textChannel = (TextChannel)channel;
				String topic = textChannel.getTopic();
				if (topic != null && topic.contains(p.getRealName()))
				{
					p.setPrivateChannel(textChannel.getIdLong());
					return;
				}
			}
	}

	/**
	 * Send invite to all players
	 * @param guild
	 */
	protected void sendInviteToPlayers()
	{
		Guild guild = getGuild();
		guild.getChannels().get(0).createInvite().queue((invite) -> getPlayersCache().forEach((person) -> person.sendDM(invite.getUrl())));
	}
}
