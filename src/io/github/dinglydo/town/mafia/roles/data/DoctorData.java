package io.github.dinglydo.town.mafia.roles.data;

import io.github.dinglydo.town.roles.RoleData;

public class DoctorData implements RoleData
{
	private int selfHeal;

	public DoctorData(int selfHeal)
	{
		setSelfHeal(selfHeal);
	}

	public void setSelfHeal(int val)
	{
		this.selfHeal = val;
	}

	public int getSelfHeal()
	{
		return selfHeal;
	}

	public void useSelfHeal()
	{
		if (selfHeal == 0) throw new IllegalStateException("Cannot use selfheal if selfheal is 0");
		--selfHeal;
	}

	public boolean canSelfHeal()
	{
		return selfHeal > 0;
	}
}
