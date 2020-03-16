public interface Role{
	/*
	//what role is it? (extension)
	//role name

	//faction
	//faction name

	//priority

	//attack stat, defense stat

	//overriden method execute()

	//role block immunity
	//control immunity

	//how can executioner become a jester

	//special attributes

	//a "dead" role that can only talk to dead and mediums
	*/


	public String getRoleName();

	public String getFactionName();

	public int getPriority();

	//return attack stat
	//return defense stat

	public boolean execute();

	public boolean hasRBImunnity();

	public boolean hasControlImmunity();

	//event listener for attributes

}