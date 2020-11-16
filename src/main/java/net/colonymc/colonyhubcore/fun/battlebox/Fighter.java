package net.colonymc.colonyhubcore.fun.battlebox;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Fighter {

	final Player p;
	final Team team;
	boolean dead;
	int kills = 0;
	static final ArrayList<Fighter> fighters = new ArrayList<>();
	
	public Fighter(Player p, Team team, boolean dead) {
		this.p = p;
		this.team = team;
		this.dead = dead;
		fighters.add(this);
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void addKill() {
		kills++;
	}
	
	public static Fighter getByPlayer(Player p) {
		for(Fighter f : fighters) {
			if(p.getUniqueId().toString().equals(f.getPlayer().getUniqueId().toString())) {
				return f;
			}
		}
		return null;
	}
	
}
