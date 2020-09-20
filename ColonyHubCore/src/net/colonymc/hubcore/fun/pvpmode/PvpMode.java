package net.colonymc.hubcore.fun.pvpmode;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.NBTItems;
import net.colonymc.api.player.ColonyPlayer;
import net.colonymc.hubcore.Main;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class PvpMode implements Listener {
	
	static ArrayList<Player> pvpPlayers = new ArrayList<Player>();
	static HashMap<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
	static HashMap<Player, ItemStack[]> armors = new HashMap<Player, ItemStack[]>();
	
	public void togglePvpMode(Player p) {
		if(isPvping(p)) {
			disablePvpMode(p);
		}
		else {
			enablePvpMode(p);
		}
	}
	
	public void enablePvpMode(Player p) {
		new BukkitRunnable() {
			int i = 3;
			@Override
			public void run() {
				if(i == 3) {
					inventories.put(p, p.getInventory().getContents());
					armors.put(p, p.getInventory().getArmorContents());
					p.setAllowFlight(false);
					if(ColonyPlayer.getByPlayer(p).isFlying()) {
						p.setFlying(false);
						ColonyPlayer.getByPlayer(p).togglePlayerFlight();
					}
					p.getInventory().clear();
					p.getOpenInventory().getTopInventory().clear();
					p.getOpenInventory().getBottomInventory().clear();
					p.setItemOnCursor(new ItemStack(Material.AIR));
					p.getInventory().setItem(0, new ItemStackBuilder(Material.IRON_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
					p.getInventory().setItem(1, new ItemStackBuilder(Material.FISHING_ROD).unbreakable(true).build());
					p.getInventory().setItem(8, new ItemStackBuilder(Material.BARRIER).name("&cExit PvP Mode").addTag("type", new NBTTagString("cancelPvp")).build());
					p.getInventory().setArmorContents(new ItemStack[] {new ItemStackBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).unbreakable(true).build(), 
							new ItemStackBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).unbreakable(true).build(), new ItemStackBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).unbreakable(true).build()
							, new ItemStackBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).unbreakable(true).build()});
					p.updateInventory();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fPvP enables in &d" + i + " seconds&f!"));
					p.playSound(p.getLocation(), Sound.CLICK, 2, 1);
				}
				else if(i == 0) {
					pvpPlayers.add(p);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fPvP is now &denabled&f!"));
					p.playSound(p.getLocation(), Sound.EXPLODE, 2, 1);
					cancel();
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fPvP enables in &d" + i + " seconds&f!"));
					p.playSound(p.getLocation(), Sound.CLICK, 2, 1);
				}
				i--;
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
	public void disablePvpMode(Player p) {
		pvpPlayers.remove(pvpPlayers.indexOf(p));
		p.setAllowFlight(true);
		p.getInventory().clear();
		p.getOpenInventory().getTopInventory().clear();
		p.getOpenInventory().getBottomInventory().clear();
		p.setItemOnCursor(new ItemStack(Material.AIR));
		p.getInventory().setHeldItemSlot(0);
		p.getInventory().setContents(inventories.get(p));
		p.getInventory().setArmorContents(armors.get(p));
		p.updateInventory();
		p.playSound(p.getLocation(), Sound.HORSE_ARMOR, 2, 1);
	}
	
	public static boolean isPvping(Player p) {
		if(pvpPlayers.contains(p)) {
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(p.getItemInHand().getType() != Material.AIR) {
				if(NBTItems.hasTag(p.getItemInHand(), "type") && NBTItems.getString(p.getItemInHand(), "type").equals("axe")) {
					e.setCancelled(true);
					if(!PvpMode.isPvping(p)) {
						Main.getPvpInstance().enablePvpMode(p);
					}
				}
				else if(NBTItems.hasTag(p.getItemInHand(), "type") && NBTItems.getString(p.getItemInHand(), "type").equals("cancelPvp")) {
					e.setCancelled(true);
					if(PvpMode.isPvping(p)) {
						if(CombatTag.getCombat(p) == null) {
							Main.getPvpInstance().disablePvpMode(p);
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are currently in combat!"));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(CombatTag.getCombat(e.getPlayer()) != null) {
			CombatTag tag = CombatTag.getCombat(e.getPlayer());
			Player anotherP = tag.players.get(0).equals(e.getPlayer()) ? tag.players.get(1) : tag.players.get(0);
			anotherP.playSound(anotherP.getLocation(), Sound.ORB_PICKUP, 2, 1);
			anotherP.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&5&lKILL! &fYou killed &d" + e.getPlayer().getName() + "&f! (&f+10 &c❤&f)\n "));
			anotherP.setHealth(20);
			tag.end();
		}
		if(PvpMode.isPvping(e.getPlayer())) {
			Main.getPvpInstance().disablePvpMode(e.getPlayer());
		}
		if(inventories.containsKey(e.getPlayer())) {
			inventories.remove(e.getPlayer());
		}
		if(armors.containsKey(e.getPlayer())) {
			armors.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player damager = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			if(PvpMode.isPvping(damaged) && PvpMode.isPvping(damager)) {
				if(e.getFinalDamage() > damaged.getHealth()) {
					e.setCancelled(true);
					damaged.teleport(new Location(Bukkit.getWorld("world"), 0, 110, 0, 0, 0));
					damaged.setHealth(20);
					damaged.playSound(damaged.getLocation(), Sound.BAT_DEATH, 2f, 1.5f);
					damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&5&lDEATH! &fYou got killed by &d" + damager.getName() + "&f!\n "));
					Main.getPvpInstance().disablePvpMode(damaged);
					damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 2, 1);
					damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&5&lKILL! &fYou killed &d" + damaged.getName() + "&f! (&f+10 &c❤&f)\n "));
					damager.setHealth(20);
					CombatTag.getCombat(damaged).end();
				}
				else {
					ArrayList<Player> players = new ArrayList<Player>();
					players.add(damaged);
					players.add(damager);
					if(CombatTag.getCombat(damaged) != null) {
						CombatTag tag = CombatTag.getCombat(damaged);
						tag.removePlayer(damaged);
					}
					else {
						damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been combat tagged by &d" + damager.getName() + "&f!"));
					}
					if(CombatTag.getCombat(damager) != null) {
						CombatTag tag = CombatTag.getCombat(damager);
						tag.removePlayer(damager);
					}
					else {
						damager.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have combat tagged &d" + damaged.getName() + "&f!"));
					}
					new CombatTag(players);
				}
			}
		}
		else if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
			Player damaged = (Player) e.getEntity();
			if(PvpMode.isPvping(damaged) && PvpMode.isPvping(damager)) {
				ArrayList<Player> players = new ArrayList<Player>();
				players.add(damaged);
				players.add(damager);
				if(CombatTag.getCombat(damaged) != null) {
					CombatTag tag = CombatTag.getCombat(damaged);
					tag.removePlayer(damaged);
				}
				else {
					damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been combat tagged by &d" + damager.getName() + "&f!"));
				}
				if(CombatTag.getCombat(damager) != null) {
					CombatTag tag = CombatTag.getCombat(damager);
					tag.removePlayer(damager);
				}
				else {
					damager.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have combat tagged &d" + damaged.getName() + "&f!"));
				}
				new CombatTag(players);
			}
		}
	}
	

}
