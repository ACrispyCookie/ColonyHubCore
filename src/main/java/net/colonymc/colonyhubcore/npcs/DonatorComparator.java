package net.colonymc.colonyhubcore.npcs;

import java.util.Comparator;

public class DonatorComparator implements Comparator<Donator> {

	@Override
	public int compare(Donator d, Donator d1) {
		if(d.playerUuid.equals(d.playerUuid) && d.packageName.equals(d1.packageName) && d.timeDonated == d1.timeDonated) {
			return 1;
		}
		return 0;
	}

}
