package town.logic.roles;

import town.logic.roles.stats.AttackStat;
import town.logic.roles.stats.DefenseStat;
import town.logic.Player;
import town.logic.Event;

//Doctor is a town role that can grant a person Powerful defense each night.
public class Doctor implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;
	private int selfHeal;

	public Doctor(){
		selfHeal = 1;
	}

	public Doctor(int heals){
		selfHeal = heals;
	}

	//gets this role's unique name
	public String getRoleName(){
		return "Doctor";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 3;
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
		//a doctor can always heal, except on themselves. they can only heal themselves once.
		if(target.equals(actor) && selfHeal <= 0)
				return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		if(actor.equals(target))
			selfHeal--;

		target.getRole().setDefenseStat(DefenseStat.POWERFUL);
		//IMPLEMENT: If target is attacked, send message to target (this one will be tough)
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
