package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Vampire Hunter is a Town role that can visit a person each night. If they visit a Vampire, they stake them.
public class VampireHunter implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	//gets this role's unique name
	public String getRoleName(){
		return "Vampire Hunter";
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
  		return DefenseStat.NONE;
	}

	//checks to see if this role can perform its action on given target
	public boolean canExecute(Player actor, Player target){
		//a vigilante can shoot anyone but themselves, as long as they have bullets
		if(actor.equals(target)) return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		//check to see if target is a vampire.
		if(target.getRole().getRoleName().equals("Vampire")){
			//check to see if the target has worse than basic defense.
			if(target.canBeKillAble(actor.getRole().getAttackStat())){
				//if so, the vampire is staked
				target.dies();
			}
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

	public void onEvent(Event event)
	{

	}
}