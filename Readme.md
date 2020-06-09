# Town of Salem Inspired Discord bot
We *were* planning to try to make an exact copy of Town of Salem, however, we've noticed that there are features that we can improve upon (or some roles be removed).
For one, most of the time in Town of Salem, it's not fun being a dead player, especially when you die on the first night.
There's also not much point to a medium when you could directly DM each other.
Some mechanics also just don't work well on Discord, for instance the day phase.
Having a 50 second time limit is going to be nightmarish for large groups in voice chat,

There will probably be more instances were we tweak the game design to fit our needs, and as such,
I don't think it would be fair to call this project a Town of Salem project,as we also want to avoid copying the name.
That being said, this wouldn't have started if it wasn't for Town of Salem, credit is where it's due.
Also a new name is currently pending.

Here are the roles that we currently support:
- Serial Killer
- Civilian, a temporary townee
- Lookout

We're done working on getting the intial setup correctly. Here's how the game starts:
- Anyone can start a lobby with `tos.startParty`
- Anyone can join with `tos.join` (party host automatically joins)
- The party leader is then able to start the game with `tos.startGame`
- The bot will create a new server with the appropriate channels
- Game would commence once all the players join
- Once the game ends, the server gets transferred to the party owner.
	- What if the party owner leaves before the game ends? We delete the server.

We create a new server so that the admin is able to play without being able to peek at other channels.
It also moves all the clutter away from the main channel.
We start the game once all players have joined and Day takes two minutes, so most people should have enough time to settle down with their new roles.
An option to cut the day short with a command is going to be soon implemented.

Note: Since people can DM each other, we're considering embracing the concept by removing any (or most) role reveals. That way dead people can continue to plea and frame others.
Note on note: We've scratched the idea, but not completely. We might add it as a fun option soon, but for now we're going to have to beg users to not cheat.
