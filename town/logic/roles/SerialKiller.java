package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Serial Killer is a Neutral Killing role that can attack a player each night.
public class SerialKiller implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	//gets this role's unique name
	public String getRoleName(){
		return "Serial Killer";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 5;
	}

	public AttackStat getAttackStat(){
		return AttackStat.BASIC;
	}

	public DefenseStat getDefenseStat(){
		if (tempStat != null) return tempStat;
  		return DefenseStat.BASIC;
	}

	//checks to see if this role can perform its action on given target
	public boolean canExecute(Player actor, Player target){
		//can attack anyone but themselves
		if(target.equals(actor))  return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		actor.visit(target);
		//check to see if the target has worse than basic defense.
		if(target.canBeKillAble(actor.getRole().getAttackStat())){
			//if so, they die
			target.dies();
		}

		else{
			//IMPLEMENT: send message to target that they were attacked
			//IMPLEMENT: send message to Serial Killer their target was immune
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