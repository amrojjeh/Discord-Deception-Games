package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Doctor is a town role that can grant a person Powerful defense each night.
public class Veteran implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;
	private int numAlerts;

	public Veteran(){
		numAlerts = 3;
	}

	//gets this role's unique name
	public String getRoleName(){
		return "Veteran";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 1;
	}

	public AttackStat getAttackStat(){
		return AttackStat.BASIC;
	}

	public DefenseStat getDefenseStat(){
		if (tempStat != null) return tempStat;
  		return DefenseStat.NONE;
	}

	//checks to see if this role can perform its action on given target
	public boolean canExecute(Player actor, Player target){
		//a veteran can only "act" upon themselves, and only if they have alerts left
		if(actor.equals(target) && numAlerts > 0){
			return true;
		}
		return false;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		actor.getRole().setDefenseStat(DefenseStat.POWERFUL);
		for(Player visitor : actor.nightlyVisitors){
			if(visitor.canBeKillAble(AttackStat.POWERFUL)){
				target.dies();
				//IMPLEMENT: message that target was shot by veteran
			}
			else{
				//IMPLEMENT: message that target was attacked
			}
		}
		return true;
	}

	//can this role NOT be roleblocked?
	public boolean hasRBImunnity(){
		return true;
	}

	//can this role NOT be controlled by a witch?
	public boolean hasControlImmunity(){
		return true;
	}

	//used for healing / being jailed
	public void setDefenseStat(DefenseStat newStat){
		tempStat = newStat;
	}

	public void onEvent(Event event)
	{
		DefaultOnEvent.run(this, event);
	}
}
