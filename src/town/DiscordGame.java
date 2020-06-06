package town;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction.RoleData;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import town.events.TownEvent;
import town.persons.Person;
import town.persons.assigner.Assigner;
import town.phases.Accusation;
import town.phases.Phase;
import town.phases.PhaseManager;

public class DiscordGame
{
	JDA jda;
	long guildID;
	long gameGuildID;
	ArrayList<Person> persons; // TODO: Sort based on priority also (SortedSet?)
	LinkedList<TownEvent> events; // TODO: PriorityQueue<E>
	PhaseManager phaseManager;
	boolean started;

	// Important channels (Name : id)
	HashMap<String, Long> channels;
	Assigner assigner = Assigner.buildDefault(this);

	long playerRoleID;
	long botRoleID;
	long aliveRoleID;
	long deadRoleID;

	long partyLeaderID;
	String prefix;

	public DiscordGame(JDA jda, Long guildId, long partyLeaderId)
	{
		this.jda = jda;
		guildID = guildId;
		partyLeaderID = partyLeaderId;
		prefix = "tos.";

		phaseManager = new PhaseManager(this);
		persons = new ArrayList<>();
		events = new LinkedList<>();
		channels = new HashMap<>();

		started = false;
	}

	public void processMessage(Message message)
	{
		// TODO: When game starts, allow ! as a prefix also
		if (!isMessageFromGameGuild(message) && message.getContentRaw().contentEquals(prefix + "startGame"))
			startGameCommand(message);

		// TODO: Block people if they occupy a certain role
		else if (message.getContentRaw().contentEquals(prefix + "party"))
			displayParty(message.getChannel());
		else if (!isMessageFromGameGuild(message) && message.getContentRaw().contentEquals(prefix + "join"))
			joinGame(message.getMember().getIdLong(), message.getChannel());

		else if (started && isMessageFromGameGuild(message) && message.getContentRaw().startsWith(prefix + "ability"))
			activateAbilityCommand(message);

		else if (started && isMessageFromGameGuild(message) && message.getContentRaw().startsWith(prefix + "cancel"))
			cancelAbilityCommand(message);
		else if (started && isMessageFromGameGuild(message) && message.getContentRaw().contentEquals(prefix + "roleHelp"))
			roleHelpCommand(message);
		else if (started && isMessageFromGameGuild(message) && message.getContentRaw().startsWith(prefix + "vote"))
			voteCommand(message);
		// TODO: Add endgame
		// TODO: Instead of transfering the server, offer the option to delete or tranfer. If no choice is made, delete
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
		// TODO: 5 people min.
		else if (persons.isEmpty())
			message.getChannel().sendMessage("Not enough players to start a server!").queue();
		else if (message.getMember().getIdLong() != partyLeaderID)
			message.getChannel().sendMessage(String.format("Only party leader (<@%d>) can start the game!", partyLeaderID)).queue();
		else
		{
			message.getChannel().sendMessage("Game has started! Creating server...").queue();
			startGame();
		}
	}

	private void activateAbilityCommand(Message message)
	{
		Person user = getPerson(message.getMember());
		message.getChannel().sendMessage(user.ability(getPersonsFromMessage(message))).queue();
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
		String format = "%d. %s (%s)\n";
		for (Person p : persons)
			description += String.format(format, p.getNum(), p.getRealName(), p.getNickName());
		MessageEmbed embed = new EmbedBuilder().setColor(Color.YELLOW).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
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

		// FIXME: WARNING: Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added
		// Is being caued by the addPermissionOverride method.
		// FIXME: Setting position is not working
		g.newChannel(ChannelType.VOICE, "Daytime")
		.setPosition(2);

		// players discussing during the day
		g.newChannel(ChannelType.TEXT, "daytime_discussion")
		.setPosition(3);

		for (Person p : getPlayers())
		{
			g.newChannel(ChannelType.TEXT, "private")
			.setPosition(1)
			.addPermissionOverride(playerRoleData, 0, readPermissions() | writePermissions())
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
		//		voiceChannels.put("The Dead", "");
		//		textChannels.put("the_afterlife", "");
	}

	public void addEvent(TownEvent event)
	{
		events.add(event);
	}

	public void removeEvent(TownEvent event)
	{
		events.remove(event);
	}

	public void startGame()
	{
		started = true;

		// TODO: Add an icon to the server

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
		for (Person p : getPlayers())
		{
			if (channel.getType() == ChannelType.TEXT)
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
		channels.put(channel.getName(), channel.getIdLong());
	}

	public void endGame()
	{
		phaseManager.end();
		transferOrDelete();
	}

	public void transferOrDelete()
	{
		Member partyLeader = getMemberFromGame(partyLeaderID);
		if (partyLeader != null)
		{
			System.out.println(getGameGuild().getOwner().getEffectiveName());
			getGameGuild().transferOwnership(partyLeader).reason("The game has ended").queue();
		}

		else getGameGuild().delete().queue();
	}

	public void joinGame(long id, MessageChannel channelUsed)
	{
		if (started)
		{
			String message = String.format("Can't join game until session is over <@%s>", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		if (getPerson(id) != null)
		{
			String message = String.format("<@%s> already joined! Check party members with tos.party", id);
			channelUsed.sendMessage(message).queue();
			return;
		}

		persons.add(assigner.generatePerson(persons.size() + 1, id));
		String message = String.format("<@%s> joined the lobby", id);
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

	public Long getPartyID()
	{
		return guildID;
	}

	public Long getGameID()
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
		jda.getUserById(person.getID()).openPrivateChannel().queue((channel) -> channel.sendMessage(msg).queue());
	}

	public void sendMessageToTextChannel(String channelName, String msg, Consumer<Message> consumer)
	{
		getTextChannel(channelName).sendMessage(msg).queue(consumer);
	}

	public void  sendMessageToTextChannel(Long channelID, String msg, Consumer<Message> consumer)
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

	public void  sendMessageToTextChannel(Long channelID, String msg)
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
		// TODO: If the game starts, remove invite link?
		// TODO: Otherwise boot? Other option is to make him spectator
		// For now we'll boot.

		// Check if member was in the lobby
		boolean shouldKick = true;
		for (Person p : getPlayers())
			if (p.getID().longValue() == member.getUser().getIdLong())
			{
				getGameGuild().addRoleToMember(member, getGameGuild().getRolesByName("Player", false).get(0)).queue();
				TextChannel textChannel = getTextChannel(p.getChannelID());
				textChannel.putPermissionOverride(getMemberFromGame(p)).setAllow(readPermissions() | writePermissions()).queue();
				p.sendMessage("Your role is " + p.getRoleName());
				p.sendMessage(p.getHelp());
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
	public void setChannelVisibility(String channelName, boolean read, boolean write)
	{
		GuildChannel channel = getGuildChannel(channelName);
		PermissionOverrideAction action = null;
		Role playerRole = getRole(playerRoleID);
		if (channel.getType().equals(ChannelType.VOICE))
		{
			if (read)
				action = channel.putPermissionOverride(playerRole).reset().setAllow(connectPermissions());
			else
				action = channel.putPermissionOverride(playerRole).reset().setDeny(connectPermissions());
		}
		else if (channel.getType().equals(ChannelType.TEXT))
		{
			if (read && !write)
				action = channel.putPermissionOverride(playerRole).reset().setPermissions(readPermissions(), writePermissions());
			else if (read && write)
				action = channel.putPermissionOverride(playerRole).reset().setPermissions(readPermissions() | writePermissions(), 0);
			else
				action = channel.putPermissionOverride(playerRole).reset().setDeny(readPermissions() | writePermissions());
		}
		if (action != null)
			action.queue();
	}
	
	// Write doesn't matter if we're setting a voice channel
	public void setChannelVisibility(Person p, String channelName, boolean read, boolean write)
	{
		GuildChannel channel = getGuildChannel(channelName);
		PermissionOverrideAction action = null;
		Role playerRole = getRole(playerRoleID);
		if (channel.getType().equals(ChannelType.VOICE))
		{
			if (read)
				action = channel.putPermissionOverride(getMemberFromGame(p)).reset().setAllow(connectPermissions());
			else
				action = channel.putPermissionOverride(getMemberFromGame(p)).reset().setDeny(connectPermissions());
		}
		else if (channel.getType().equals(ChannelType.TEXT))
		{
			if (read && !write)
				action = channel.putPermissionOverride(getMemberFromGame(p)).reset().setPermissions(readPermissions(), writePermissions());
			else if (read && write)
				action = channel.putPermissionOverride(getMemberFromGame(p)).reset().setPermissions(readPermissions() | writePermissions(), 0);
			else
				action = channel.putPermissionOverride(getMemberFromGame(p)).reset().setDeny(readPermissions() | writePermissions());
		}
		if (action != null)
			action.queue();
	}
	
	public void muteExcept(Person p) {
		for(Person townie : getAlivePlayers()) {
			if(p != townie) getMemberFromGame(townie).mute(true).queue();
		}
	}
	
	public void restoreTalking() {
		for(Person townie : getAlivePlayers()) {
			getMemberFromGame(townie).mute(false).queue();
		}
	}
}
