package io.github.dinglydo.town;

import net.dv8tion.jda.api.entities.MessageChannel;

public class DiscordException extends Exception
{
	private static final long serialVersionUID = -4287799848584466168L;

	public DiscordException()
	{
		super();
	}

	public DiscordException(String msg)
	{
		super(msg);
	}

	public void panicInDiscord(MessageChannel channel)
	{
		channel.sendMessage("EXCEPTION!! Report this to the developers!\n" + toString()).queue();
	}

}
