package town.commands;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import net.dv8tion.jda.api.entities.Message;

public class CommandSet<T>
{
	ArrayList<Command<T>> commands = new ArrayList<>();

	public void addCommand(boolean startsWith, BiConsumer<T, Message> c, String... names)
	{
		commands.add(new Command<T>(startsWith, c, names));
	}

	public Command<T> getCommand(String prefix, Message message)
	{
		String lowerCaseMessage = message.getContentRaw().toLowerCase();
		for (Command<T> command : commands)
		{
			if (command.check(lowerCaseMessage, prefix))
				return command;
		}
		return null;
	}

	public boolean executeCommand(T obj, String prefix, Message message)
	{
		Command<T> command = getCommand(prefix, message);
		if (command != null)
		{
			command.accept(obj, message);
			return true;
		}
		return false;
	}
}
