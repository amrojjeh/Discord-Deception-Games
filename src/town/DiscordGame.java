package town;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction.RoleData;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import town.events.TownEvent;
import town.games.PartyGame;
import town.persons.LobbyPerson;
import town.persons.Person;
import town.phases.Accusation;
import town.phases.End;
import town.phases.Judgment;
import town.phases.Phase;
import town.phases.PhaseManager;

public class DiscordGame
{
	private JDA jda;
	private long guildID;
	private long gameGuildID;
	private long partyLeaderID;
	private PartyGame gameMode;

	// Important channels (Name : id)
	private HashMap<String, Long> channels = new HashMap<>();
	private String prefix = "pg.";
	private HashSet<TownFaction> wonTownRoles = new HashSet<TownFaction>();
	private ArrayList<Person> persons = new ArrayList<>();
	private LinkedList<Person> savedForMorning = new LinkedList<>();
	private PriorityQueue<TownEvent> events = new PriorityQueue<>();
	private PhaseManager phaseManager = new PhaseManager(this);
	private boolean noMinimumPlayers = false;
	private boolean randomMode = false;
	private int dayNum = 1;

	private long playerRoleID;
	private long deadRoleID;
	private long defendantRoleID;

	boolean started = false;
	boolean ended = false;

	public DiscordGame(JDA jda, Long guildId, long partyLeaderId)
	{
		this.jda = jda;
		guildID = guildId;
		partyLeaderID = partyLeaderId;
		gameMode = PartyGame.TALKING_GRAVES;
	}

	public void processMessage(Message message)
	{
		processMessage(prefix, message);
	}

	public void processMessage(String prefix, Message message)
	{
		String lowerCaseMessage = message.getContentRaw().toLowerCase();

		if (!isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "startgame"))
			startGameCommand(message);

		// TODO: Block people if they occupy a certain role
		else if (message.getContentRaw().contentEquals(prefix + "party"))
			displayParty(message.getChannel());
		else if (!isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "join"))
			joinGame(message.getMember().getIdLong(), message.getChannel());
		else if (!isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "leave"))
			leaveGameCommand(message.getMember().getIdLong(), message.getChannel());
		else if (!isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "nomin"))
			noMinCommand(message);
		else if (!isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "setgame"))
			setGameTypeCommand(message);
		else if (!isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "setrand"))
			setRandomCommand(message);
		else if (started && isMessageFromGameGuild(message) && (lowerCaseMessage.startsWith(prefix + "ability") || lowerCaseMessage.startsWith(prefix + "a")))
			activateAbilityCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "cancel"))
			cancelCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "rolehelp"))
			roleHelpCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "vote"))
			voteCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "guilty"))
			guiltyCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "innocent"))
			innocentCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "targets"))
			getPossibleTargetsCommand(message);
	}

	private boolean isMessageFromGameGuild(Message message)
	{
		if (getGameGuild() != null)
			return message.getGuild().getIdLong() == getGameGuild().getIdLong();
		else
			return false;
	}

	private List<Person> getPersonsFromMessage(Message message)
	{
		List<Person> references = getPersonsFromMessageUsingNumReferences(message);
		if (references != null)
			return references;

		return getPersonsFromMessageUsingMentions(message);
	}

	private List<Person> getPersonsFromMessageUsingMentions(Message message)
	{
		List<Member> members = message.getMentionedMembers();
		ArrayList<Person> mentioned = new ArrayList<>();

		if (members.size() == 0) return mentioned;

		for (Member m : members)
		{
			Person p = getPerson(m);
			if (p == null)
			{
				message.getChannel().sendMessage(String.format("Person <@%d> isn't a player", m.getIdLong())).queue();
				return null;
			}

			mentioned.add(p);
		}
		return mentioned;
	}

	private List<Person> getPersonsFromMessageUsingNumReferences(Message message)
	{
		ArrayList<Person> references = new ArrayList<>();
		String[] words = message.getContentStripped().split(" ");
		if (words.length == 1) return references;
		for (int x = 1; x < words.length; ++x)
		{
			// Check if parsable
			int personNum = 0; // 0 can't exist as a ref
			try
			{
				personNum = Integer.parseInt(words[x]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}

			Person reference = getPerson(personNum);
			if (reference == null)
			{
				message.getChannel().sendMessage(String.format("Person with number %d doesn't exist", personNum)).queue();
				return null;
			}
			references.add(reference);
		}

		return references;
	}

	private void setGameTypeCommand(Message message)
	{
		if (message.getMember().getIdLong() != partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", partyLeaderID)).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ", 2);

		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + prefix + "setGame 2` OR `" + prefix + "setGame Mashup`").queue();
			return;
		}

		message.getChannel().sendMessage(setGameType(words[1])).queue();
	}

	public String setGameType(String game)
	{
		PartyGame gameToChangeTo = PartyGame.getGame(game);
		if (gameToChangeTo == null)
			return "FAILED: Game **" + game + "** was not found.";

		gameMode = gameToChangeTo;
		String extra = "";
		if (!gameMode.hasRandom() && randomMode)
			extra = setRandomMode(false) + "\n";
		return extra + "Game mode was set to **" + gameMode.getName() + "**.";
	}

	private void setRandomCommand(Message message)
	{
		if (message.getMember().getIdLong() != partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", partyLeaderID)).queue();
			return;
		}

		String words[] = message.getContentRaw().split(" ");
		if (words.length != 2)
		{
			message.getChannel().sendMessage("Syntax is: `" + prefix + "setRand [0|1]`");
			return;
		}

		Integer activator = JavaHelper.parseInt(words[1]);
		if (activator == null)
		{
			message.getChannel().sendMessage("Syntax is: `" + prefix + "setRand [0|1]`");
			return;
		}

		setRandomMode(activator == 1);
		message.getChannel().sendMessage(setRandomMode(activator == 1)).queue();
	}

	public String setRandomMode(boolean randomVal)
	{
		if (!getGameType().hasRandom() && randomVal)
			return "FAILED: Game mode doesn't support random";

		randomMode = randomVal;
		if (randomMode)
			return "Random mode was activated";
		return "Random mode was disabled";
	}

	private void getPossibleTargetsCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		List<Person> targets = user.getPossibleTargets();

		if (targets == null || targets.isEmpty())
		{
			user.sendMessage("No possible targets");
			return;
		}

		String description = "";
		String format = "%d. <@%d> ";
		for (Person p : targets)
			description += String.format(format, p.getNum(), p.getID()) + (p.isDisconnected() ? "(d)\n" : "\n");
		MessageEmbed embed = new EmbedBuilder().setColor(Color.YELLOW).setTitle("Possible targets").setDescription(description).build();
		user.sendMessage(embed);
	}

	private void noMinCommand(Message message)
	{
		if (message.getMember().getIdLong() != partyLeaderID)
		{
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can configure the game!", partyLeaderID)).queue();
			return;
		}

		String syntax = "Syntax is: " + prefix + "nomin [0|1]";
		String words[] = message.getContentRaw().split(" ");
		int activator = 0;
		if (words.length != 2) message.getChannel().sendMessage(syntax).queue();
		else
		{
			try
			{
				activator = Integer.parseInt(words[1]);
			}
			catch (NumberFormatException e)
			{
				message.getChannel().sendMessage(syntax).queue();
				return;
			}
			noMinimumPlayers = activator == 1;
			if (noMinimumPlayers) message.getChannel().sendMessage("No minimum players requried anymore.").queue();
			else message.getChannel().sendMessage("Default minimum players required.").queue();
		}
	}

	private void startGameCommand(Message message)
	{
		if (started)
			message.getChannel().sendMessage("Game is already running!").queue();
		else if (persons.isEmpty())
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
		else if (message.getMember().getIdLong() != partyLeaderID)
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", partyLeaderID)).queue();
		else if (!noMinimumPlayers && getPlayers().size() < gameMode.getMinimum())
			message.getChannel().sendMessage("Not enough players to play " + gameMode.getName() + "!").queue();
		else
		{
			message.getChannel().sendMessage("Game has started! Creating server...").queue();
			startGame();
		}
	}

	private void activateAbilityCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		user.sendMessage(user.ability(getPersonsFromMessage(message)));
	}

	private void cancelCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		Phase currentPhase = getCurrentPhase();
		if (currentPhase instanceof Accusation)
		{
			Accusation acc = (Accusation)currentPhase;
			message.getChannel().sendMessage(acc.cancelVote(user)).queue();
			return;
		}

		if (currentPhase instanceof Judgment)
		{
			if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

			Judgment j = (Judgment)currentPhase;
			message.getChannel().sendMessage(j.abstain(user)).queue();
			return;
		}

		message.getChannel().sendMessage(user.cancel()).queue();
	}

	private void roleHelpCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		user.sendMessage(user.getHelp());
	}

	private void voteCommand(Message message)
	{
		Phase phase = getCurrentPhase();
		if (!(phase instanceof Accusation))
		{
			message.getChannel().sendMessage("You can only vote for trial once the accusation phase starts!").queue();
			return;
		}

		List<Person> referenced = getPersonsFromMessage(message);
		if (referenced == null)
		{
			System.out.println("Thing was null mate");
			return;
		}

		if (referenced.isEmpty())
		{
			message.getChannel().sendMessage("You have to vote one person! Ex: `" + prefix + "vote 2`").queue();
			return;
		}

		if (referenced.size() > 1)
		{
			message.getChannel().sendMessage("You can only vote one person! Ex: `" + prefix + "vote 2`").queue();
			return;
		}

		Person accuser = getPerson(message.getMember());;
		Person accused = referenced.get(0);

		Accusation acc = (Accusation)phase;
		sendMessageToTextChannel("daytime_discussion", acc.vote(accuser, accused)).queue();
	}

	private void displayParty(MessageChannel channelUsed)
	{
		Phase currentPhase = getCurrentPhase();
		if (currentPhase instanceof Accusation)
		{
			Accusation acc = (Accusation)currentPhase;
			channelUsed.sendMessage(acc.generateList()).queue();
			return;
		}

		String description = "";
		String format = "%d. <@%d> ";
		for (Person p : persons)
			description += String.format(format, p.getNum(), p.getID()) + (p.isDisconnected() ? "(d)\n" : "\n");
		MessageEmbed embed = new EmbedBuilder().setColor(Color.YELLOW).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
	}

	private void guiltyCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		Phase currentPhase = getCurrentPhase();

		if (!(currentPhase instanceof Judgment))
		{
			String msg = String.format("<@%d> can only vote guilty once someone's been accused.", user.getID());
			message.getChannel().sendMessage(msg).queue();
			return;
		}

		if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

		Judgment j = (Judgment)currentPhase;
		message.getChannel().sendMessage(j.guilty(user)).queue();
	}

	private void innocentCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		Phase currentPhase = getCurrentPhase();

		if (!(currentPhase instanceof Judgment))
		{
			String msg = String.format("<@%d> can only vote innocent once someone's been accused.", user.getID());
			message.getChannel().sendMessage(msg).queue();
			return;
		}

		if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

		Judgment j = (Judgment)currentPhase;
		message.getChannel().sendMessage(j.innocent(user)).queue();
	}

	public void createNewChannels(GuildAction g)
	{
		// this channel used for general game updates
		g.newRole().setName("Bot").addPermissions(Permission.ADMINISTRATOR).setColor(Color.YELLOW);

		g.newRole().setName("Player").setColor(Color.CYAN)
				.setPermissionsRaw(QP.readPermissions() | QP.writePermissions() | QP.speakPermissions());

		RoleData deadPlayerRoleData = g.newRole().setName("Dead").setColor(Color.GRAY)
				.setPermissionsRaw(QP.readPermissions());

		RoleData defendantRoleData = g.newRole().setName("Defendant").setColor(Color.GREEN)
				.setPermissionsRaw(QP.speakPermissions() | QP.writePermissions() | QP.readPermissions());

		// FIXME: WARNING: Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added
		// Is being caued by the addPermissionOverride method.
		g.newChannel(ChannelType.VOICE, "Daytime").setPosition(0);

		// players discussing during the day
		g.newChannel(ChannelType.TEXT, "daytime_discussion")
		.setPosition(0)
		.addPermissionOverride(g.getPublicRole(), QP.readPermissions(), QP.writePermissions())
		.addPermissionOverride(defendantRoleData, QP.readPermissions() | QP.writePermissions(), 0);

		for (Person p : getPlayers())
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

	public void addEvent(TownEvent event)
	{
		events.add(event);
		events.forEach(e -> System.out.println(e.toString()));
	}

	public void removeEvent(TownEvent event)
	{
		events.remove(event);
	}

	public void startGame()
	{
		started = true;

		// TODO: Add an icon to the server
		gameMode.build(this, randomMode);

		GuildAction ga = jda.createGuild(gameMode.getName());
		createNewChannels(ga);
		ga.newRole().setName("" + guildID);
		ga.queue();
	}

	public void getNewGuild(Guild guild)
	{
		guild.getChannels().get(0).createInvite().queue((invite) -> persons.forEach((person) -> sendDMTo(person, invite.getUrl()).queue()));
		gameGuildID = guild.getIdLong();
		guild.getChannels(true).forEach((channel) -> assignChannel(channel));
		getGameGuild().addRoleToMember(jda.getSelfUser().getIdLong(), guild.getRolesByName("Bot", false).get(0)).queue();

		playerRoleID = guild.getRolesByName("Player", false).get(0).getIdLong();
		deadRoleID = guild.getRolesByName("Dead", false).get(0).getIdLong();
		defendantRoleID = guild.getRolesByName("Defendant", false).get(0).getIdLong();

		for (Person p : getPlayers())
		{
			p.sendMessage("Your role is " + p.getType().getName());
			p.sendMessage(p.getHelp());
		}

		guild.getPublicRole().getManager().reset().queue();
		sendMessageToTextChannel("daytime_discussion", "Waiting for players...").queue();
	}

	public Member getMemberFromGame(Person person)
	{
		return getMemberFromGame(person.getID());
	}

	public Member getMemberFromGame(Long memberID)
	{
		return getGameGuild().getMemberById(memberID);
	}

	public void assignChannel(GuildChannel channel)
	{
		if (!channel.getName().contentEquals("private")) channels.put(channel.getName(), channel.getIdLong());
		else
			for (Person p : getPlayers())
			{
				TextChannel textChannel = (TextChannel)channel;
				String topic = textChannel.getTopic();
				if (topic != null && topic.contains(p.getRealName()))
				{
					p.assignPrivateChannel(textChannel.getIdLong());
					return;
				}
			}
	}

	public void endGame()
	{
		phaseManager.end();
		phaseManager.start(new End(phaseManager));
		ended = true;
	}

	public void transferOrDelete()
	{
		if (!transfer()) deleteServer();
	}

	public void deleteServer()
	{
		phaseManager.end();
		getGameGuild().delete().queue();
	}

	public boolean transfer()
	{
		phaseManager.end();
		for (Person p : getPlayers())
		{
			Member member = getMemberFromGame(p);
			if (member == null) continue;
			getGameGuild().transferOwnership(member).reason("The game has ended").queue();
			return true;
		}
		return false;
	}

	public void joinGame(long id, MessageChannel channelUsed)
	{
		if (started)
		{
			String message = String.format("Can't join game until session is over <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (getPerson(id) != null)
		{
			String message = String.format("<@%d> already joined! Check party members with " + prefix + "party", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		persons.add(new LobbyPerson(this, persons.size() + 1, id));
		String message = String.format("<@%d> joined the lobby", id);
		channelUsed.sendMessage(message).queue();
	}

	private void leaveGameCommand(long id, MessageChannel channelUsed)
	{
		if (started)
		{
			String message = String.format("Can't leave a game after it has started. Leave the server instead. <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (getPerson(id) == null)
		{
			String message = String.format("Can't leave a game you're not in <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (id == partyLeaderID)
		{
			String message = String.format("Party leader can't leave the party. `" + prefix + "endParty` instead <@%d>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		persons.remove(getPerson(id));
		for (int x = 1; x <= persons.size(); ++x)
			persons.get(x - 1).setNum(x);

		String message = String.format("You've been removed from the party <@%d>", id);
		channelUsed.sendMessage(message).queue();
	}

	public Person getPerson(Member member)
	{
		return getPerson(member.getIdLong());
	}

	public Person getPerson(int refNum)
	{
		for (Person person : persons)
			if (person.getNum() == refNum)
				return person;
		return null;
	}

	public Person getPerson(long id)
	{
		for (Person person : persons)
			if (person.getID() == id)
				return person;
		return null;
	}

	public List<Person> getPlayersCache()
	{
		return persons;
	}

	public List<Person> getPlayers()
	{
		return new ArrayList<>(persons);
	}

	public List<Person> getAlivePlayers()
	{
		ArrayList<Person> alive = new ArrayList<>();
		persons.stream().filter(p -> p.isAlive()).forEach(p -> alive.add(p));
		return alive;
	}

	public List<Person> getDeadPlayers()
	{
		ArrayList<Person> dead = new ArrayList<>();
		persons.stream().filter(p -> !p.isAlive()).forEach(p -> dead.add(p));
		return dead;
	}

	public GuildChannel getGuildChannel(String channelName)
	{
		Long channelID = channels.get(channelName);
		return getGuildChannel(channelID);
	}

	public GuildChannel getGuildChannel(Long channelID)
	{
		if (channelID != null)
			return getGameGuild().getGuildChannelById(channelID);
		return null;
	}

	public TextChannel getTextChannel(String channelName)
	{
		Long channelID = channels.get(channelName);
		return getTextChannel(channelID);
	}

	public TextChannel getTextChannel(Long channelID)
	{
		if (channelID != null)
			return getGameGuild().getTextChannelById(channelID);
		return null;
	}

	public VoiceChannel getVoiceChannel(String channelName)
	{
		return getGameGuild().getVoiceChannelsByName(channelName, false).get(0);
	}

	public void dispatchEvents()
	{
		if (events.size() == 0) return;
		TownEvent event = events.remove();
		for (Person person : persons)
			person.onEvent(event);

		event.postDispatch();
		dispatchEvents();
	}

	public Phase getCurrentPhase()
	{
		return phaseManager.getCurrentPhase();
	}

	public User getUser(Person person)
	{
		return jda.getUserById(person.getID());
	}

	public long getPartyID()
	{
		return guildID;
	}

	public long getGameID()
	{
		return gameGuildID;
	}

	public Guild getPartyGuild()
	{
		return jda.getGuildById(getPartyID());
	}

	public Guild getGameGuild()
	{
		return jda.getGuildById(getGameID());
	}

	public Role getRole(long roleID)
	{
		return getGameGuild().getRoleById(roleID);
	}

	public Role getRole(String roleName)
	{
		return getRole(getRoleID(roleName));
	}

	private long getRoleID(String roleName)
	{
		if (!started) throw new IllegalStateException("Game has not started, can't get roles");
		switch (roleName.toLowerCase())
		{
		case "player":
			return playerRoleID;
		case "dead":
			return deadRoleID;
		case "defendant":
			return defendantRoleID;
		default:
			throw new IllegalArgumentException("Role name not found");
		}
	}

	private RestAction<Message> sendDMTo(Person person, String msg)
	{
		return jda.getUserById(person.getID()).openPrivateChannel()
		.flatMap((channel) -> channel.sendMessage(msg));
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

	public void gameGuildJoin(Member member)
	{
		// Check if member was in the lobby
		boolean shouldKick = true;
		for (Person p : getPlayers())
			if (p.getID() == member.getUser().getIdLong())
			{
				getGameGuild().addRoleToMember(member, getRole(playerRoleID)).queue();
				TextChannel textChannel = getTextChannel(p.getChannelID());
				textChannel.putPermissionOverride(getMemberFromGame(p)).setAllow(QP.readPermissions() | QP.writePermissions()).queue();
				shouldKick = false;
				if (getPlayers().size() == getGameGuild().getMemberCount() - 1)
					phaseManager.start();
				break;
			}

		if (shouldKick)
			member.kick("He was not part of the lobby").queue();
	}

	public RestAction<?> toggleVC(String channelName, boolean show)
	{
		if (!show)
			return getVoiceChannel(channelName).delete();
		else
			return getGameGuild().createVoiceChannel("Daytime");
	}

	public PermissionOverrideAction setChannelVisibility(String roleName, String channelName, boolean read, boolean write)
	{
		return setChannelVisibility(getRole(getRoleID(roleName)), channelName, read, write);
	}

	public PermissionOverrideAction setChannelVisibility(Person p, String channelName, boolean read, boolean write)
	{
		if (p.isDisconnected()) return null;
		Member member = getMemberFromGame(p);
		if (member == null) throw new IllegalArgumentException("Invalid person.");
		return setChannelVisibility(member, channelName, read, write);
	}

	private PermissionOverrideAction setChannelVisibility(IPermissionHolder holder, String channelName, boolean read, boolean write)
	{
		GuildChannel channel = getGuildChannel(channelName);
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

	public ArrayList<Person> findAllWithTownRole(TownRole role) {
		ArrayList<Person> peeps = new ArrayList<>();
		for(Person p : persons) {
			if(p.getType().equals(role)) {
				peeps.add(p);
			}
		}
		return peeps;
	}

	public RoleManager muteAllInRole(String roleName, boolean shouldMute)
	{
		Role role = getRole(roleName);
		List<Member> members = getGameGuild().getMembersWithRoles(role);
		for (Member member : members)
		{
			getPerson(member).mute(shouldMute);
			if (member.getVoiceState().inVoiceChannel()) member.mute(shouldMute).queue(v -> {}, v -> {});
		}
		if (shouldMute)
			return role.getManager().revokePermissions(Permission.VOICE_SPEAK);
		return role.getManager().setPermissions(Permission.VOICE_SPEAK);
	}

	public RoleManager muteAllInRoleExcept(String roleName, boolean shouldMute, Person p)
	{
		Role role = getRole(roleName);
		List<Member> members = getGameGuild().getMembersWithRoles(role);
		for (Member member : members)
		{
			Person person = getPerson(member);
			if (person != p)
				person.mute(shouldMute);
			if (member.getVoiceState().inVoiceChannel()) member.mute(person.isMuted()).queue(v -> {}, v -> {});
		}
		if (shouldMute)
			return role.getManager().revokePermissions(Permission.VOICE_SPEAK);
		return role.getManager().setPermissions(Permission.VOICE_SPEAK);
	}

	public void gameGuildVoiceJoin(Member m, VoiceChannel channel)
	{
		if (!started) return;
		if (channel.getGuild().getIdLong() != getGameID()) return;

		Person person = getPerson(m);
		m.mute(person.isMuted()).queue();
	}

	public void winTownFaction(TownFaction faction)
	{
		wonTownRoles.add(faction);
	}

	public boolean hasTownFactionWon(TownFaction faction)
	{
		return wonTownRoles.contains(faction);
	}

	public void personDied(Person person, boolean saveForMorning)
	{
		if (!person.isDisconnected())
			getGameGuild()
			.modifyMemberRoles(getMemberFromGame(person), getRole("dead"))
			.queue();

		if (saveForMorning)
			savedForMorning.add(person);
	}

	public Person getDeathForMorning()
	{
		if (savedForMorning.isEmpty())
			return null;
		return savedForMorning.pop();
	}

	public Person peekDeathForMorning()
	{
		return savedForMorning.peek();
	}

	public void gameGuildPersonLeave(Member member)
	{
		Person person = getPerson(member);
		person.disconnect();
		person.die(String.format("<@%d> (%d) committed suicide.", person.getID(), person.getNum()), true);
	}

	public int getDayNum()
	{
		return dayNum;
	}

	public void nextDayStarted()
	{
		dayNum++;
	}

	public PartyGame getGameType()
	{
		return gameMode;
	}

	// Quick Permissions
	private static class QP
	{
		public static long readPermissions()
		{
			return Permission.getRaw(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL);
		}

		public static long writePermissions()
		{
			return Permission.getRaw(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
		}

		public static long speakPermissions()
		{
			return Permission.getRaw(Permission.VOICE_SPEAK, Permission.PRIORITY_SPEAKER);
		}
	}
}
