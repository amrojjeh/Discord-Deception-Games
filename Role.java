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


	String getRoleName();

	String getFactionName();

	int getPriority();

	//return attack stat
	//return defense stat

	boolean execute();

	boolean hasRBImunnity();

	boolean hasControlImmunity();

	//event listener for attributes

}