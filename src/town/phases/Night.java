package town.phases;

import java.util.LinkedList;

import town.TownRole;
import town.persons.Person;

public class Night extends Phase
{
	LinkedList<Person> deaths = new LinkedList<>();


	public Night(PhaseManager pm)
	{
		super(pm);
	}

	@Override
	public void start()
	{
		getGame().setChannelVisibility("daytime_discussion", true, false);
		getGame().setChannelVisibility("Daytime", false, false);
		//for each medium, give access to dead chat
		for(Person p : getGame().findAllWithRole(TownRole.MEDIUM)){
			getGame().setChannelVisibility(p, "the_afterlife", true, true);
		}
		getGame().discconectEveryoneFromVC("Daytime");
		getGame().sendMessageToTextChannel("daytime_discussion", "The night has started");
		phaseManager.setWarningInSeconds(5);
	}

	@Override
	public void end()
	{
		getGame().dispatchEvents();
	}

	@Override
	public Phase getNextPhase()
	{
		return new Morning(phaseManager, deaths);
	}

	@Override
	public int getDurationInSeconds()
	{
		return 15;
	}

	public void addDeath(Person person)
	{
		deaths.add(person);
	}
}
