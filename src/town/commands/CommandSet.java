package town.commands;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import net.dv8tion.jda.api.entities.Message;
import town.DiscordGame;

public class CommandSet
{
	ArrayList<Command> commands = new ArrayList<>();

	public void addCommand(boolean startsWith, BiConsumer<DiscordGame, Message> c, String... names)
	{
		commands.add(new Command(startsWith, c, names));
	}

	public Command getCommand(String prefix, Message message)
	{
		String lowerCaseMessage = message.getContentRaw().toLowerCase();
		for (Command command : commands)
		{
			if (command.check(lowerCaseMessage, prefix))
				return command;
		}
		return null;
	}

	public static boolean executeCommand(DiscordGame game, CommandSet commandSet, String prefix, Message message)
	{
		Command command = commandSet.getCommand(prefix, message);
		if (command != null)
		{
			command.accept(game, message);
			return true;
		}
		return false;
	}
}
