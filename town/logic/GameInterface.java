package town.logic;
// The game interface class will manage the communication between discord and the logic of the game

public interface GameInterface
{
	boolean pollEvent(Event e);
}
