# Discord Deception Games (DDG)
This is a Town of Salem inspired discord bot. This will become a collection of party games that fundamentally have more or less the same rules, but will vary by the roles that will be available. We're just finishing up **Talking Graves**, which has the roles:
- Civilian
- Medium
- Lookout
- Serial Killer

The number of each role that can exist will differ depending on the party size, but generally speaking Civilian is the most common role and Medium is the least common (Only one per game that isn't random). This game mode was made specifically because of the nice combination that a Medium and a Lookout can make. Because a lookout can see who visits him overnight, a Medium would be vital for seeing who killed the lookout. And since there can only be one Medium at most (if it isn't random), conflicts could arise on who's the medium and who's the lookout.

## Tutorial
Assuming you've invited the bot to your server, instructions on that later, you can view most of the commands with `pg.help`, but here's the run of the mill walkthrough on how to start a game:
- `pg.startparty` -> starts the lobby, there can only be one lobby in a server. (The one who starts the lobby becomes the party leader)
- `pg.join` -> joins the current lobby in the server. Note: if you started the party, you automatically join.
- `pg.party` -> to check who's currently in the lobby. Can also be used in the game.
- `pg.startgame` -> starts the game. Everyone in the lobby should get an invite to a new server. Only the party leader can start a game.
- `pg.endparty` -> if you want to remove the party and not start the game. Note: This is automatically done when starting a game.
(This command can be activated by anybody. This is done in the case that the party leader is AFK)
- `pg.games` -> lists all the current game modes available.
- `pg.setGame` -> changes the selected game mode.

Here are commands used once you are in the game (The pg. prefix can be replaced with ! once in the game):
- `!ability (mention|num)` (or `!a`) -> Uses your role ability. Some roles don't have one, others require a parameter.
- `!targets` -> Gives you a list of people that you could use your ability on.
- `!vote [mention|num]` -> Accuse a person during the accusation phase, either by mentioning him or using the number given by !party
- `!guilty` and `!innocent` -> Once someone has been put on trial, you can either vote guilty or innocent

Some of the more advanced commands:
- `pg.nomin [1|0]` -> Bypasses the minimum required players set by the game mode. Obviously not recommended, but whatever floats your boat.

## Inviting and running the bot
**Update: Since gateway intentions have been introduced, this bot no longer works. See you next version!**

Unlike most other bots, we can't just give you the invitation link. Bots can only create a server if they're under 10 servers, which means we can't distribute it. So you have to create your application and download our code, but we'll try to make it as easy as possible.

Go to https://discord.com/developers/applications, there you should be able to create your application. Once you do that, go to the application and open the bot section. You can then add your bot, and afterwards you may reveal your token, copy it, and add it in the token.txt file provided in the release (https://github.com/Persian-Eagle/Discord-Deception-Games/releases). You can then double click run.bat, and you should be good!

**Make sure the latest version of Java is installed on your machine, otherwise the program will not work.**

## Getting in contact
If you have suggestions for roles that could make an interesting game, or any questions, you can reach me at amrojjeh@outlook.com or DinglyDo#5197

## Upcoming features
https://trello.com/b/E537wPgg/discord-deception-games
