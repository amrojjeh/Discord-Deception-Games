package io.github.dinglydo.town.games.parser;

import java.util.ArrayList;

import io.github.dinglydo.town.games.GameMode;
import io.github.dinglydo.town.mafia.roles.Civilian;
import io.github.dinglydo.town.mafia.roles.Doctor;
import io.github.dinglydo.town.mafia.roles.Lookout;
import io.github.dinglydo.town.mafia.roles.Medium;
import io.github.dinglydo.town.mafia.roles.SerialKiller;
import io.github.dinglydo.town.roles.Role;
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
			Rule rule = parseRule(lines[currentLine++]);
			if (rule.totalPlayers != calculateImplicitTotalPlayers(rule.roles))
				throw new IllegalArgumentException("The total number of players does not equal the max number of players pass in the rule: **" + str + "**");
			game.addRule(rule);
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

	public static Rule parseRule(String str)
	{
		Rule rule = new Rule(getExplicitTotalPlayers(str));
		String[] tuples = str.replaceFirst(rule.totalPlayers + "", "").split(",");
		if (tuples.length == 0)
			throw new IllegalArgumentException("No roles passed");
		for (String tuple : tuples)
			rule.addRole(getRoleFromTuple(tuple.strip()));

		return rule;
	}

	public static RoleInfo getRoleFromTuple(String tuple)
	{
		String[] splitTuple = tuple.split(" ");
		Integer roleMax = JavaHelper.parseInt(splitTuple[splitTuple.length - 1].replace("+", ""));
		if (roleMax == null)
			throw new IllegalArgumentException("No role max was passed in the tuple: " + tuple);
		splitTuple[splitTuple.length - 1] = "";
		String roleName = String.join(" ", splitTuple);

		Role role = getRoleFromName(roleName.strip().toLowerCase());
		if (role == null)
			throw new IllegalArgumentException("Role name (" + roleName.strip() + ") not found in: **" + tuple + "**");
		return new RoleInfo(role, roleMax, tuple.charAt(tuple.length() - 1) == '+');
	}

	public static Role getRoleFromName(String roleName)
	{
		switch (roleName)
		{
		case "serial killer":
			return new SerialKiller();
		case "doctor":
			return new Doctor();
		case "medium":
			return new Medium();
		case "civilian":
			return new Civilian();
		case "lookout":
			return new Lookout();
		default:
			return null;
		}
	}

	public static int calculateImplicitTotalPlayers(ArrayList<RoleInfo> singleRoles)
	{
		int sum = 0;
		for (RoleInfo sr : singleRoles)
			sum += sr.max;
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
