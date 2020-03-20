package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

public interface Role
{
	/*
	also need to think about:

	//how can executioner become a jester

	//special attributes

	//a "dead" role that can only talk to dead and mediums
	*/

	//get the role name
	public String getRoleName();

	//get the "priority" of this role
	public int getPriority();

	//get this role's attack stat
	public AttackStat getAttackStat();
	
	//get this role's defense stat
	public DefenseStat getDefenseStat();

	//whether or not this role can perform its ability on a certain player. sometimes, that player is yourself.
	public boolean canExecute(Player actor, Player target);

	//this role's special ability, unique to each role
	public boolean execute(Player actor, Player target);

	//if this role can be roleblocked
	public boolean hasRBImunnity();

	//if this role can be controlled
	public boolean hasControlImmunity();

	//sets this role's defense stat (temporarily)
	public void setDefenseStat(DefenseStat newStat);

	// Whenever an event is dispatched from Game Manager, this method will run
	public void onEvent(Event event);

	//event listener for attributes

}
