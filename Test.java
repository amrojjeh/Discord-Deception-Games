import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import java.util.Timer;
import java.util.TimerTask;


import javax.security.auth.login.LoginException;

public class Test implements EventListener
{
	boolean first = true;
	public static void main(String args[])
		throws LoginException, InterruptedException
	{
		JDA jda = new JDABuilder("MzY1MjI0NjM2Mzg1ODUzNDUx.XsHJSw.cIfT2Ecn7VxkZ9yqqlQn0jWlPGQ").addEventListeners(new Test()).build();		
	}

	@Override
	public void onEvent(GenericEvent event)
	{
		if (event instanceof MessageReceivedEvent)
		{
			MessageReceivedEvent msgEvent = (MessageReceivedEvent)event;
			msgEvent.getTextChannel().sendMessage("Yoyo").queue();
		}
	}
}

class Day extends TimerTask
{
	private MessageChannel channel;
	Day(MessageChannel channel)
	{
		this.channel = channel;
	}

	@Override
	public void run()
	{
		channel.sendMessage("Phase ended").queue();
	}
}
