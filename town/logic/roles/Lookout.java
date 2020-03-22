package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Lookout is a role that can see who visited a player each night.
public class Lookout implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	//gets this role's unique name
	public String getRoleName(){
		return "Lookout";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 4;
	}

	public AttackStat getAttackStat(){
		return AttackStat.NONE;
	}

	public DefenseStat getDefenseStat(){
		if (tempStat != null) return tempStat;
  		return DefenseStat.NONE;
	}

	//checks to see if this role can perform its action on given target
	public boolean canExecute(Player actor, Player target){
		//a lookout can watch anyone but themselves
		if(target.equals(actor))  return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		actor.visit(target);
		for(Player visitor : target.nightlyVisitors){
			//IMPLEMENT: send a message to the player, telling them everyone who visited their target.
		}
		return true;
	}

	//can this role NOT be roleblocked?
	public boolean hasRBImunnity(){
		return false;
	}

	//can this role NOT be controlled by a witch?
	public boolean hasControlImmunity(){
		return false;
	}

	//used for healing / being jailed
	public void setDefenseStat(DefenseStat newStat){
		tempStat = newStat;
	}

	// Gets called when the night finishes
	public void onEvent(Event event)
	{
		// Write and execute a default onEvent class
		// This is where tempstat gets reset
		DefaultOnEvent.run(this, event);
	}
}