package town.logic.roles.stats;

public enum AttackStat{
	NONE("None", 0), BASIC("Basic", 1), POWERFUL("Powerful", 2), UNSTOPPABLE("Unstoppable", 3);

	private String name;
	private int value;

	AttackStat(String theName, int theValue){
		name = theName;
		value = theValue;
	}

	public int getValue(){
		return value;
	}

}
