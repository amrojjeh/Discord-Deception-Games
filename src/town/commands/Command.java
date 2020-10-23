package town.commands;

import java.util.function.BiConsumer;

import net.dv8tion.jda.api.entities.Message;

public class Command<T>
{
	private String[] commands;
	private boolean startsWith;
	private BiConsumer<T, Message> consumer;

	public Command(boolean startsWith, BiConsumer<T, Message> consumer, String...commands)
	{
		this.startsWith = startsWith;
		this.consumer = consumer;
		this.commands = commands;
	}

	public String[] getNames()
	{
		return commands;
	}

	public boolean startsWith()
	{
		return startsWith;
	}

	public void accept(T game, Message message)
	{
		consumer.accept(game, message);
	}

	// Assumes msg is lowercase
	public boolean check(String msg, String prefix)
	{
		msg = msg.toLowerCase();
		for (String command : commands)
			if (startsWith && msg.startsWith(prefix + command)) return true;
			else if (msg.equals(prefix + command)) return true;
		return false;
	}
}
