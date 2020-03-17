public class SuicideVigilante implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	public String getRoleName(){
		return "Vigilante";
	}

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

	public boolean execute(Player actor, Player target){
		actor.dies();
	}

	public boolean hasRBImunnity(){
		return true;
	}

	public boolean hasControlImmunity(){
		return true;
	}

	public void setDefenseStat(DefenseStat newStat){
		tempStat = newStat;
	}
}
