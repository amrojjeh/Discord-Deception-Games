package io.github.dinglydo.town.persons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.discordgame.DiscordRole;
import io.github.dinglydo.town.discordgame.DiscordRoles;
import io.github.dinglydo.town.discordgame.QP;
import io.github.dinglydo.town.events.TownEvent;
import io.github.dinglydo.town.mafia.phases.Night;
import io.github.dinglydo.town.roles.Role;
import io.github.dinglydo.town.roles.RoleData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordGamePerson
{
	private final long id;
	private final DiscordGame game;

	private long privateChannelId = 0;
	private Attributes tempAttributes = null;
	private boolean muted = false;
	private String realName = "";

	private Role role;
	private RoleData roleData;

	// joinedServer will reamin true upon disconnection
	// disconnected will also remain true even after joining back
	private boolean joinedServer = false;
	private boolean disconnected = false;
	private boolean alive = true;
	private String causeOfDeath = String.format("<@%d> is still alive.", getID());
	private TownEvent event;

	private DiscordRoles discordRoles;
	// Create a DiscordChannels visibleChannels;

	/**
	* A public constructor used to create person.
	*
	* @param game The Discord Game which the person is in.
	* @param id The Discord ID which represents the actual discord user.
	*/
	public DiscordGamePerson(@Nonnull DiscordGame game, long id, @Nonnull Role role)
	{
		if (game == null) throw new NullPointerException("DiscordGame game cannot be null");
		this.game = game;
		this.id = id;
		this.discordRoles = new DiscordRoles(game);
		setRole(role);
	}

	/**
	 * Can the person sync with Discord? In other words, is the person in the game server?
	 * @return Whether the person is in the game server.
	 */
	public boolean canSync()
	{
		return hasJoined() && !isDisconnected();
	}

	public void syncMute()
	{
		if (!canSync()) throw new IllegalStateException("User is not in server, cannot sync");
		if (game != null)
		{
			Member member = getMember();
			if (member.getVoiceState().inVoiceChannel())
				game.getGuild().mute(member, muted).queue(e -> {}, e -> {});
		}
	}

	public void syncPrivateChannel()
	{
		if (!canSync()) throw new IllegalStateException("User is not in server, cannot sync");
		TextChannel textChannel = getGame().getTextChannel(getPrivateChannelID());
		textChannel.putPermissionOverride(getMember()).setAllow(QP.readPermissions() | QP.writePermissions()).queue();
	}

	public void syncRoles()
	{
		if (!canSync()) throw new IllegalStateException("User is not in server, cannot sync");
		net.dv8tion.jda.api.entities.Role[] roles = new net.dv8tion.jda.api.entities.Role[discordRoles.size()];
		for (int x = 0; x < discordRoles.size(); ++x)
			roles[x] = discordRoles.get(x).getRole();
		getGame().getGuild().modifyMemberRoles(getMember(), roles).queue();
	}

	public void addDiscordRole(DiscordRole role)
	{
		discordRoles.add(role);
	}

	public Member getMember()
	{
		if (!canSync()) throw new IllegalStateException("User is not in server, cannot sync");
		if (!getGame().wasServerCreated()) throw new IllegalStateException("Server not created yet");
		return getGame().getGuild().getMemberById(getID());
	}

	/**
	 * Check if the person is considered muted.
	 * @return true if the person is muted, otherwise false.
	 */
	public boolean isMuted()
	{
		return muted;
	}

	/**
	 * Mute the person.
	 * @param val If val is true, then the person will be muted. Otherwise person will be unmuted.
	 */
	public void mute(boolean val)
	{
		muted = val;
	}

	/**
	 * Get the person's Discord ID.
	 * @return the Discord ID.
	 */
	public long getID()
	{
		return id;
	}

	/**
	 * Get the person's DiscordGame.
	 * @return The Discord Game the person resides in.
	 */
	@Nonnull
	public DiscordGame getGame()
	{
		return game;
	}

	/**
	 * Get the Discord account's name. This is not the nickname of the person.
	 * @return The account name.
	 */
	@Nonnull
	public String getRealName()
	{
		if (realName.isEmpty()) realName = game.getUser(this).getName();
		return realName;
	}

	/**
	 * Get the person's role. Ex. Civillian, Serial Killer...
	 * @return The person's role.
	 */
	@Nonnull
	public Role getRole()
	{
		return role;
	}

	/**
	 * Set the person's role. Automatically constructs initial role data.
	 * @param role The role which the person will assume.
	 */
	public void setRole(@Nonnull Role role)
	{
		this.role = role;
		if (role == null) throw new IllegalArgumentException("Role was null");
		this.roleData = role.getInitialRoleData();
		if (roleData == null) throw new IllegalArgumentException("No roledata found from " + role.getName());
	}

	/**
	 * Returns the role data associated with person.
	 * @return The Role data associated with person.
	 */
	@Nonnull
	public RoleData getRoleData()
	{
		return roleData;
	}

	/**
	 * Is the person alive?
	 * @return True if the person is alive.
	 */
	public boolean isAlive()
	{
		return alive;
	}

	/**
	 * Send a message to the person's private channel.
	 * @param msg The message content in String.
	 */
	public void sendMessage(@Nonnull String msg)
	{
		if (msg == null) throw new NullPointerException("Message cannot be null");
		if (privateChannelId != 0)
			game.sendMessageToTextChannel(privateChannelId, msg).queue();
		else
			System.out.println("Could not send to private channel");
	}

	/**
	 * Send a message to the person's private channel.
	 * @param msg The message content with MessageEmbed type.
	 */
	public void sendMessage(@Nonnull MessageEmbed msg)
	{
		if (msg == null) throw new NullPointerException("Message cannot be null");
		if (privateChannelId != 0)
			game.sendMessageToTextChannel(privateChannelId, msg).queue();
		else
			System.out.println("Could not send to private channel");
	}

	/**
	 * Sends a direct message to a discord user
	 * @param msg The message sent
	 */
	public void sendDM(String msg)
	{
		getGame().getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}

	/**
	 * Sends a direct message to a discord user
	 * @param msg The message sent
	 */
	public void sendDM(MessageEmbed msg)
	{
		getGame().getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}

	/**
	 * Assign a private channel to the user.
	 * @param channelID The private channel ID to be assigned.
	 */
	public void setPrivateChannel(long channelID)
	{
		privateChannelId = channelID;
	}

	/**
	 * Get the private channel ID.
	 * @return Returns the private channel ID. 0 if not assigned.
	 */
	public long getPrivateChannelID()
	{
		return privateChannelId;
	}

	/**
	 * Get the private channel object.
	 * @return The private chanenl object as a TextChannel.
	 */
	@Nullable
	public TextChannel getPrivateChannel()
	{
		return getGame().getTextChannel(getPrivateChannelID());
	}

	/**
	 * Kill the person.
	 * @param reason The reason why the person died.
	 */
	public void die(@NotNull String reason)
	{
		if (getGame().getCurrentPhase() instanceof Night)
			die(reason, true);
		else
			die(reason, false);
	}

	/**
	 * Kill the person. Automatically syncs.
	 * @param reason The reason why the person died.
	 * @param saveForMorning If true, the person's death will be revealed in the morning.
	 */
	public void die(@NotNull String reason, boolean saveForMorning)
	{
		mute(true);
		if (!alive) return;
		if (!reason.isEmpty()) causeOfDeath = reason;
		if (!isDisconnected())
			addDiscordRole(getGame().getRole("dead"));
		if (saveForMorning)
			getGame().saveForMorning(this);
		alive = false;
		syncRoles();
		syncMute();
	}

	/**
	 * Get the cause of death, assigned by the method {@code die()}.
	 * @return The cause of death.
	 */
	@Nullable
	public String getCauseOfDeath()
	{
		return causeOfDeath;
	}

	/**
	 * Responds to game events.
	 * @param event The event that took place.
	 */
	public void onEvent(@Nonnull TownEvent event)
	{
		if (event == null) throw new NullPointerException("Event cannot be null");
		event.standard(this);
	}

	/**
	 * Get the person's current attributes. If a temporary attribute is set, then that's given instead.
	 * @return The player's current attributes.
	 */
	public Attributes getAttributes()
	{
		if (tempAttributes == null) return getRole().getAttributes();
		AttributeValue attack = tempAttributes.attack == AttributeValue.DEFAULT ? getRole().getAttributes().attack : tempAttributes.attack;
		AttributeValue defense = tempAttributes.defense == AttributeValue.DEFAULT ? getRole().getAttributes().defense : tempAttributes.defense;
		return new Attributes(attack, defense);
	}

	/**
	 * Set the person's temporary attributes. Unless null, this overrides the default attack.
	 * @param atr The player's temporary attributes.
	 */
	public void setTemporaryAttributes(@Nullable Attributes atr)
	{
		tempAttributes = atr;
	}

	public void clearTemporaryAttributes()
	{
		setTemporaryAttributes(null);
	}

	/**
	 * Has the user left the game server? Changes to false if the user joined back.
	 * <br><br>
	 * It's called "isDisconnected" rather than "hasDisconnected" because the value can change if the person joins back, unlike hasJoined.
	 * @return True if the user left the game server.
	 */
	public boolean isDisconnected()
	{
		return disconnected;
	}

	/**
	 * This method should be called when the person leaves the game server.
	 * It subsequently kills the person via suicide.
	 */
	public void disconnect()
	{
		disconnected = true;
		die(String.format("<@%d> committed suicide.", getID()), true);
	}

	/**
	 * This method should be called when the person either joins or rejoins the game server.
	 */
	public void join()
	{
		joinedServer = true;
		disconnected = false;
	}

	/**
	 * Has the person joined the server? Will reamin true if the person disconnects the server.
	 * @return true if the user joined the game server at least once
	 */
	public boolean hasJoined()
	{
		return joinedServer;
	}

	/**
	 * Set the person's event.
	 * @param e The town event to be used
	 */
	public void setTownEvent(@Nullable TownEvent e)
	{
		event = e;
	}

	/**
	 * Get the person's event.
	 * @return The person's event.
	 */
	public TownEvent getTownEvent()
	{
		return event;
	}

	/**
	 * Clears the person's town event.
	 */
	public void clearTownEvent()
	{
		setTownEvent(null);
	}
}
