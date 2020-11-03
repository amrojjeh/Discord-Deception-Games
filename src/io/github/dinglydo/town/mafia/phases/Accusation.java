package io.github.dinglydo.town.mafia.phases;

import java.awt.Color;
import java.util.HashMap;

import io.github.dinglydo.town.discordgame.DiscordGame;
import io.github.dinglydo.town.persons.DiscordGamePerson;
import io.github.dinglydo.town.phases.Phase;
import io.github.dinglydo.town.phases.PhaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

//Accusation is the phase where players can vote to put another player on trial. If a player receives enough
//votes, the defense phase begins for that player. There can be 3 trials in a day. Night phase happens otherwise.
public class Accusation extends Phase
{
	long msgID;
	HashMap<DiscordGamePerson, Integer> numOfVotes = new HashMap<>();
	HashMap<DiscordGamePerson, DiscordGamePerson> voters = new HashMap<>();
	int numVotesNeeded;
	int numTrials;

	public Accusation(DiscordGame game, PhaseManager pm, int numTrials)
	{
		super(game, pm);
		this.numTrials = numTrials;
		numVotesNeeded = getGame().getAlivePlayers().size() / 2 + 1;
	}

	@Override
	public void start()
	{
		getGame().sendMessageToTextChannel("daytime_discussion", "The Accusation phase has started. **There are "
				+ numTrials + " left in the day**. Vote up a person with `!vote [num|@mention]`.")
		.flatMap(msg -> getGame().sendMessageToTextChannel("daytime_discussion", generateList()))
		.queue(message -> {msgID = message.getIdLong(); message.pin().queue();});
		getPhaseManager().setWarningInSeconds(5);
	}

	@Override
	public Phase getNextPhase()
	{
		return new Night(getGame(), getPhaseManager());
	}

	@Override
	public int getDurationInSeconds()
	{
		return 120;
	}

	public void putPlayerOnTrial(DiscordGamePerson p)
	{
		getPhaseManager().end();
		getGame().getGuild().modifyMemberRoles(p.getMember(), getGame().getRole("defendant").getRole())
		.queue();
		getPhaseManager().start(getGame(), new Trial(getGame(), getPhaseManager(), p, numTrials - 1));
	}

	public MessageEmbed generateList()
	{
		String description = "";
		for (DiscordGamePerson p : getGame().getAlivePlayers())
		{
			Integer vote = numOfVotes.get(p);
			if (vote == null) vote = 0;
			description += "(votes " + vote + ") ";
			description += ". <@" + p.getID() + ">\n";
		}
		return new EmbedBuilder().setTitle("Players Alive").setColor(Color.GREEN).setDescription(description).build();
	}

	public void updateMessage()
	{
		getGame().getMessage("daytime_discussion", msgID)
		.flatMap(msg -> msg.editMessage(generateList()))
		.queue();
	}

	public String vote(DiscordGamePerson accuser, DiscordGamePerson accused)
	{
		DiscordGamePerson previousAccused = voters.get(accuser);
		String message = "";

		if (!accused.isAlive())
			return String.format("Can't accuse a dead person <@%d>", accuser.getID());

		if (!accuser.isAlive())
			return String.format("A dead person can't vote <@%d>", accuser.getID());

		if (accused == accuser)
			return String.format("Can't vote against yourself <@%d>", accuser.getID());

		if (previousAccused != null && previousAccused != accused)
			message = String.format("<@%d> (%d) lost a vote from <@%d>\n", previousAccused.getID(), numOfVotes.get(previousAccused) - 1, accuser.getID());

		cancelVote(accuser);

		voters.put(accuser, accused);
		numOfVotes.compute(accused, (k, v) -> (v == null) ? 1 : v + 1);
		updateMessage();
		//ADDITION: Puts a player on trial if their number of votes exceed the threshold
		if(numOfVotes.get(accused) >= numVotesNeeded) {
			putPlayerOnTrial(accused);
			return "**The Town has decided to put <@" + accused.getID() + "> on trial!**";
		}
		return message + String.format("<@%d> (%d) was accused by <@%d>", accused.getID(), numOfVotes.get(accused), accuser.getID());
	}

	public String cancelVote(DiscordGamePerson accuser)
	{
		DiscordGamePerson previousAccused = voters.get(accuser);
		if (previousAccused == null)
			return "No vote to cancel";

		numOfVotes.put(previousAccused, numOfVotes.get(previousAccused) - 1);
		voters.put(accuser, null);
		updateMessage();
		return "Vote cancelled";
	}
}
