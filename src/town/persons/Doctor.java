package town.persons;

import java.util.List;

import town.DiscordGame;
import town.TownFaction;
import town.TownRole;
import town.events.DoctorTownEvent;
import town.phases.Night;

public class Doctor extends Person {
	int selfHeal = 1;

	public Doctor(DiscordGame game, int num, Long id)
	{
		super(game, num, id, TownRole.DOCTOR);
	}

	public void selfHealed()
	{
		--selfHeal;
	}

	@Override
	public boolean canWin()
	{
		// TODO: We can put commonly used victories in a static class
		return getGame().getPlayers().stream().filter((person) -> person.getType().getFaction() == TownFaction.TOWN
				&& person.alive).count() == getGame().getPlayers().stream().filter((person) -> person.alive).count();
	}

	@Override
	public boolean hasWon()
	{
		return getGame().hasTownFactionWon(getType().getFaction());
	}

	@Override
	public void win()
	{
		getGame().winTownFaction(getType().getFaction());
		getGame().sendMessageToTextChannel("daytime_discussion", "**Town has won!**").queue();
		getGame().endGame();
	}

	@Override
	public String ability(List<Person> references)
	{
		String msg = "";

		if (references.isEmpty())
			return "There's no one to heal. `!ability 1` to protect the first person shown in `!party`.";
		if (references.size() > 1)
			return "Cannot heal more than one person at once. `!ability 1` to watch the first person show in `!party`.";
		if (!(getGame().getCurrentPhase() instanceof Night))
			return "You can only use your ability at night.";
		if (!isAlive())
			return "Doctors can't heal when dead";

		if (references.get(0) == this && selfHeal <= 0)
			return "You already used your self-heal!";
		else if(references.get(0) == this)
		{
			msg += "Remember, you only get one self-heal.";
		}


		if (event != null)
		{
			msg += "You've changed your mind.\n";
			cancel();
		}

		event = new DoctorTownEvent(getGame(), this, references.get(0));
		getGame().addEvent(event);

		return msg + String.format("You will heal <@%d> tonight.", references.get(0).getID());
	}

	@Override
	public List<Person> getPossibleTargets()
	{
		List<Person> targets = getGame().getAlivePlayers();
		if (selfHeal <= 0)
			targets.remove(this);
		return targets;
	}

	@Override
	public String getHelp()
	{
		return "DOCTOR\n" +
				"Doctors can heal a person each night.\n" +
				"Ability: Grants a person high defense. Ex: `!ability 2` heals person number two. Check a person's number with !party";
	}
}
