public class Doctor implements Role{
	private String playerName; //get from discord
	private String name = "Doctor";
	//private Faction faction = 

	public String getRoleName(){
		
	}

	public String getFactionName(){
		
	}

	public int getPriority(){
		return 3;
	}

	private AttackStat getAtt(){
		return AttackStat.NONE;
	}

	private AttackStat getDef(){
		return DefenseStat.NONE;
	}

	public boolean execute(){
		
	}

	public boolean hasRBImunnity(){
		return false;
	}

	public boolean hasControlImmunity(){
		return false;
	}
}