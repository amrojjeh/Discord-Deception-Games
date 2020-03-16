public enum Faction{
	TOWN(0), MAFIA(1), SERIAL_KILLER(2), ARSONIST(3), WEREWOLF(4), VAMPIRE(5), NEUTRAL(10);
	//for any of these factions to win, all other factions on this list must be wiped out (except neutral)
	int arrIndex;

	Faction(int theArrIndex){
		arrIndex = theArrIndex;
	}

	//somewhere else in the code, at the end of each night the game will check and see what the living players' factions are.
	//if a faction member is found, their faction's corresponding index is set to true in a boolean array list.

	/*EXAMPLE:
	A Townie, Serial killer and Executioner are the only ones left.
	The array list would be
	[true, false, true, false, false, false]
	and neutrals are not counted.*/

}