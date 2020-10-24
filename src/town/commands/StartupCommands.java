package town.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import town.GameParty;
import town.MainListener;
import town.PartyIsFullException;

public class StartupCommands extends CommandSet<MainListener>
{
	public StartupCommands()
	{
		addCommand(true, StartupCommands::startLobby, "startparty");
	}

	public static void startLobby(MainListener ml, Message message)
	{
		System.out.println("Starting party");
		MessageChannel channelUsed = message.getChannel();

		if (ml.isChannelUsedForParty(message.getTextChannel()))
			channelUsed.sendMessage("Party already started").queue();
		else if (ml.isGuildUsedForGame(message.getGuild()))
			channelUsed.sendMessage("Can't start a party in a discord game!").queue();
		else
		{
			GameParty party = GameParty.createParty(ml.getJDA(), message.getTextChannel(), message.getMember());
			String[] words = message.getContentRaw().split(" ", 2);
			String messageToSend = "Party started\n";
			// TODO: Change custom parsers
			String gameName = party.getConfig().getGameMode().getName();

			// words[0] = pg.startparty
			// words[1] = Talking Graves Rand
			if (words.length == 2)
					gameName = words[1];
			messageToSend += party.getConfig().setGameMode(gameName) + "\n";

			channelUsed.sendMessage(messageToSend).queue();
			if (messageToSend.contains("FAILED"))
			{
				party.registerAsListener(false);
				return;
			}

			ml.addGameParty(party);
			try {
				// TODO: Make him game leader automatically when he first joins
				party.joinGame(message.getMember());
				party.setGameLeader(party.getPerson(message.getMember()));
			} catch (PartyIsFullException e) {
				e.panicInDiscord(channelUsed);
			}
		}
	}
}
