# Town of Salem Inspired Discord bot
We *were* planning to try to make an exact copy of Town of Salem, however, we've noticed that there are features that we can improve upon. For one, most of the time in Town of Salem, it's not fun being a dead player, especially when you die on the first night. There's also not much point to a medium when you could directly DM each other. Some mechanics also just don't work well on Discord, for instance the day phase. Having a 50 second time limit is going to be nightmarish for large groups in voice chat, but having more than 2 minutes is too much. So we're considering moving away from the day phase by vote.

There will probably be more instances were we tweak the game design to fit our needs, and as such, I don't think it would be fair to call this project a Town of Salem project, as we also want to avoid copying the name. That being said, this wouldn't have started if it wasn't for Town of Salem, credit is where it's due. Also a new name is currently pending.

Currently we're implementing all the phases:
- [ ] Morning, explain deaths
- [X] Discussion, also known as Day (For now, the discussion phase ends in 1-2 minutes, but we plan to make it based on majority vote)
- [ ] Accusation, first voting round
- [ ] Defense, accused person gives defense
- [ ] Judgement, final voting round
- [ ] Verdict, reveals votes
- [ ] Last words
- [X] Night

Here are the roles that we currently support:
- Serial Killer
- Civilian, a temporary townee

We're also working on getting the intial setup correctly. Here's how the game should start:
- Anyone can start a lobby with `tos.startParty`
- Anyone can join with `tos.join` (party host automatically joins)
- The party leader is then able to start the game with `tos.startGame`
- The bot will create a new server with the appropriate channels
- Game would commence once all the players join
- Once the game ends, the server gets transferred to the party owner.
	- What if the party owner leaves before the game ends? We delete the server.

We create a new server so that the admin is able to play without being able to peek at other channels. It also moves all the clutter away from the main channel. We start the game once all players have joined and the discussion phase is passed by vote, so most people should have enough time to settle down with their new roles.

Note: Since people can DM each other, we're considering embracing the concept by removing any (or most) role reveals. That way dead people can continue to plea and frame others.
