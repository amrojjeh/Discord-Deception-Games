package town.persons;

import javax.annotation.Nonnull;

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
}
