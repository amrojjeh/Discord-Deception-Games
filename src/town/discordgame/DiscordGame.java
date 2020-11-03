package town.discordgame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javax.annotation.Nullable;

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
import town.DiscordGameConfig;
import town.GameParty;
import town.MainListener;
import town.events.TownEvent;
import town.persons.DiscordGamePerson;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.roles.Faction;
import town.roles.Role;

//// This represents an ongoing deception game. It's instantiated with pg.startParty
public class DiscordGame
{
	private final MainListener ml;
	private final DiscordGameConfig config;

	// Important channels (Name : id)
	private HashMap<String, Long> channels = new HashMap<>();
	private DiscordRoles discordRoles = new DiscordRoles(this);
	private HashSet<Faction> wonTownRoles = new HashSet<Faction>();
	private PriorityQueue<TownEvent> events = new PriorityQueue<>();
	private PhaseManager phaseManager = new PhaseManager();

	private LinkedList<DiscordGamePerson> savedForMorning = new LinkedList<>();
	private ArrayList<DiscordGamePerson> players = new ArrayList<>();

	private int dayNum = 1;
	private boolean ended = false;

	// For listener
	DiscordGameListener listener = new DiscordGameListener(this);
	boolean serverCreated = false;
	boolean registeredListener = false;
	long identifier = 0;
	long gameGuildId;

	private DiscordGame(MainListener ml, DiscordGameConfig config)
	{
		this.ml = ml;
		this.config = config;
	}

	public JDA getJDA()
	{
		return ml.getJDA();
	}

	public String getPrefix()
	{
		return "!";
	}

	public DiscordGameConfig getConfig()
	{
		return config;
	}

	public ArrayList<DiscordGamePerson> getPlayersCache()
	{
		return players;
	}

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

	public boolean isRegisteredListener()
	{
		return registeredListener;
	}

	public static DiscordGame createServer(GameParty party, long identifier)
	{
		DiscordGame game = new DiscordGame(party.getMainListener(), party.getConfig());
		game.identifier = identifier;
		game.registerAsListener(true);
		game.players = game.getConfig().getGameMode().build(party, game, game.getConfig().isRandom());
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
		for (Role role : getConfig().getGameMode().getClosestRule(getPlayersCache().size()).getRoles())
			g.newRole().setName(role.getName()).setPermissionsRaw(0l);

		g.newRole().setName("Bot").addPermissions(Permission.ADMINISTRATOR).setColor(Color.YELLOW);

		g.newRole().setName("Player").setColor(Color.CYAN)
				.setPermissionsRaw(QP.readPermissions() | QP.writePermissions() | QP.speakPermissions());

		RoleData deadPlayerRoleData = g.newRole().setName("Dead").setColor(Color.GRAY)
				.setPermissionsRaw(QP.readPermissions());

		RoleData defendantRoleData = g.newRole().setName("Defendant").setColor(Color.GREEN)
				.setPermissionsRaw(QP.speakPermissions() | QP.writePermissions() | QP.readPermissions());

		// FIXME: WARNING: Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added
		// Is being caued by the addPermissionOverride method.

		// players discussing during the day
		g.newChannel(ChannelType.TEXT, "daytime_discussion")
		.setPosition(0)
		.addPermissionOverride(g.getPublicRole(), QP.readPermissions(), QP.writePermissions())
		.addPermissionOverride(defendantRoleData, QP.readPermissions() | QP.writePermissions(), 0);

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


//	public void processMessage(String prefix, Message message)
//	{
//		boolean fromGuild = isMessageFromGameGuild(message);
//
//		if (!executeCommand(this, new GlobalCommands(), prefix, message))
//			if (!fromGuild)
//				executeCommand(this, new PartyCommands(), prefix, message);
//			else if (initiated)
//				executeCommand(this, config.getGameMode().getCommands(), prefix, message);
//	}


//
//	public void endGame()
//	{
//		ended = true;
//		phaseManager.end();
//		phaseManager.start(this, new End(this, phaseManager));
//	}
//
//	public boolean hasEnded()
//	{
//		return ended;
//	}

	public boolean wasServerCreated()
	{
		return serverCreated;
	}

	public void transferOrDelete()
	{
		if (!transfer()) deleteServer();
	}

	public void deleteServer()
	{
		phaseManager.end();
		getGuild().delete().queue();
	}

	public boolean transfer()
	{
		phaseManager.end();
		for (DiscordGamePerson p : getPlayersCache())
		{
			Member member = p.getMember();
			if (member == null) continue;
			getGuild().transferOwnership(member).reason("The game has ended").queue();
			return true;
		}
		return false;
	}

	public DiscordGamePerson getPerson(Member member)
	{
		return getPerson(member.getIdLong());
	}

//	public Person getPerson(int refNum)
//	{
//		for (Person person : persons)
//			if (person.getNum() == refNum)
//				return person;
//		return null;
//	}

	public DiscordGamePerson getPerson(long id)
	{
		for (DiscordGamePerson person : getPlayersCache())
			if (person.getID() == id)
				return person;
		return null;
	}

	public ArrayList<DiscordGamePerson> getAlivePlayers()
	{
		ArrayList<DiscordGamePerson> alive = new ArrayList<>();
		getPlayersCache().stream().filter(p -> p.isAlive()).forEach(p -> alive.add(p));
		return alive;
	}

//	public List<Person> getDeadPlayers()
//	{
//		ArrayList<Person> dead = new ArrayList<>();
//		persons.stream().filter(p -> !p.isAlive()).forEach(p -> dead.add(p));
//		return dead;
//	}
//
//	public GuildChannel getGuildChannel(String channelName)
//	{
//		Long channelID = channels.get(channelName);
//		return getGuildChannel(channelID);
//	}
//
//	public GuildChannel getGuildChannel(Long channelID)
//	{
//		if (channelID != null)
//			return getGameGuild().getGuildChannelById(channelID);
//		return null;
//	}
//
	public TextChannel getTextChannel(String channelName)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		Long channelID = channels.get(channelName);
		return getTextChannel(channelID);
	}

	public TextChannel getTextChannel(long channelID)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		return getGuild().getTextChannelById(channelID);
	}

	public VoiceChannel getVoiceChannel(String channelName)
	{
		if (!serverCreated) throw new IllegalStateException("Server not created yet");
		return getGuild().getVoiceChannelsByName(channelName, false).get(0);
	}

	public void addEvent(TownEvent event)
	{
		events.add(event);
	}

	public void removeEvent(TownEvent event)
	{
		events.remove(event);
	}

	public void dispatchEvents()
	{
		if (events.size() == 0) return;
		TownEvent event = events.remove();
		for (DiscordGamePerson person : getPlayersCache())
			person.onEvent(event);

		event.postDispatch();
		dispatchEvents();
	}

	public Phase getCurrentPhase()
	{
		return phaseManager.getCurrentPhase();
	}

	public User getUser(DiscordGamePerson person)
	{
		return getJDA().getUserById(person.getID());
	}

	public long getGuildId()
	{
		return gameGuildId;
	}

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
	public DiscordRole getRole(String roleName)
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

	public MessageAction sendMessageToTextChannel(String channelName, String msg)
	{
		return getTextChannel(channelName).sendMessage(msg);
	}

	public MessageAction sendMessageToTextChannel(Long channelID, String msg)
	{
		return getTextChannel(channelID).sendMessage(msg);
	}

	public MessageAction sendMessageToTextChannel(String channelName, MessageEmbed embed)
	{
		return getTextChannel(channelName).sendMessage(embed);
	}

	public MessageAction sendMessageToTextChannel(Long channelID, MessageEmbed embed)
	{
		return getTextChannel(channelID).sendMessage(embed);
	}

	public RestAction<Message> getMessage(String channelName, long messageID)
	{
		return getTextChannel(channelName).retrieveMessageById(messageID);
	}

	public void startGame()
	{
		// TODO: start game
		System.out.println("Game started");
//		config.getGameMode().start(this, phaseManager);
	}


	public RestAction<?> toggleVC(String channelName, boolean show)
	{
		if (!show)
			return getVoiceChannel(channelName).delete();
		else
			return getGuild().createVoiceChannel("Daytime");
	}

	public PermissionOverrideAction setChannelVisibility(String roleName, String channelName, boolean read, boolean write)
	{
		return setChannelVisibility(getRole(roleName).getRole(), channelName, read, write);
	}

	/**
	 * Open private channels to all players
	 */
	public void openPrivateChannels()
	{
		getPlayersCache().forEach(p -> setChannelVisibility(getGuild().getPublicRole(), p.getPrivateChannel(), true, false).queue());
	}

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

	private PermissionOverrideAction setChannelVisibility(IPermissionHolder holder, long channelId, boolean read, boolean write)
	{
		return setChannelVisibility(holder, getTextChannel(channelId), read, write);
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

//	public void gameGuildVoiceJoin(Member m, VoiceChannel channel)
//	{
//		if (!initiated) return;
//		if (channel.getGuild().getIdLong() != getGameID()) return;
//
//		Person person = getPerson(m);
//		m.mute(person.isMuted()).queue();
//	}
//
//	public void winTownFaction(Faction faction)
//	{
//		wonTownRoles.add(faction);
//	}
//
	public boolean hasTownFactionWon(Faction faction)
	{
		return wonTownRoles.contains(faction);
	}

	public void saveForMorning(DiscordGamePerson p)
	{
		savedForMorning.add(p);
	}

	public DiscordGamePerson getDeathForMorning()
	{
		if (savedForMorning.isEmpty())
			return null;
		return savedForMorning.pop();
	}

	public DiscordGamePerson peekDeathForMorning()
	{
		return savedForMorning.peek();
	}

//	public void memberLeftGameGuild(Member member)
//	{
//		Person person = getPerson(member);
//		if (person != null)
//			person.disconnect();
//	}

	public int getDayNum()
	{
		return dayNum;
	}

	public void startNextDay()
	{
		dayNum++;
	}

	public void assignRoles(Guild guild)
	{
		guild.addRoleToMember(getJDA().getSelfUser().getIdLong(), guild.getRolesByName("Bot", false).get(0)).queue();

		List<net.dv8tion.jda.api.entities.Role> guildRoles = guild.getRoles();
		for (int x = 0; x < config.getGameMode().getClosestRule(getPlayersCache().size()).getRoles().size(); ++x)
		{
			net.dv8tion.jda.api.entities.Role townRole = guildRoles.get(guildRoles.size() - x - 2);
			discordRoles.add(townRole.getName().toLowerCase(), townRole.getIdLong());
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

	public void assignChannel(GuildChannel channel)
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

	public void sendInviteToPlayers(Guild guild)
	{
		guild.getChannels().get(0).createInvite().queue((invite) -> getPlayersCache().forEach((person) -> person.sendDM(invite.getUrl())));
	}
}
