package io.github.dinglydo.town.commands;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import net.dv8tion.jda.api.entities.Message;

/**
 * Represents a command set.
 * @author Amr Ojjeh
 *
 * @param <T> The type of object that the commands revolve around.
 */
public class CommandSet<T>
{
	private ArrayList<Command<T>> commands = new ArrayList<>();

	/**
	 * Adds a new command to the set.
	 * @param startsWith Whether the command should start with the alias or match it.
	 * @param c The function that represents the command action.
	 * @param names The name aliases that can trigger the command.
	 */
	public void addCommand(boolean startsWith, BiConsumer<T, Message> c, String... names)
	{
		commands.add(new Command<T>(startsWith, c, names));
	}

	/**
	 * Get the command triggered by message <b>without executing it.</b> Used by {@code executeCommand}.
	 * @param prefix The prefix which the message assumes prior to the command alias/name.
	 * @param message The message which triggered the command.
	 * @return The command that was triggered by the message.
	 */
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

	/**
	 * Trigger the command by message. If no command was triggered, it returns false. Otherwise, true.
	 * @param obj The object which the command revolves around.
	 * @param prefix The prefix assumed by the mesasge prior to the command alias/name.
	 * @param message The message to trigger the command.
	 * @return True if a command was executed as a result of message.
	 */
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
