package net.colonymc.hubcore.util;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.hubcore.Main;

public class PortalListener implements Listener {
	
	ArrayList<Player> portalPlayers = new ArrayList<Player>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if((e.getPlayer().getLocation().getZ() <= 32.3 && e.getPlayer().getLocation().getZ() >= 30.7) 
				&& (e.getPlayer().getLocation().getX() <= 3.7 && e.getPlayer().getLocation().getX() >= -2.7) 
				&& e.getPlayer().getLocation().getBlock().getType() == Material.PORTAL) {
			if(!portalPlayers.contains(e.getPlayer())) {
				portalPlayers.add(e.getPlayer());
				openPortal(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(portalPlayers.contains(e.getPlayer())) {
			portalPlayers.remove(e.getPlayer());
		}
	}
	
	public void openPortal(Player p) {
		BukkitRunnable portal = new BukkitRunnable() {
			@Override
			public void run() {
				if((p.getLocation().getZ() >= 32.3 || p.getLocation().getZ() <= 30.7) || (p.getLocation().getX() >= 3.7 || p.getLocation().getX() <= -2.7) && p.getLocation().getBlock().getType() == Material.PORTAL) {
					p.chat("/server");
					portalPlayers.remove(p);
					cancel();
				}
				else {
					p.setVelocity(p.getLocation().getDirection().multiply(2).setY(0));
					if((p.getLocation().getZ() >= 32.3 || p.getLocation().getZ() <= 30.7) || (p.getLocation().getX() >= 3.7 || p.getLocation().getX() <= -2.7)) {
						p.chat("/server");
						portalPlayers.remove(p);
						cancel();
					}
				}
			}
		};
		portal.runTaskTimer(Main.getInstance(), 0L, 3L);
	}

}
