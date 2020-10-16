package town.mafia.persons;

import java.util.List;

import town.DiscordGame;
import town.events.LookoutTownEvent;
import town.mafia.phases.Night;
import town.persons.Person;
import town.roles.GameRole;

public class Lookout extends Person
{
	public Lookout(DiscordGame game, int num, Long id)
	{
		super(game, num, id, GameRole.LOOKOUT);
	}

	@Override
	public String ability(List<Person> references)
	{
		if (!isAlive())
			return "Can't watch people if you're dead.";
		if (references.isEmpty())
			return "There's no person to watch. `!ability 1` to watch the first person shown in `!party`.";
		if (references.size() > 1)
			return "Cannot watch more than one person at once. `!ability 1` to watch the first person show in `!party`.";
		if (!(getGame().getCurrentPhase() instanceof Night))
			return "Lookouts can only watch visitors during the night.";
		if (!references.get(0).isAlive())
			return "Lookouts can't watch dead people.";
		// In the real game, you can't track yourself
		//		if (references.get(0) == this)
		//			return "You can't track yourself.";

		String msg = "";

		if (event != null)
		{
			msg += "You've changed your mind.\n";
			cancel();
		}

		event = new LookoutTownEvent(getGame(), this, references.get(0));
		getGame().addEvent(event);

		return msg + String.format("You will watch <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public List<Person> getPossibleTargets()
	{
		return getGame().getAlivePlayers();
	}

	@Override
	public String getHelp()
	{
		return "LOOKOUT\n" +
				"Lookouts win with other townees. His goal is to eliminate any evil doer.\n" +
				"Ability: Can see who visited a person that night. Ex: `!ability 2` watches person number two. Check a person's number with !party";
	}
}