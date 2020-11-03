package town.discordgame;

import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import town.persons.DiscordGamePerson;

public class DiscordRole
{
	final long roleId;
	final DiscordGame game;
	final String name;

	public DiscordRole(DiscordGame game, String name, long roleId)
	{
		this.roleId = roleId;
		this.game = game;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public DiscordGame getGame()
	{
		return game;
	}

	public long getId()
	{
		return roleId;
	}

	public Role getRole()
	{
		return getGame().getGuild().getRoleById(getId());
	}

	public RoleManager muteAllInRole(boolean shouldMute)
	{
		Role role = getRole();
		List<Member> members = getGame().getGuild().getMembersWithRoles(role);
		for (Member member : members)
		{
			DiscordGamePerson p = getGame().getPerson(member);
			p.mute(shouldMute);
			p.syncMute();
		}

		if (shouldMute)
			return role.getManager().revokePermissions(Permission.VOICE_SPEAK);
		return role.getManager().setPermissions(Permission.VOICE_SPEAK);
	}

	public RoleManager muteAllInRoleExcept(DiscordGamePerson p)
	{
		Role role = getRole();
		List<Member> members = getGame().getGuild().getMembersWithRoles(role);
		for (Member member : members)
		{
			DiscordGamePerson person = getGame().getPerson(member);
			if (person != p)
			{
				person.mute(true);
				person.syncMute();
			}
		}
		return role.getManager().revokePermissions(Permission.VOICE_SPEAK);
	}
}
