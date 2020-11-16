package io.github.dinglydo.town.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JavaHelper
{
	public static Integer parseInt(String str)
	{
		Integer num;
		try
		{
			num = Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		return num;
	}

	public static String readFile(String fileName) throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File(fileName));
		String fileContents = "";
		while (scanner.hasNextLine())
			fileContents += scanner.nextLine() + "\n";
		scanner.close();
		return fileContents;
	}
}
