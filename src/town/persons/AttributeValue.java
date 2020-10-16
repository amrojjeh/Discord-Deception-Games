package town.persons;

public enum AttributeValue
{
	NONE(0),
	BASIC(1),
	POWERFUL(2),
	OMEGA(3);

	private int val;
	AttributeValue(int val)
	{
		this.val = val;
	}

	public int getVal()
	{
		return val;
	}
}
