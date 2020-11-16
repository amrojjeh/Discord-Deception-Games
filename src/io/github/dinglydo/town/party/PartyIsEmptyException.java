package io.github.dinglydo.town.party;

public final class PartyIsEmptyException extends PartyException
{
	private static final long serialVersionUID = -3525729221287962295L;

	public PartyIsEmptyException(Party party)
	{
		super(party, "Party is empty. Cannot remove anymore players");
	}

	public PartyIsEmptyException(Party party, String message)
	{
		super(party, message);
	}

}
