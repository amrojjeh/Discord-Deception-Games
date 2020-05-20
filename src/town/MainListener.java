package town;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;

import town.events.TownEvent;
import town.events.onDeathTownEvent;
import town.events.onMurderTownEvent;
import town.persons.DummyPerson; // Should be removed once done testing
import town.persons.Person;


public class MainListener extends ListenerAdapter
{
	ArrayList<Person> persons; // TODO: Sort based on priority also (SortedSet?)
	LinkedList<TownEvent> events; // TODO: PriorityQueue<E>
	public MainListener() 
	{
		persons = new ArrayList<>();
		events = new LinkedList<>();
	}
	
	public static void main(String[] args)
		throws InterruptedException
	{
		String token;
		JDA jda;
		try
		{
			token = loadToken();
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("token.txt did not exist.");
			return;
		}
		
		if (token == "")
			System.out.println("No token is found in token.txt");
		
		try 
		{
			jda = new JDABuilder(token).addEventListeners(new MainListener()).build();
		}
		catch (LoginException e) 
		{
			System.out.println("Couldn't login: " + e);
			return;
		}
		jda.awaitReady();
	}

	
	@Override
	public void onReady(ReadyEvent e)
	{
		System.out.println("Bot is ready to be used");
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{
		if(e.getMessage().getContentRaw().equals("!phaseStart"))
		{
			e.getChannel().sendMessage("Starting phase cycle").queue();
			PhaseManager m = new PhaseManager();
			m.start();
		}
		
		if (e.getMessage().getContentRaw().startsWith("!start")) // TODO: Make event
			for (Member m : e.getMessage().getMentionedMembers())
				persons.add(new DummyPerson(e.getJDA(), m.getId()));
		else if (e.getMessage().getContentRaw().startsWith("!kill"))
		{
			Person deadPerson = getPerson(e.getMessage().getMentionedMembers().get(0));
			Person murderer = getPerson(e.getMember());
			if (deadPerson != null && murderer != null)
			{
				events.add(new onDeathTownEvent(e.getJDA(), deadPerson));
				events.add(new onMurderTownEvent(e.getJDA(), murderer, deadPerson));
			}
			else System.out.println("Didn't get person");
		}
		
		dispatchEvents();
	}
	
	public void dispatchEvents() // TODO: Change to a for loop?
	{
		if (events.size() == 0) return;
		for (Person person : persons) 
		{
			person.onEvent(events.remove());
		}
		dispatchEvents();
	}
	
	public Person getPerson(Member member)
	{
		for (Person person : persons)
			if (person.getID().equals(member.getId()))
				return person;
		return null;
	}
	
	public static String loadToken()
		throws FileNotFoundException
	{
		File file = new File("token.txt");
		Scanner scanner = new Scanner(file);
		if (!scanner.hasNextLine())
		{
			scanner.close();
			return "";
		}
		String token = scanner.nextLine();
		scanner.close();
		return token;
	}
}
