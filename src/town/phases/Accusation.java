package town.phases;

import java.awt.Color;
import java.util.HashMap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import town.persons.Person;

//Accusation is the phase where players can vote to put another player on trial. If a player receives enough
//votes, the defense phase begins for that player. There can be 3 trials in a day. Night phase happens otherwise.
public class Accusation extends Phase
{
	long msgID;
	HashMap<Person, Integer> numOfVotes = new HashMap<>();
	HashMap<Person, Person> voters = new HashMap<>();

	public Accusation(PhaseManager pm)
	{
		super(pm);
	}

	//begins Accusation. Players can now use the command to accuse other players. One accusation at a time!!
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("system", "Accusation started").queue();
		sendInitialMessage();
	}

	//ends the Accusation phase. HOWEVER, the phase may be resumed later, depending on if a trial has begun.
	@Override
	public void end()
	{
	}

	//gets the next phase in line: could be Defense (if a player has enough votes) or Night
	@Override
	public Phase getNextPhase(PhaseManager pm)
	{
		return new Night(pm);
	}

	//Duration: 30 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 3600;
	}

	private MessageEmbed generateList()
	{
		String description = "";
		for (Person p : getGame().getAlivePlayers())
		{
			Integer vote = numOfVotes.get(p);
			if (vote == null) vote = 0;
			if (vote < 10)
				description += vote;
			else
				description += vote + " ";
			description += ":  " + p.getNum() + ". " + p.getNickName() + "\n";
		}

		return new EmbedBuilder().setTitle("Players Alive").setColor(Color.YELLOW).setDescription(description).build();
	}

	public void sendInitialMessage()
	{
		getGame().sendMessageToTextChannel("system", generateList()).queue(message -> msgID = message.getIdLong());
	}

	public void updateMessage()
	{
		getGame().getMessage("system", msgID, (msg) -> updateMessage(msg));
	}

	public void updateMessage(Message msg)
	{
		msg.editMessage(generateList()).queue();
	}

	public String vote(Person accuser, Person accused)
	{
		// TODO: Return another message if voting for the same person
		// TODO: Make a command for dispalying current votes
		// TODO: Merge system and daytime_discussion

		Person previousAccused = voters.get(accuser);
		String message = "";
		if (previousAccused != null)
		{
			message = String.format("<@%d> (%d) lost a vote from <@%d>\n", accused.getID(), numOfVotes.get(previousAccused), accuser.getID());
			numOfVotes.computeIfPresent(previousAccused, (k, v) -> (v == 0) ? 0 : v - 1);
		}

		voters.put(accuser, accused);
		numOfVotes.compute(accused, (k, v) -> (v == null) ? 1 : v + 1);
		updateMessage();
		return message + String.format("<@%d> (%d) was accused by <@%d>", accused.getID(), numOfVotes.get(accused), accuser.getID());
	}
}
