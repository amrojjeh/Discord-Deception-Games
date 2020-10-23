package town;

public class PartyException extends DiscordException
{
	private static final long serialVersionUID = -8535238722781046891L;
	private final GameParty party;

	public PartyException(GameParty party)
	{
		super("An exception with the party has occured");
		this.party = party;
	}

	public PartyException(GameParty party, String message)
	{
		super(message);
		this.party = party;
	}

	public GameParty getGameParty()
	{
		return party;
	}
}
