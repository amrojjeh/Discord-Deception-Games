//a  Vigilante becomes a SuicideVigilante if they kill a townie. The SuicideVigilante's only ability is to commit suicide that night.
public class SuicideVigilante implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	//gets this role's unique name
	public String getRoleName(){
		return "Vigilante";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 1;
	}

	public AttackStat getAttackStat(){
		return AttackStat.NONE;
	}

	public DefenseStat getDefenseStat(){
		if (tempStat != null) return tempStat;
  		return DefenseStat.NONE;
	}

	public boolean canExecute(Player actor, Player target){
		return false;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		actor.dies();
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
		// KIlls himself when it's end of night
	}

}
