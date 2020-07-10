package town.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import town.DiscordGame;
import town.mafia.phases.Accusation;
import town.persons.Person;
import town.phases.Phase;

public class GlobalCommands extends CommandSet
{
	public GlobalCommands()
	{
		addCommand(false, GlobalCommands::displayParty, "party");
	}

	public static void displayParty(DiscordGame game, Message message)
	{
		Phase currentPhase = game.getCurrentPhase();
		MessageChannel channelUsed = message.getChannel();
		if (currentPhase instanceof Accusation)
		{
			Accusation acc = (Accusation)currentPhase;
			channelUsed.sendMessage(acc.generateList()).queue();
			return;
		}

		String description = "";
		String format = "%d. <@%d> ";
		for (Person p : game.getPlayersCache())
			description += String.format(format, p.getNum(), p.getID()) + (p.isDisconnected() ? "(d)\n" : "\n");
		MessageEmbed embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("Party members").setDescription(description).build();
		channelUsed.sendMessage(embed).queue();
	}
}
