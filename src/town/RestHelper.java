package town;

import net.dv8tion.jda.api.requests.RestAction;

public class RestHelper
{
	public static void queueAll(RestAction<?>... actions)
	{
		for (RestAction<?> action : actions)
			if (action != null)
				action.queue();
	}
}
