package net.colonymc.hubcore.npcs;

public class Donator {
	
	String playerName;
	String playerUuid;
	String packageName;
	int packagePrice;
	long timeDonated;
	
	public Donator(String playerName, String playerUuid, String packageName, int packagePrice, long timeDonated) {
		this.playerName = playerName;
		this.playerUuid = playerUuid;
		this.packageName = packageName;
		this.packagePrice = packagePrice;
		this.timeDonated = timeDonated;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public String getPlayerUuid() {
		return playerUuid;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public int getPackagePrice() {
		return packagePrice;
	}
	
	public long getTimeDonated() {
		return timeDonated;
	}

}
