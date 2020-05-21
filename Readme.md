# Town of Salem Discord bot
Once, it was a only but a dream to be able to play Town of Salem in Discord, but now it's becoming a reality.

We're starting all over again. Currently we're implementing all the phases:
- [ ] Morning, explain deaths
- [ ] Discussion (For now, the discussion phase ends in 1-2 minutes, but we plan to make it based on majority vote)
- [ ] Accusation, first voting round
- [ ] Defense, accused person gives defense
- [ ] Judgement, final voting round
- [ ] Verdict, reveals votes
- [ ] Last words
- [ ] Night

We're also working on getting the intial setup correctly. Here's how the game should start:
- Anyone can start a lobby with `!startLobby`
- Anyone can join with `!join`
- The party leader is then able to start the game with `!start`
- The bot will create a new server with the appropriate channels
- Game would immediately commence
- Once the game ends, the server gets deleted on request, or when everybody leaves.

We create a new server so that the admin is able to play without being able to peek at other channels. It also moves all the clutter away from the main channel. We start the game immediately because the discussion phase is passed by vote anyways, so most people should have enough time to settle down with their new roles.

It might also be possible to be able to play the game without enabling DMs. This is done by creating a specific private text channel. We might also be going with this plan completely and removing all DM communications, we'll have to see which works better.

Note: Since people can DM each other, we're considering embracing the concept by removing any (or most) role reveals. That way dead people can continue to plea and frame others.
