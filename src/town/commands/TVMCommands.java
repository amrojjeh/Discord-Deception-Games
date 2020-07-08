package town.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import town.DiscordGame;
import town.persons.Person;
import town.phases.Accusation;
import town.phases.Judgment;
import town.phases.Phase;

public class TVMCommands extends CommandSet
{
	public TVMCommands()
	{
		addCommand(true, TVMCommands::activateAbility, "a", "ability");
		addCommand(false, TVMCommands::cancel, "c", "cancel");
		addCommand(false, TVMCommands::roleHelp, "rh", "rolehelp");
		addCommand(true, TVMCommands::vote, "v", "vote");
		addCommand(false, TVMCommands::guilty, "g", "guilty");
		addCommand(false, TVMCommands::innocent, "i", "inno", "innocent");
		addCommand(false, TVMCommands::getTargets, "t", "targets");
	}

	public static void activateAbility(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		user.sendMessage(user.ability(getPersonsFromMessage(game, message)));
	}

	public static void cancel(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		Phase currentPhase = game.getCurrentPhase();
		if (currentPhase instanceof Accusation)
		{
			Accusation acc = (Accusation)currentPhase;
			message.getChannel().sendMessage(acc.cancelVote(user)).queue();
			return;
		}

		if (currentPhase instanceof Judgment)
		{
			if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

			Judgment j = (Judgment)currentPhase;
			message.getChannel().sendMessage(j.abstain(user)).queue();
			return;
		}

		message.getChannel().sendMessage(user.cancel()).queue();
	}

	public static void roleHelp(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		user.sendMessage(user.getHelp());
	}

	public static void vote(DiscordGame game, Message message)
	{
		Phase phase = game.getCurrentPhase();
		if (!(phase instanceof Accusation))
		{
			message.getChannel().sendMessage("You can only vote for trial once the accusation phase starts!").queue();
			return;
		}

		List<Person> referenced = getPersonsFromMessage(game, message);
		if (referenced == null)
		{
			System.out.println("Thing was null mate");
			return;
		}

		if (referenced.isEmpty())
		{
			message.getChannel().sendMessage("You have to vote one person! Ex: `" + game.config.getPrefix() + "vote 2`").queue();
			return;
		}

		if (referenced.size() > 1)
		{
			message.getChannel().sendMessage("You can only vote one person! Ex: `" + game.config.getPrefix() + "vote 2`").queue();
			return;
		}

		Person accuser = game.getPerson(message.getMember());;
		Person accused = referenced.get(0);

		Accusation acc = (Accusation)phase;
		game.sendMessageToTextChannel("daytime_discussion", acc.vote(accuser, accused)).queue();
	}

	public static void guilty(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		Phase currentPhase = game.getCurrentPhase();

		if (!(currentPhase instanceof Judgment))
		{
			String msg = String.format("<@%d> can only vote guilty once someone's been accused.", user.getID());
			message.getChannel().sendMessage(msg).queue();
			return;
		}

		if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

		Judgment j = (Judgment)currentPhase;
		message.getChannel().sendMessage(j.guilty(user)).queue();
	}

	public static void innocent(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		Phase currentPhase = game.getCurrentPhase();

		if (!(currentPhase instanceof Judgment))
		{
			String msg = String.format("<@%d> can only vote innocent once someone's been accused.", user.getID());
			message.getChannel().sendMessage(msg).queue();
			return;
		}

		if (!user.isAlive()) message.getChannel().sendMessage(String.format("Can't vote if you're dead <@%d>.", user.getID())).queue();

		Judgment j = (Judgment)currentPhase;
		message.getChannel().sendMessage(j.innocent(user)).queue();
	}

	public static void getTargets(DiscordGame game, Message message)
	{
		Person user = game.getPerson(message.getMember());
		List<Person> targets = user.getPossibleTargets();

		if (targets == null || targets.isEmpty())
		{
			user.sendMessage("No possible targets");
			return;
		}

		String description = "";
		String format = "%d. <@%d> ";
		for (Person p : targets)
			description += String.format(format, p.getNum(), p.getID()) + (p.isDisconnected() ? "(d)\n" : "\n");
		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("Possible targets").setDescription(description).build();
		user.sendMessage(embed);
	}

	private static List<Person> getPersonsFromMessage(DiscordGame game, Message message)
	{
		List<Person> references = getPersonsFromMessageUsingNumReferences(game, message);
		if (references != null)
			return references;

		return getPersonsFromMessageUsingMentions(game, message);
	}

	private static List<Person> getPersonsFromMessageUsingMentions(DiscordGame game, Message message)
	{
		List<Member> members = message.getMentionedMembers();
		ArrayList<Person> mentioned = new ArrayList<>();

		if (members.size() == 0) return mentioned;

		for (Member m : members)
		{
			Person p = game.getPerson(m);
			if (p == null)
			{
				message.getChannel().sendMessage(String.format("Person <@%d> isn't a player", m.getIdLong())).queue();
				return null;
			}

			mentioned.add(p);
		}
		return mentioned;
	}

	private static List<Person> getPersonsFromMessageUsingNumReferences(DiscordGame game, Message message)
	{
		ArrayList<Person> references = new ArrayList<>();
		String[] words = message.getContentStripped().split(" ");
		if (words.length == 1) return references;
		for (int x = 1; x < words.length; ++x)
		{
			// Check if parsable
			int personNum = 0; // 0 can't exist as a ref
			try
			{
				personNum = Integer.parseInt(words[x]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}

			Person reference = game.getPerson(personNum);
			if (reference == null)
			{
				message.getChannel().sendMessage(String.format("Person with number %d doesn't exist", personNum)).queue();
				return null;
			}
			references.add(reference);
		}

		return references;
	}
}
