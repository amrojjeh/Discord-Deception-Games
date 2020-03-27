package town.logic;
// The game interface class will manage the communication between discord and the logic of the game

public interface GameInterface
{
	Event pollEvent();
	void sendDayMessage(String message);
	void sendNightMessage(String message, Faction faction);
	// This interface should also handle outputting
}
