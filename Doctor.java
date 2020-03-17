public class Doctor implements Role{
	private String roleName = "Doctor";

	public String getRoleName(){
		return roleName;
	}

	public int getPriority(){
		return 3;
	}

	private AttackStat getAtt(){
		return AttackStat.NONE;
	}

	private DefenseStat getDef(){
		return DefenseStat.NONE;
	}

	public boolean execute(){
		return false;
	}

	public boolean hasRBImunnity(){
		return false;
	}

	public boolean hasControlImmunity(){
		return false;
	}
}
