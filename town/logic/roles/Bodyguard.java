package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Bodyguard is a Town role that can protect a person each night. If that person is attacked, the attacker and the Bodyguard are both killed.
public class Bodyguard implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;
	private int selfHeal;

	public Bodyguard(){
		selfHeal = 1;
	}

	public Bodyguard(int heals){
		selfHeal = heals;
	}

	//gets this role's unique name
	public String getRoleName(){
		return "Bodyguard";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 3;
	}

	public AttackStat getAttackStat(){
		return AttackStat.POWERFUL;
	}

	public DefenseStat getDefenseStat(){
		if (tempStat != null) return tempStat;
  		return DefenseStat.NONE;
	}

	//checks to see if this role can perform its action on given target
	public boolean canExecute(Player actor, Player target){
		//a bodyguard can always protect, except on themselves. they can only protect themselves once.
		if(target.equals(actor) && selfHeal <= 0)
				return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		if(actor.equals(target))
			selfHeal--;

		//IMPLEMENT: check to see if the target was attacked.
		//IMPLEMENT: If target is attacked, send message to target
		//IMPLEMENT: also send message to the attacker (you were blocked by BG) and the BG themselves
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
	}
}