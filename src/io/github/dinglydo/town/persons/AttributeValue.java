package io.github.dinglydo.town.persons;

public enum AttributeValue
{
	DEFAULT(-1),
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
