package io.github.dinglydo.town.persons;

import io.github.dinglydo.town.party.Party;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LobbyPerson
{
	Party party;
	long id;

	public LobbyPerson(Party gp, long id)
	{
		this.party = gp;
		this.id = id;
	}

	public long getID()
	{
		return id;
	}

	public String getRealName()
	{
		return party.getUser(this).getName();
	}

	public void sendDM(String msg)
	{
		party.getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}

	public void sendDM(MessageEmbed msg)
	{
		party.getUser(this).openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
	}
}
