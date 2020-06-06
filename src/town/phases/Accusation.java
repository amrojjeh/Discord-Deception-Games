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
	int numVotesNeeded;
	PhaseManager pm;

	public Accusation(PhaseManager pm)
	{
		super(pm);
		this.pm = pm;
		//ADDITION: calculates number of votes needed to start a trial
		numVotesNeeded = getGame().getAlivePlayers().size() / 2 + 1;
	}

	//begins Accusation. Players can now use the command to accuse other players. One accusation at a time!!
	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "Accusation started");
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
		return 30;
	}
	
	public void putPlayerOnTrial(Person p) {
		//ADDITION: "Start over" the phase cycle, from a trial phase.
		pm.end();
		pm.startTrial(p);
	}

	public MessageEmbed generateList()
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
			description += ": " + p.getNum() + ". " + p.getNickName() + "\n";
		}
		return new EmbedBuilder().setTitle("Players Alive").setColor(Color.YELLOW).setDescription(description).build();
	}

	public void sendInitialMessage()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", generateList(), message -> msgID = message.getIdLong());
	}

	public void updateMessage()
	{
		getGame().getMessage("daytime_discussion", msgID, (msg) -> updateMessage(msg));
	}

	public void updateMessage(Message msg)
	{
		msg.editMessage(generateList()).queue();
	}

	public String vote(Person accuser, Person accused)
	{
		Person previousAccused = voters.get(accuser);
		String message = "";

		if (!accused.isAlive())
			return String.format("Can't accuse a dead person <@%d>", accuser.getID());

		if (accused == accuser)
			return String.format("Can't vote against yourself <@%d>", accuser.getID());

		if (previousAccused != null && previousAccused != accused)
			message = String.format("<@%d> (%d) lost a vote from <@%d>\n", accused.getID(), numOfVotes.get(previousAccused), accuser.getID());

		cancelVote(accuser);

		voters.put(accuser, accused);
		numOfVotes.compute(accused, (k, v) -> (v == null) ? 1 : v + 1);
		updateMessage();
		//ADDITION: Puts a player on trial if their number of votes exceed the threshold
		if(numOfVotes.get(accused) >= numVotesNeeded) {
			putPlayerOnTrial(accused);
			return "**The Town has decided to put " + accused.getNickName() + "on trial!**";
		}
		return message + String.format("<@%d> (%d) was accused by <@%d>", accused.getID(), numOfVotes.get(accused), accuser.getID());
	}

	public String cancelVote(Person accuser)
	{
		Person previousAccused = voters.get(accuser);
		if (previousAccused == null)
			return "No vote to cancel";

		numOfVotes.put(previousAccused, numOfVotes.get(previousAccused) - 1);
		voters.put(accuser, null);
		updateMessage();
		return "Vote cancelled";
	}
}
