package town;

public class JavaHelper
{
	static Integer parseInt(String str)
	{
		Integer num;
		try
		{
			num = Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		return num;
	}
}
