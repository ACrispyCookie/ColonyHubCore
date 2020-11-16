package net.colonymc.colonyhubcore.util;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyhubcore.Main;

public class PortalListener implements Listener {
	
	final ArrayList<Player> portalPlayers = new ArrayList<>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if((e.getPlayer().getLocation().getZ() <= Math.max(Main.getInstance().getConfig().getDouble("portal.pos1.z"), Main.getInstance().getConfig().getDouble("portal.pos2.z"))
				&& e.getPlayer().getLocation().getZ() >= Math.min(Main.getInstance().getConfig().getDouble("portal.pos1.z"), Main.getInstance().getConfig().getDouble("portal.pos2.z")))
				&& (e.getPlayer().getLocation().getX() <= Math.max(Main.getInstance().getConfig().getDouble("portal.pos1.x"), Main.getInstance().getConfig().getDouble("portal.pos2.x"))
				&& e.getPlayer().getLocation().getX() >= Math.min(Main.getInstance().getConfig().getDouble("portal.pos1.x"), Main.getInstance().getConfig().getDouble("portal.pos2.x")))
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
				if((p.getLocation().getZ() >= Math.max(Main.getInstance().getConfig().getDouble("portal.pos1.z"), Main.getInstance().getConfig().getDouble("portal.pos2.z"))
						|| p.getLocation().getZ() <= Math.min(Main.getInstance().getConfig().getDouble("portal.pos1.z"), Main.getInstance().getConfig().getDouble("portal.pos2.z")))
						|| (p.getLocation().getX() >= Math.max(Main.getInstance().getConfig().getDouble("portal.pos1.x"), Main.getInstance().getConfig().getDouble("portal.pos2.x"))
						|| p.getLocation().getX() <= Math.min(Main.getInstance().getConfig().getDouble("portal.pos1.x"), Main.getInstance().getConfig().getDouble("portal.pos2.x")))
						&& p.getLocation().getBlock().getType() != Material.PORTAL) {
					p.chat("/server");
					portalPlayers.remove(p);
					cancel();
				}
				else {
					p.setVelocity(p.getLocation().getDirection().multiply(2).setY(0));
				}
			}
		};
		portal.runTaskTimer(Main.getInstance(), 0L, 3L);
	}

}