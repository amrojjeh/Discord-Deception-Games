package io.github.dinglydo.town.party;

import io.github.dinglydo.town.DiscordException;

public class PartyException extends DiscordException
{
	private static final long serialVersionUID = -8535238722781046891L;
	private final Party party;

	public PartyException(Party party)
	{
		super("An exception with the party has occured");
		this.party = party;
	}

	public PartyException(Party party, String message)
	{
		super(message);
		this.party = party;
	}

	public Party getGameParty()
	{
		return party;
	}
}
