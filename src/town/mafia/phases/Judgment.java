package town.mafia.phases;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import town.discordgame.DiscordGame;
import town.persons.DiscordGamePerson;
import town.phases.Phase;
import town.phases.PhaseManager;

//Judgment is the moment of fate. All players still alive vote on the life of the defendant.
public class Judgment extends Phase
{
	DiscordGamePerson defendant;
	HashMap<DiscordGamePerson, voteType> votes = new HashMap<>();
	int guilty = 0;
	int innocent = 0;
	int numTrials;

	public Judgment(DiscordGame game, PhaseManager pm, DiscordGamePerson p, int numTrials)
	{
		super(game, pm);
		defendant = p;
		this.numTrials = numTrials;
	}

	//begins the phase. sends out a message, and opens up text channels and voice chat.
	@Override
	public void start()
	{
		loadPlayers();
		getGame().sendMessageToTextChannel("daytime_discussion", "Vote `!guilty` or `!innocent` in #private").queue();
		getPhaseManager().setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		displayVotes();
	}

	//After Judgment, the results are revealed (Verdict)
	@Override
	public Phase getNextPhase()
	{
		if (guilty > innocent)
			return new LastWords(getGame(), getPhaseManager(), defendant);
		else if(numTrials > 0)
		{
			if (!defendant.isDisconnected())
				getGame().getGuild().modifyMemberRoles(defendant.getMember(), getGame().getRole("player").getRole()).queue();
			return new Accusation(getGame(), getPhaseManager(), numTrials);
		}
		else
			return new Night(getGame(), getPhaseManager());
	}

	//Duration: 15-20 seconds
	@Override
	public int getDurationInSeconds()
	{
		return 40;
	}

	public String guilty(DiscordGamePerson person)
	{
		if (person == defendant)
			return defendantVoting();
		if (!person.isAlive())
			return deadVoting();
		voteType previous = votes.put(person, voteType.GUILTY);
		if (previous == voteType.INNOCENT) innocent -= 1;
		guilty += 1;
		return String.format("<@%d> Voted guilty", person.getID());
	}

	public String innocent(DiscordGamePerson person)
	{
		if (person == defendant)
			return defendantVoting();
		if (!person.isAlive())
			return deadVoting();
		voteType previous = votes.put(person, voteType.INNOCENT);
		if (previous == voteType.GUILTY) guilty -= 1;
		innocent += 1;
		return String.format("<@%d> Voted innocent", person.getID());
	}

	public String abstain(DiscordGamePerson person)
	{
		if (person == defendant)
			return defendantVoting();
		if (!person.isAlive())
			return deadVoting();
		voteType previous = votes.put(person, voteType.ABSTAINED);
		if (previous == voteType.GUILTY) guilty -= 1;
		else if (previous == voteType.INNOCENT) innocent -= 1;
		return String.format("<@%d> Abstained", person.getID());
	}

	private String defendantVoting()
	{
		return String.format("<@%d> can't vote guilty or innocent", defendant.getID());
	}

	private String deadVoting()
	{
		return String.format("<@%d> dead can't vote", defendant.getID());
	}

	private void loadPlayers()
	{
		List<DiscordGamePerson> persons = getGame().getAlivePlayers();
		for (DiscordGamePerson p : persons)
			if (p != defendant)
				votes.put(p, voteType.ABSTAINED);
	}

	private void displayVotes()
	{
		StringBuilder builder = new StringBuilder();
		votes.forEach((person, vote) ->
		{
			if (vote == voteType.GUILTY)
				builder.append(String.format("<@%d> voted guilty\n", person.getID()));
			else if (vote == voteType.INNOCENT)
				builder.append(String.format("<@%d> voted innocent\n", person.getID()));
			else
				builder.append(String.format("<@%d> abstained\n", person.getID()));
		});

		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setDescription(builder.toString()).build();
		getGame().sendMessageToTextChannel("daytime_discussion", embed).queue();
	}

	public enum voteType
	{
		GUILTY,
		INNOCENT,
		ABSTAINED;
	}
}