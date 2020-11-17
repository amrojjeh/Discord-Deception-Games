package io.github.dinglydo.town.commands;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Message;

/**
 * Represents a single discord command, such start {@code startparty}.
 *
 * @author Amr Ojjeh
 *
 * @param <T> The object that the command deals with.
 */
public class Command<T>
{
	private String[] commands;
	private boolean startsWith;
	private BiConsumer<T, Message> consumer;

	/**
	 * Constructs the command object.
	 * @param startsWith If the message is to be triggered if it starts with the command, then it should be true.
	 * Otherwise, if the message should match the command exactly, it should be false.
	 * @param consumer The function that represents the command.
	 * @param commands The string aliases that would trigger the function.
	 */
	public Command(boolean startsWith, @Nonnull BiConsumer<T, Message> consumer, @Nonnull String...commands)
	{
		if (consumer == null || commands == null)
			throw new IllegalArgumentException("Cannot have either consumer or commands as null");
		this.startsWith = startsWith;
		this.consumer = consumer;
		this.commands = commands;
	}

	/**
	 * Get the aliases that can trigger the command.
	 * @return The list of aliases.
	 */
	@Nonnull
	public String[] getNames()
	{
		return commands;
	}

	/**
	 * Whether the command needs to start with the alias rather than match it.
	 * @return True if the command is triggered if the message starts with the command alias. Otherwise, false.
	 */
	public boolean startsWith()
	{
		return startsWith;
	}

	/**
	 * Trigger the command.
	 * @param game The object which the command revolves around.
	 * @param message The message which triggered the command.
	 */
	public void accept(T game, @Nonnull Message message)
	{
		if (message == null)
			throw new IllegalArgumentException("Mesasge cannot be null");
		consumer.accept(game, message);
	}

	// Assumes msg is lowercase
	/**
	 * Checks if the message can trigger the command.
	 * @param msg <b>Function assumes msg is lowercase.</b>
	 * @param prefix The prefix assumed before the command alias.
	 * @return True if the message can trigger the command. Otherwise, false.
	 */
	public boolean check(@Nonnull String msg, @Nonnull String prefix)
	{
		if (msg == null || prefix == null)
			throw new IllegalArgumentException("Cannot have either msg or prefix be null");
		msg = msg.toLowerCase();
		for (String command : commands)
			if (startsWith && msg.startsWith(prefix + command)) return true;
			else if (msg.equals(prefix + command)) return true;
		return false;
	}
}
