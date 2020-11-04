package net.colonymc.colonyhubcore.npcs;

public class Voter {
	
	String playerName;
	String playerUuid;
	long timeVoted;
	
	public Voter(String playerName, String playerUuid, long timeVoted) {
		this.playerName = playerName;
		this.playerUuid = playerUuid;
		this.timeVoted = timeVoted;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public String getPlayerUuid() {
		return playerUuid;
	}
	
	public long getTimeVoted() {
		return timeVoted;
	}

}
