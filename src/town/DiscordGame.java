package town;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Function;

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
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction.RoleData;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import town.events.TownEvent;
import town.games.PartyGame;
import town.persons.LobbyPerson;
import town.persons.Person;
import town.phases.Accusation;
import town.phases.End;
import town.phases.Judgment;
import town.phases.Night;
import town.phases.Phase;
import town.phases.PhaseManager;
import town.phases.Trial;

public class DiscordGame
{
	private JDA jda;
	private long guildID;
	private long gameGuildID;
	private long partyLeaderID;

	// Important channels (Name : id)
	private HashMap<String, Long> channels = new HashMap<>();

	private HashSet<TownFaction> wonTownRoles = new HashSet<TownFaction>();
	private ArrayList<Person> persons = new ArrayList<>();
	private PriorityQueue<TownEvent> events = new PriorityQueue<>();
	private PhaseManager phaseManager = new PhaseManager(this);
	private PartyGame gameMode;

	private long playerRoleID;
	private long botRoleID;
	private long aliveRoleID;
	private long deadRoleID;

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
		processMessage("tos.", message);
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
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "ability"))
			activateAbilityCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "cancel"))
			cancelAbilityCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "rolehelp"))
			roleHelpCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.startsWith(prefix + "vote"))
			voteCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "guilty"))
			guiltyCommand(message);
		else if (started && isMessageFromGameGuild(message) && lowerCaseMessage.contentEquals(prefix + "innocent"))
			innocentCommand(message);
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

	private void startGameCommand(Message message)
	{
		if (started)
			message.getChannel().sendMessage("Game is already running!").queue();
		else if (persons.isEmpty())
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
		else if (message.getMember().getIdLong() != partyLeaderID)
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", partyLeaderID)).queue();
		else if (getPlayers().size() < gameMode.getMinimum())
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

	private void cancelAbilityCommand(Message message)
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
			message.getChannel().sendMessage("You have to vote one person! Ex: `tos.vote 2`").queue();
			return;
		}

		if (referenced.size() > 1)
		{
			message.getChannel().sendMessage("You can only vote one person! Ex: `tos.vote 2`").queue();
			return;
		}

		Person accuser = getPerson(message.getMember());;
		Person accused = referenced.get(0);

		if (!accuser.isAlive())
		{
			message.getChannel().sendMessage(String.format("Dead men can't vote <@%d>", accuser.getID())).queue();
			return;
		}
		Accusation acc = (Accusation)phase;
		sendMessageToTextChannel("daytime_discussion", acc.vote(accuser, accused));
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
		String format = "%d. <@%d>\n";
		for (Person p : persons)
			description += String.format(format, p.getNum(), p.getID());
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

		Judgment j = (Judgment)currentPhase;
		message.getChannel().sendMessage(j.innocent(user)).queue();
	}

	private long readPermissions()
	{
		return Permission.getRaw(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL);
	}

	private long writePermissions()
	{
		return Permission.getRaw(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
	}

	private long connectPermissions()
	{
		return Permission.getRaw(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL);
	}

	public void createNewChannels(GuildAction g)
	{
		// this channel used for general game updates
		g.newRole().setName("Bot").addPermissions(Permission.ADMINISTRATOR).setColor(Color.YELLOW);
		RoleData playerRoleData = g.newRole().setName("Player").setColor(Color.CYAN);
		RoleData deadPlayerRoleData = g.newRole().setName("Dead").setColor(Color.GRAY);

		// FIXME: WARNING: Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added
		// Is being caued by the addPermissionOverride method.
		g.newChannel(ChannelType.VOICE, "Daytime").setPosition(0);

		//		players discussing during the day
		g.newChannel(ChannelType.TEXT, "daytime_discussion")
		.addPermissionOverride(deadPlayerRoleData, readPermissions(), writePermissions())
		.setPosition(0);

		for (Person p : getPlayers())
		{
			g.newChannel(ChannelType.TEXT, "private")
			.setPosition(1)
			.addPermissionOverride(playerRoleData, 0, readPermissions() | writePermissions())
			.addPermissionOverride(deadPlayerRoleData, 0, readPermissions() | writePermissions())
			.setTopic(p.getRealName()); // This will be used as an identifier
		}

		//		textChannels.put("the_hideout", "");
		//		textChannels.put("the_underground", "");
		// TODO: Do jailor talk in #private
		//		textChannels.put("jailor", "");
		//the "jail" where the bot transfers the jailor's text anonymously, and the jailed player can respond
		//		textChannels.put("jail", "");

		// mafia private chat at night
		//		voiceChannels.put("Mafia", "");
		// vampire private chat at night
		//		voiceChannels.put("Vampires", "");


		//for dead players
		g.newChannel(ChannelType.TEXT, "the_afterlife")
		.setPosition(2)
		.addPermissionOverride(playerRoleData, 0, readPermissions() | writePermissions())
		.addPermissionOverride(deadPlayerRoleData, readPermissions() | writePermissions(), 0);
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
		gameMode.build(this);

		GuildAction ga = jda.createGuild("Town of Salem");
		createNewChannels(ga);
		ga.newRole().setName("" + guildID);
		ga.queue();
	}

	public void getNewGuild(Guild guild)
	{
		guild.getChannels().get(0).createInvite().queue((invite) -> persons.forEach((person) -> sendDMTo(person, invite.getUrl())));
		gameGuildID = guild.getIdLong();
		guild.getChannels(true).forEach((channel) -> assignChannel(channel));
		phaseManager.start();
		getGameGuild().addRoleToMember(jda.getSelfUser().getIdLong(), guild.getRolesByName("Bot", false).get(0)).queue();

		playerRoleID = guild.getRolesByName("Player", false).get(0).getIdLong();
		botRoleID = guild.getRolesByName("Bot", false).get(0).getIdLong();
		deadRoleID = guild.getRolesByName("Dead", false).get(0).getIdLong();

		for (Person p : getPlayers())
		{
			p.sendMessage("Your role is " + p.getType().getName());
			p.sendMessage(p.getHelp());
		}
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
		Member partyLeader = getMemberFromGame(partyLeaderID);
		// TODO: Make it transfer to the first found member instead of just to party leader
		if (partyLeader != null)
		{
			getGameGuild().transferOwnership(partyLeader).reason("The game has ended").queue();
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
			String message = String.format("<@%d> already joined! Check party members with tos.party", id);
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
			String message = String.format("Party leader can't leave the party. `tos.endParty` instead <@%d>", id);
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

	public ArrayList<Person> getPlayers()
	{
		return persons;
	}

	public ArrayList<Person> getAlivePlayers()
	{
		ArrayList<Person> alive = new ArrayList<>();
		persons.stream().filter(p -> p.isAlive()).forEach(p -> alive.add(p));
		return alive;
	}

	public ArrayList<Person> getDeadPlayers()
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
		Long channelID = channels.get(channelName);
		return getVoiceChannel(channelID);
	}

	public VoiceChannel getVoiceChannel(Long channelID)
	{
		if (channelID != null)
			return getGameGuild().getVoiceChannelById(channelID);
		return null;
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

	private void sendDMTo(Person person, String msg)
	{
		jda.getUserById(person.getID()).openPrivateChannel()
		.flatMap((channel) -> channel.sendMessage(msg))
		.queue();
	}

	public void sendMessageToTextChannel(String channelName, String msg, Consumer<Message> consumer)
	{
		getTextChannel(channelName).sendMessage(msg).queue(consumer);
	}

	public void sendMessageToTextChannel(Long channelID, String msg, Consumer<Message> consumer)
	{
		getTextChannel(channelID).sendMessage(msg).queue(consumer);
	}

	public void sendMessageToTextChannel(String channelName, MessageEmbed embed, Consumer<Message> consumer)
	{
		getTextChannel(channelName).sendMessage(embed).queue(consumer);
	}

	public void sendMessageToTextChannel(Long channelID, MessageEmbed embed, Consumer<Message> consumer)
	{
		getTextChannel(channelID).sendMessage(embed).queue(consumer);
	}

	public void sendMessageToTextChannel(String channelName, String msg)
	{
		getTextChannel(channelName).sendMessage(msg).queue();
	}

	public void sendMessageToTextChannel(Long channelID, String msg)
	{
		getTextChannel(channelID).sendMessage(msg).queue();
	}

	public void sendMessageToTextChannel(String channelName, MessageEmbed embed)
	{
		getTextChannel(channelName).sendMessage(embed).queue();
	}

	public void sendMessageToTextChannel(Long channelID, MessageEmbed embed)
	{
		getTextChannel(channelID).sendMessage(embed).queue();
	}

	public void getMessage(String channelName, long messageID, Consumer<Message> consumer)
	{
		getTextChannel(channelName).retrieveMessageById(messageID).queue(consumer);
	}

	public void gameGuildJoin(Member member)
	{
		// TODO: Otherwise boot? Other option is to make him spectator
		// For now we'll boot.

		// Check if member was in the lobby
		boolean shouldKick = true;
		for (Person p : getPlayers())
			if (p.getID() == member.getUser().getIdLong())
			{
				getGameGuild().addRoleToMember(member, getRole(playerRoleID)).queue();
				TextChannel textChannel = getTextChannel(p.getChannelID());
				textChannel.putPermissionOverride(getMemberFromGame(p)).setAllow(readPermissions() | writePermissions()).queue();
				shouldKick = false;
				break;
			}

		if (shouldKick)
			member.kick("He was not part of the lobby").queue();
	}

	public void discconectEveryoneFromVC(String vcName)
	{
		discconectEveryoneFromVC(getVoiceChannel(vcName));
	}

	public void discconectEveryoneFromVC(VoiceChannel channel)
	{
		channel.getMembers().forEach(p -> p.getGuild().kickVoiceMember(p).queue());
	}

	// Write doesn't matter if we're setting a voice channel
	public void setChannelVisibility(String channelName, boolean read, boolean write,
			Function<PermissionOverride, RestAction<PermissionOverride>> func)
	{
		Role playerRole = getRole(playerRoleID);
		setChannelVisibility(playerRole, channelName, read, write, func);
	}

	public void setChannelVisibility(String channelName, boolean read, boolean write)
	{
		Role playerRole = getRole(playerRoleID);
		setChannelVisibility(playerRole, channelName, read, write, null);
	}

	public void removeReadExcept(Person p, String channelName)
	{
		Member member = getMemberFromGame(p);
		if (member == null) throw new IllegalArgumentException("Invalid person.");
		setChannelVisibility(channelName, true, false,
				perm -> getGuildChannel(channelName).putPermissionOverride(member).setAllow(readPermissions() | writePermissions()));
	}

	public void setChannelVisibility(Person p, String channelName, boolean read, boolean write)
	{
		Member member = getMemberFromGame(p);
		if (member == null) throw new IllegalArgumentException("Invalid person.");
		setChannelVisibility(member, channelName, read, write, null);
	}

	public void restoreRead(Person p, String channelName)
	{
		Member member = getMemberFromGame(p);
		if (member == null) throw new IllegalArgumentException("Invalid person.");
		setChannelVisibility(channelName, true, true,
				perm -> getGuildChannel(channelName).putPermissionOverride(member).reset());
	}

	private void setChannelVisibility(IPermissionHolder holder, String channelName, boolean read, boolean write,
			Function<PermissionOverride, RestAction<PermissionOverride>> func)
	{
		GuildChannel channel = getGuildChannel(channelName);
		if (channel == null) throw new IllegalArgumentException("Channel name doesn't exist");
		PermissionOverrideAction action = null;
		if (channel.getType().equals(ChannelType.VOICE))
		{
			if (read)
				action = channel.putPermissionOverride(holder).reset().setAllow(connectPermissions());
			else
				action = channel.putPermissionOverride(holder).reset().setDeny(connectPermissions());
		}
		else if (channel.getType().equals(ChannelType.TEXT))
		{
			if (read && !write)
				action = channel.putPermissionOverride(holder).reset().setPermissions(readPermissions(), writePermissions());
			else if (read && write)
				action = channel.putPermissionOverride(holder).reset().setAllow(readPermissions() | writePermissions());
			else
				action = channel.putPermissionOverride(holder).reset().setDeny(readPermissions() | writePermissions());
		}
		if (action != null && func != null)
			action.flatMap(func).queue();
		else if (action != null)
			action.queue();
	}

	public ArrayList<Person> findAllWithRole(TownRole role) {
		ArrayList<Person> peeps = new ArrayList<>();
		for(Person p : persons) {
			if(p.getType().equals(role)) {
				peeps.add(p);
			}
		}
		return peeps;
	}

	public void muteExcept(String channelName, Person p)
	{
		VoiceChannel channel = getVoiceChannel(channelName);
		if (channel == null) throw new IllegalArgumentException("Can't pass a non-voice channel to muteExcept");
		List<Member> members = channel.getMembers();
		if (members.isEmpty()) return;
		RestAction<Void> action = members.get(0).getIdLong() != p.getID() ? members.get(0).mute(true) : members.get(0).mute(false);
		for (int x = 1; x < members.size(); ++x)
		{
			final int y = x; // Finals are needed for lambdas
			action = action.flatMap(perm -> members.get(y).getIdLong() != p.getID() ? members.get(y).mute(true) : members.get(y).mute(false));
		}
		action.queue();
	}

	public void restoreTalking(String channelName)
	{
		VoiceChannel channel = getVoiceChannel(channelName);
		if (channel == null) throw new IllegalArgumentException("Can't pass a non-voice channel to restoreTalking");
		channel.getMembers().forEach(m -> {m.mute(false).queue();} );
	}

	public void gameGuildVoiceJoin(Member m, VoiceChannel channel)
	{
		if (!started) return;
		if (channel.getGuild().getIdLong() != getGameID()) return;

		Phase phase = getCurrentPhase();
		if (phase instanceof Trial)
		{
			Trial trial = (Trial)phase;
			if (m.getIdLong() != trial.getDefendant().getID())
				m.mute(true).queue();
		}

		else if (!getPerson(m).isAlive())
		{
			if (channel.getIdLong() == getVoiceChannel("Daytime").getIdLong())
				m.mute(true).queue();
			else
				m.mute(false).queue();
		}

		else
			m.mute(false).queue();
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
		getGameGuild()
		.addRoleToMember(person.getID(), getRole(deadRoleID))
		.flatMap((rest) -> getGameGuild().removeRoleFromMember(person.getID(), getRole(playerRoleID)))
		.queue();

		if (saveForMorning && getCurrentPhase() instanceof Night)
		{
			Night night = (Night)getCurrentPhase();
			night.addDeath(person);
		}
	}
}
