package net.colonymc.hubcore.npcs;

import java.util.Comparator;

public class VoterComparator implements Comparator<Voter>{
	
	@Override
	public int compare(Voter d, Voter d1) {
		if(d.playerUuid.equals(d.playerUuid) && d.timeVoted == d1.timeVoted) {
			return 1;
		}
		return 0;
	}

}
