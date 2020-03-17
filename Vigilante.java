public class Vigilante implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	public String getRoleName(){
		return "Vigilante";
	}

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

	public boolean execute(Player actor, Player target){
		
	}

	public boolean hasRBImunnity(){
		return false;
	}

	public boolean hasControlImmunity(){
		return false;
	}

	public void setDefenseStat(DefenseStat newStat){
		tempStat = newStat;
	}
}