package town.persons;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.MessageEmbed;

/**
* The person class is the class used to refer to discord members within a game or party.
*
* @author Amr Ojjeh
*/
public interface Person
{
	/**
	 * Get the person's Discord ID.
	 * @return the Discord ID.
	 */
	public long getID();


	/**
	 * Get the Discord account's name. This is not the nickname of the person.
	 * @return The account name.
	 */
	@Nonnull
	public String getRealName();


	/**
	 * Send a message to the person's private channel.
	 * @param msg The message content in String.
	 */
	public void sendMessage(@Nonnull String msg);

	/**
	 * Send a message to the person's private channel.
	 * @param msg The message content with MessageEmbed type.
	 */
	public void sendMessage(@Nonnull MessageEmbed msg);
}
