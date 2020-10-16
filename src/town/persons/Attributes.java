package town.persons;

public class Attributes
{
	final AttributeValue attack;
	final AttributeValue defense;

	/**
	 * Construct a new Attributes class. Used by roles to denote defense and attack.
	 * @param defense Set the defense
	 * @param attack Set the attack
	 */
	public Attributes(AttributeValue defense, AttributeValue attack)
	{
		this.defense = defense;
		this.attack = attack;
	}
}
