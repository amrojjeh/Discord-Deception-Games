package town.discordgame;

import net.dv8tion.jda.api.Permission;

// Quick Permissions
public class QP
{
	public static long readPermissions()
	{
		return Permission.getRaw(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL);
	}

	public static long writePermissions()
	{
		return Permission.getRaw(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
	}

	public static long speakPermissions()
	{
		return Permission.getRaw(Permission.VOICE_SPEAK, Permission.PRIORITY_SPEAKER);
	}
}