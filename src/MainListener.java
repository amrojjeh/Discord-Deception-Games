package town;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.GenericEvent;


public class MainListener extends ListenerAdapter
{
	public static void main(String[] args)
	{
		try
		{
			System.out.println(loadToken());
			// JDA jda = new JDABuilder();
		}
		catch(Exception e)
		{
			System.out.println(e);
			return;
		}

	}

	public static String loadToken()
		throws FileNotFoundException
	{
		File file = new File("token.txt");
		if (!file.exists()) throw new FileNotFoundException("token.txt was not found");
		Scanner scanner = new Scanner(file);
		if (!scanner.hasNextLine()) throw new java.util.NoSuchElementException("Empty file, please insert the token");
		String token = scanner.nextLine();
		scanner.close();
		return token;
	}
}