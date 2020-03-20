package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Vigilante is a Town role that has 3 bullets, and use basic attack on a person any night after night 1
public class Vigilante implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;
	//how many bullets the vigilante has
	private int numBullets;

	public Vigilante(){
		numBullets = 3;
	}

	public Vigilante(int b){
		numBullets = b;
	}

	//gets this role's unique name
	public String getRoleName(){
		return "Vigilante";
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
		if(numBullets <= 0) return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		//check to see if the target has worse than basic defense.
		if(target.canBeKillAble(actor.getRole().getAttackStat())){
			//if so, the vigilante's target dies
			target.dies();
			numBullets--;
			//if the target was a townie, the vigilante will commit suicide the next night
			if(target.getFaction().major.equals(actor.getFaction().major)){
				actor.setRole(new SuicideVigilante());
			}
		}

		else{
			//IMPLEMENT: send message to target that they were attacked
			//IMPLEMENT: send message to Vigilante their attack failed
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
		DefaultOnEvent.run(this, event);
	}
}
