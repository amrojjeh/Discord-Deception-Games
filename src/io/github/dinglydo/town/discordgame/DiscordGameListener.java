package io.github.dinglydo.town.discordgame;

import javax.annotation.Nonnull;

import io.github.dinglydo.town.persons.DiscordGamePerson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordGameListener extends ListenerAdapter
{
	private final DiscordGame game;

	public DiscordGameListener(@Nonnull DiscordGame game)
	{
		if (game == null) throw new IllegalArgumentException("Game cannot be null");
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
		if (event.getGuild().getIdLong() != game.getGuildId()) return;
		boolean shouldKick = true;
		Member member = event.getMember();
		for (DiscordGamePerson p : game.getPlayersCache())
		{
			if (p.getID() == member.getIdLong())
			{
				p.join();
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

	// Bot should leave the game once the owner has changed
	@Override
	public void onGuildUpdateOwner(GuildUpdateOwnerEvent event)
	{
		if (event.getGuild().getIdLong() != game.getGuildId()) return;
		game.registerAsListener(false);
		game.getGuild().leave().queue();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent message)
	{
		if (message.getGuild().getIdLong() == game.getGuildId())
			processMessage(message.getMessage());
	}

	public boolean processMessage(Message message)
	{
		if (processMessage("!", message))
			return true;
		else return processMessage("pg.", message);
	}

	public boolean processMessage(String prefix, Message message)
	{
		return game.getConfig().getGameMode().getCommands().executeCommand(game, prefix, message);
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		DiscordGamePerson person = game.getPerson(event.getMember());
		person.syncMute();
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
	{
		DiscordGamePerson person = game.getPerson(event.getMember());
		person.syncMute();
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		if (event.getGuild().getIdLong() != game.getGuildId()) return;
		DiscordGamePerson person = game.getPerson(event.getMember());
		if (person != null)
			person.disconnect();
	}
}
