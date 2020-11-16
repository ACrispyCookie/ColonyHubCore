package net.colonymc.colonyhubcore.npcs;

public class Donator {
	
	final String playerName;
	final String playerUuid;
	final String packageName;
	final int packagePrice;
	final long timeDonated;
	
	public Donator(String playerName, String playerUuid, String packageName, double packagePrice, long timeDonated) {
		this.playerName = playerName;
		this.playerUuid = playerUuid;
		this.packageName = packageName;
		this.packagePrice = (int) packagePrice;
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
