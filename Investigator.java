/*The Investigator is a Town role that gets a clue as to what a person's role might be.
There are 11 different results. They are as follows:
	-Your target owns a gun. They could be a Vigilante, Veteran, or Mafioso.
	-Your target works with the dead. They could be a Medium, Janitor, or Retributionist.
	-Your target is an outcast. They could be a Survivor, Vampire Hunter, or Amnesiac.
	-Your target knows dark secrets. They could be a Spy, Blackmailer, or Jailor.
	-Your target wants revenge. They could be a Sheriff, Executioner, or Werewolf.
	-Your target sticks to the shadows. They could be a Lookout, Forger, or Witch.
	-Your target is constantly up and about. They could be a Escort, Transporter, or Consort.
	-Your target is covered in blood! They could be a Doctor, Disguiser, or Serial Killer.
	-Your target is handling an investigation. They could be a Investigator, Consigliere, or Mayor.
	-Your target isn't afraid of a fight. They could be a Bodyguard, Godfather, or Arsonist.
	-Your target is up to no good. They could be a Framer, Vampire, or Jester.
Exceptions:
	-If a target is framed, the investigator will see the last result.
	-If a target is doused, the investigator will see the second to last result.
	-If investigating a disguiser, whoever they disguise as will be what the investigator sees.
*/
public class Investigator implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;

	//gets this role's unique name
	public String getRoleName(){
		return "Investigator";
	}

	//priority is used to determine in what order the different roles act.
	public int getPriority(){
		return 4;
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
		//an investigator can investigate anyone besides themselves
		if(actor.equals(target)) return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		switch(target.getRole().getRoleName()){
			case "Vigilante": case "Veteran": case "Mafioso":
				//send corresponding message.
				break;
			//IMPLEMENT: All the other possibilities.
			//IMPLEMENT: Check for doused
			//IMPLEMENT: Check for framed
			//IMPLEMENT: Disguiser's ability
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
