public enum DefenseStat{
	NONE("None", 0), BASIC("Basic", 1), POWERFUL("Powerful", 2), INVINCIBLE("Unstoppable", 3);

	private String name;
	private int value;

	DefenseStat(String theName, int theValue){
		name = theName;
		value = theValue;
	}

	public int getValue(){
		return value;
	}

}