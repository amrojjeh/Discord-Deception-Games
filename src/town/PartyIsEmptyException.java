package town;

public final class PartyIsEmptyException extends PartyException
{
	private static final long serialVersionUID = -3525729221287962295L;

	public PartyIsEmptyException(GameParty party)
	{
		super(party, "Party is empty. Cannot remove anymore players");
	}

	public PartyIsEmptyException(GameParty party, String message)
	{
		super(party, message);
	}

}
