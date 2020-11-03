package town.discordgame;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import town.persons.DiscordGamePerson;

public class DiscordGameListener extends ListenerAdapter
{
	private final DiscordGame game;

	public DiscordGameListener(DiscordGame game)
	{
		this.game = game;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e)
	{
		System.out.println("Joined new guild");
		Guild guild = e.getGuild();
		String roleName = guild.getRoles().get(0).getName();
		long roleNumber = 0;
		try
		{
			roleNumber = Long.parseLong(roleName);
		}
		catch (NumberFormatException exception)
		{
			return;
		}
		if (roleNumber == game.identifier)
		{
			game.serverCreated = true;
			game.sendInviteToPlayers(guild);
			game.gameGuildId = guild.getIdLong();
			guild.getChannels(true).forEach((channel) -> game.assignChannel(channel));
			game.assignRoles(guild);
			guild.getPublicRole().getManager().reset().queue();
			game.sendMessageToTextChannel("daytime_discussion", "Waiting for players...").queue();
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		// TODO: Don't kick if spectators are allowed -- coming soon
		boolean shouldKick = true;
		Member member = event.getMember();
		for (DiscordGamePerson p : game.getPlayersCache())
		{
			if (p.getID() == member.getIdLong())
			{
				p.syncRoles();
				p.syncPrivateChannel();
				shouldKick = false;
				if (game.getPlayersCache().size() == game.getGuild().getMemberCount() - 1) // -1 since Bot counts as a member
					game.startGame();
				break;
			}
		}
		if (shouldKick)
			member.kick("Member was not part of the party").queue();
	}
}
