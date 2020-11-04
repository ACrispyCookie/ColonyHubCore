package net.colonymc.colonyhubcore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.colonymc.colonyspigotapi.itemstacks.NBTItems;
import net.colonymc.colonyhubcore.commands.BuilderModeCommand;
import net.colonymc.colonyhubcore.fun.pvpmode.PvpMode;

public class InteractionListeners implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("*") && p.getGameMode() != GameMode.CREATIVE) {
			if(e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.BEACON || e.getClickedBlock().getType() == Material.ANVIL)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("*") && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("*") && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("*") && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("*") && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFishItem(PlayerFishEvent e) {
		if(e.getState() == State.CAUGHT_FISH) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFishItem(PlayerMoveEvent e) {
		if(e.getPlayer().getLocation().getY() < 10 && e.getPlayer().getWorld().equals(Bukkit.getWorld("world"))) {
			e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 110, .5, 0, 0));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(PvpMode.isPvping((Player) e.getEntity())) {
				if(e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.PROJECTILE) {
					e.setCancelled(true);
				}
			}
			else {
				e.setCancelled(true);	
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			if(!BuilderModeCommand.builderMode.contains(p) && !PvpMode.isPvping(p) && !p.hasPermission("*")) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player damager = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			if(!PvpMode.isPvping(damager) || !PvpMode.isPvping(damaged)) {
				e.setCancelled(true);
				if(damager.getItemInHand().getType() != Material.AIR && NBTItems.hasTag(damager.getItemInHand(), "type") && NBTItems.getString(damager.getItemInHand(), "type").equals("axe")) {
					damager.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cRight-click the axe to enable your PvP-mode in order to damage other players!"));
				}
			}
		}
		else if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
			Player damaged = (Player) e.getEntity();
			if(!PvpMode.isPvping(damager) || !PvpMode.isPvping(damaged)) {
				e.setCancelled(true);
			}
		}
	}
	
}
