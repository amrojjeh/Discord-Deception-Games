/*Sheriff is a Town role that can investigate people to get clear-cut results on their allegiance.
The following roles will appear as "suspicious" to a sheriff:
	- Any Mafia role, EXCEPT Godfather.
	- Serial Killer.
	- Werewolf, but only on a full moon.
	- Anyone who is framed by a Framer.
All other roles are considered "not suspicious."*/
public class Sheriff implements Role{
	//tempStat starts out as null, is set to Powerful when a person is healed
	private DefenseStat tempStat;
	private String[] suspicious = {"Mafia", "Werewolf", "Serial Killer"};

	//gets this role's unique name
	public String getRoleName(){
		return "Sheriff";
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
		//a sheriff can investigate anyone besides themselves
		if(actor.equals(target)) return false;
		return true;
	}

	//this role's action.
	public boolean execute(Player actor, Player target){
		for(String f : suspicious){
			if(f.equals(target.getFaction().getFactionName())){
				if(!target.getRole().getRoleName().equals("Godfather")){
					if(!target.getRole().getRoleName().equals("Werewolf")) //IMPLEMENT: and it's an even night)
					{
						//IMPLEMENT: send a message to sheriff, telling them their target is suspcious
					}
				}
			}
			//IMPLEMENT: check to see if the target was framed
			else{
				//IMPLEMENT: send a message to sheriff, telling them their target is not suspicious
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
		DefaultOnEvent.run(this, event);
	}
}