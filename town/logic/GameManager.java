package town.logic;

import java.util.ArrayList;
import town.logic.phases.*;
import town.logic.delegates.ActionOne;
import town.logic.roles.*;

public class GameManager
{
	Phase currentPhase;
	GameInterface gameInterface;
	ArrayList<Player> players;

	// actionQueue will be sorted by a custom comperator from lowest to highest
	ArrayList<PlayerAction> actionQueue;
	boolean running;

	GameManager(GameInterface face, ArrayList<Player> players)
	{
		actionQueue = new ArrayList<>();
		currentPhase = new GameStart();

		gameInterface = face;
		this.players = players;
	
		init();
	}

	public void run()
	{
		while (running)
		{
			Event event = null;
			while (gameInterface.pollEvent(event))
			{
				switch (event.name)
				{
					case "Exit":
						running = false;
						break;
					case "Player Left":
						break;
					case "Player Action":
						// event.target can be null
						addAction(new PlayerAction(event.actor, event.target));
						break;
					default: break;
				}
			}
			if (currentPhase.isOver())
			{
				switch (currentPhase.getName())
				{
					case "Day": break;
					case "Trial": break;
					case "Night":
						actionQueue.sort((one, two) -> one.compareTo(two));
						executeActionQueueBasedOnEvent((action) -> action.actor.getRole().execute(action.actor, action.target));
						break;
					default: break;
				}

				currentPhase = currentPhase.nextPhase();
				currentPhase.start();
			}
		}
	}

	public void init()
	{
		// Assign players factions
		// Assign players roles using the factions
	}

	void addAction(PlayerAction action)
	{
		// In case there's a duplicate
		if (!action.actor.getRole().canExecute(action.actor, action.target)) return;
		actionQueue.remove(action);
		actionQueue.add(action);
	}

	// JUST FOR TESTING
	static void addAction(PlayerAction action, ArrayList<PlayerAction> actionQueue)
	{
		// In case there's a duplicate
		// if (!action.actor.getRole().canExecute(action.actor, action.target)) return;
		actionQueue.remove(action);
		actionQueue.add(action);
	}

	void executeActionQueueBasedOnEvent(ActionOne<PlayerAction> event)
	{
		for (PlayerAction action : actionQueue)
			event.run(action);
	}

	public static void main(String[] args)
	{
		ArrayList<PlayerAction> actions = new ArrayList<>();
		// Test actionQueue
		for (int x = 0; x < 10; ++x)
		{
			Role role = null;
			int r = (int)Math.floor(Math.random() * 8);
			switch (r)
			{
				case 0:
					role = new Bodyguard();
					break;
				case 1:
					role = new Doctor();
					break;
				case 2:
					role = new Investigator();
					break;
				case 3:
					role = new Sheriff();
					break;
				case 4:
					role = new SuicideVigilante();
					break;
				case 5:
					role = new VampireHunter();
					break;
				case 6:
					role = new Veteran();
					break;
				case 7:
					role = new Vigilante();
					break;
				default: role = new Doctor(); break;
			}
			Player p = new Player(x, x + "", role);
			PlayerAction action = new PlayerAction(p, p);
			System.out.println(role);
			addAction(action, actions);
		}
		actions.sort((one, two) -> one.compareTo(two));
		for (PlayerAction action : actions)
			System.out.println("Role: " + action.actor.getRole().getRoleName());
	}
}

class PlayerAction
{
	public final Player actor;
	public final Player target;

	PlayerAction(Player a)
	{
		actor = a;
		target = null;
	}

	PlayerAction(Player a, Player t)
	{
		actor = a;
		target = t;
	}

	public boolean equals(PlayerAction action)
	{
		return actor.equals(action.actor);
	}

	public boolean equals(Object other)
	{
		if (other instanceof PlayerAction) return equals((PlayerAction)other);
		return false;
	}

	public int compareTo(Object other)
	{
		if (other instanceof PlayerAction) return compareTo(other);
		return 0;
	}
	
	public int compareTo(PlayerAction other)
	{
		return actor.getRole().getPriority() - other.actor.getRole().getPriority();
	}
}
