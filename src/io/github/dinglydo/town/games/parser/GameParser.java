package io.github.dinglydo.town.games.parser;

import java.util.ArrayList;

import io.github.dinglydo.town.games.GameMode;
import io.github.dinglydo.town.mafia.roles.TVMRole;
import io.github.dinglydo.town.persons.assigner.Assigner;
import io.github.dinglydo.town.roles.RoleBuilder;
import io.github.dinglydo.town.util.JavaHelper;

public class GameParser
{
	public static GameMode parseGeneralGame(String str)
	{
		if (str.isBlank())
			throw new IllegalArgumentException("Could not parse string because it was empty.");
		String name = "Custom Game";
		String description = "";

		int currentLine = 0;
		String[] lines = str.split("\n");

		if (isGameName(lines[currentLine]))
		{
			String[] first = lines[currentLine++].split(":", 2);
			name = first[0];
			if (first.length == 2)
				description = first[1];
		}

		GameMode game = new GameMode(name, description, false);
		if (currentLine == lines.length)
			throw new IllegalArgumentException("No valid rules parsed in **" + str + "**");
		while (currentLine != lines.length)
		{
			Assigner rule = parseRule(lines[currentLine++]);
			if (rule.getMinimumPlayers() != calculateImplicitTotalPlayers(rule.getRoles()))
				throw new IllegalArgumentException("The total number of players does not equal the max number of players pass in the rule: **" + str + "**");
			game.addAssigner(rule);
		}
		return game;
	}

	// A game name would be the name that the guild server would use.
	// As long as the name is 2 characters or more, and it doesn't start with a digit, it's a valid name
	public static boolean isGameName(String str)
	{
		if (str.length() < 2) return false;
		if (str.isBlank()) return false;
		if (Character.isDigit(str.charAt(0))) return false;
		return true;
	}

	public static Assigner parseRule(String str)
	{
		// TODO: What if there is only one role?
		Assigner assigner = new Assigner(getExplicitTotalPlayers(str));
		String[] tuples = str.replaceFirst(assigner.getMinimumPlayers() + "", "").split(",");
		if (tuples.length == 0)
			throw new IllegalArgumentException("No roles passed");
		for (String tuple : tuples)
			assigner.addRole(parseRole(tuple.strip()));
		return assigner;
	}

	public static RoleBuilder parseRole(String str)
	{
		String[] splitTuple = str.split(" ");
		Integer roleMax = JavaHelper.parseInt(splitTuple[splitTuple.length - 1].replace("+", ""));
		if (roleMax == null)
			throw new IllegalArgumentException("No role max was passed in the tuple: " + str);
		splitTuple[splitTuple.length - 1] = "";
		String roleName = String.join(" ", splitTuple);

		TVMRole role = getRoleFromName(roleName);
		if (role == null)
			throw new IllegalArgumentException("Role name (" + roleName.strip() + ") not found in: **" + str + "**");
		return new RoleBuilder(role)
				.setDefault(str.charAt(str.length() - 1) == '+')
				.setMinimum(roleMax);
	}

	public static TVMRole getRoleFromName(String roleName)
	{
		return TVMRole.valueOf(roleName.strip().toUpperCase().replace(" ", "_"));
	}

	public static int calculateImplicitTotalPlayers(ArrayList<RoleBuilder> singleRoles)
	{
		int sum = 0;
		for (RoleBuilder sr : singleRoles)
			sum += sr.getMinimum();
		return sum;
	}

	public static int getExplicitTotalPlayers(String str)
	{
		Integer total = JavaHelper.parseInt(str.split(" ", 2)[0]);
		if (total == null)
			throw new IllegalArgumentException("No total number of players passed in: " + str);
		return total;
	}
}
