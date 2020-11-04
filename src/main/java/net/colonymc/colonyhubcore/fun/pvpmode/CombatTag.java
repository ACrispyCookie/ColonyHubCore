package net.colonymc.colonyhubcore.fun.pvpmode;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.Main;

public class CombatTag {
	
	ArrayList<Player> players;
	BukkitTask expire;
	static ArrayList<CombatTag> activeTags = new ArrayList<CombatTag>();
	
	public CombatTag(ArrayList<Player> players) {
		this.players = players;
		expire = new BukkitRunnable() {
			@Override
			public void run() {
				end();
			}
		}.runTaskLater(Main.getInstance(), 300);
		activeTags.add(this);
	}
	
	public void end() {
		expire.cancel();
		for(Player p : players) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou are now out of combat!"));
		}
		activeTags.remove(CombatTag.this);
	}
	
	public void removePlayer(Player p) {
		if(players.contains(p)) {
			players.remove(p);
		}
	}
	
	public static CombatTag getCombat(Player p) {
		for(CombatTag t : activeTags) {
			if(t.players.contains(p)) {
				return t;
			}
		}
		return null;
	}

}