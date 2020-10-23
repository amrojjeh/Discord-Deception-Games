package town;

import town.persons.LobbyPerson;

public final class PartyIsFullException extends PartyException
{
	private static final long serialVersionUID = -6203364956329106479L;
	private final LobbyPerson personThatFailedToJoin;

	public PartyIsFullException(GameParty gp, LobbyPerson person)
	{
		super(gp, "Party is full. Cannot add anymore players");
		this.personThatFailedToJoin = person;
	}

	public PartyIsFullException(GameParty gp, LobbyPerson person, String message)
	{
		super(gp, message);
		this.personThatFailedToJoin = person;
	}

	public LobbyPerson getPersonThatFailedToJoin()
	{
		return personThatFailedToJoin;
	}
}
