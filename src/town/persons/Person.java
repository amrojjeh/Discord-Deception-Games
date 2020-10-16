package town.persons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import town.DiscordGame;
import town.events.TownEvent;
import town.mafia.phases.Night;
import town.roles.Role;
import town.roles.RoleData;

/**
* The person class is the class used to refer to discord members within a game or party.
*
* @author Amr Ojjeh
*/
public class Person
{
	private final long ID;
	private final DiscordGame game;

	private long privateChannelID = 0;
	private Attributes tempAttributes = null;
	private boolean muted = false;

	private Role role;
	private RoleData roleData;

	private boolean disconnected = false;
	private boolean alive = true;
	private String causeOfDeath = String.format("<@%d> is still alive.", getID());
	private TownEvent event;

	/**
	* A public constructor used to create person.
	*
	* @param game The Discord Game which the person is in.
	* @param id The Discord ID which represents the actual discord user.
	*/
	public Person(@Nonnull DiscordGame game, long id)
	{
		if (game == null) throw new NullPointerException("DiscordGame game cannot be null");
		this.game = game;
		ID = id;
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
		if (game != null)
		{
			Member member = game.getMemberFromGame(this);
			if (member.getVoiceState().inVoiceChannel())
				game.getGameGuild().mute(member, muted).queue(e -> {}, e -> {});
		}
	}

	/**
	 * Get the person's Discord ID.
	 * @return the Discord ID.
	 */
	public long getID()
	{
		return ID;
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
		return game.getUser(this).getName();
	}

	/**
	 * Get the person's role. Ex. Civillian, Serial Killer...
	 * @return The person's role.
	 */
	@Nullable
	public Role getRole()
	{
		return role;
	}

	/**
	 * Set the person's role. Automatically constructs initial role data.
	 * @param role The role which the person will assume.
	 */
	public void setRole(Role role)
	{
		this.role = role;
		this.roleData = role.getInitialRoleData();
	}

	/**
	 * Returns the role data associated with person.
	 * @return The Role data associated with person.
	 */
	@Nullable
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
		if (privateChannelID != 0)
			game.sendMessageToTextChannel(privateChannelID, msg).queue();
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
		if (privateChannelID != 0)
			game.sendMessageToTextChannel(privateChannelID, msg).queue();
		else
			System.out.println("Could not send to private channel");
	}

	/**
	 * Assign a private channel to the user.
	 * @param channelID The private channel ID to be assigned.
	 */
	public void setPrivateChannel(long channelID)
	{
		privateChannelID = channelID;
	}

	/**
	 * Get the private channel ID.
	 * @return Returns the private channel ID. Null if not assigned.
	 */
	@Nullable
	public Long getPrivateChannelID()
	{
		return privateChannelID;
	}

	/**
	 * Get the private channel object.
	 * @return The private chanenl object as a TextChannel.
	 */
	@Nullable
	public TextChannel getChannel()
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
	 * Kill the person.
	 * @param reason The reason why the person died.
	 * @param saveForMorning If true, the person's death will be revealed in the morning.
	 */
	public void die(@NotNull String reason, boolean saveForMorning)
	{
		muted = true;
		if (!alive) return;
		if (!reason.isEmpty()) causeOfDeath = reason;
		if (!isDisconnected())
			getGame().modifyMemberRoles(this, "dead", getRole().getName()).queue();
		if (saveForMorning)
			getGame().saveForMorning(this);
		alive = false;
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
		return tempAttributes;
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
	 * Has the user left the game server?
	 * @return True if the user left the game server.
	 */
	public boolean isDisconnected()
	{
		return disconnected;
	}

	/**
	 * This method should be called when the person leaves the game server.
	 */
	public void disconnect()
	{
		disconnected = true;
//		die(String.format("<@%d> (%d) committed suicide.", getID(), getNum()), true);
		die(String.format("<@%d> committed suicide.", getID()), true);
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
