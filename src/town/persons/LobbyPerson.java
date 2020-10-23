package town.persons;

import net.dv8tion.jda.api.entities.MessageEmbed;
import town.GameParty;

public class LobbyPerson implements Person
{
	GameParty party;
	long id;

	public LobbyPerson(GameParty gp, long id)
	{
		this.party = gp;
		this.id = id;
	}

	@Override
	public long getID()
	{
		return id;
	}

	@Override
	public String getRealName()
	{
		return party.getUser(this).getName();
	}

	@Override
	public void sendMessage(String msg)
	{
		party.getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}

	@Override
	public void sendMessage(MessageEmbed msg)
	{
		party.getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}
}
