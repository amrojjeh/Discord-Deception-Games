package town.events;

import net.dv8tion.jda.api.JDA;
import town.persons.Person;

public interface TownEvent
{
	public JDA getJDA();
	public void standard(Person person);
}
