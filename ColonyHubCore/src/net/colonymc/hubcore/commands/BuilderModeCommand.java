package net.colonymc.hubcore.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyapi.MainDatabase;
import net.colonymc.hubcore.MainMessages;
import net.colonymc.hubcore.util.items.VisibilityListener;

public class BuilderModeCommand implements CommandExecutor, Listener {

	public static ArrayList<Player> builderMode = new ArrayList<Player>();
	public static HashMap<Player, ItemStack[]> items = new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, ItemStack[]> armor = new HashMap<Player, ItemStack[]>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission("colonyhub.buildermode")) {
				if(builderMode.contains(p)) {
					disableBuilder(p);
				}
				else {
					enableBuilder(p);
				}
			}
			else {
				p.sendMessage(MainMessages.noPerm);
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}
	
	public static void enableBuilder(Player p) {
		builderMode.add(p);
		items.put(p, p.getInventory().getContents());
		armor.put(p, p.getInventory().getArmorContents());
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have &aenabled &fyour &dbuilder mode!"));
		p.teleport(new Location(Bukkit.getWorld("builders"), 2.5, 4, 1.5));
		p.setGameMode(GameMode.CREATIVE);
		p.getInventory().clear();
		p.getOpenInventory().getBottomInventory().clear();
		p.getOpenInventory().getTopInventory().clear();
		p.setItemOnCursor(new ItemStack(Material.AIR));
	}
	
	public static void disableBuilder(Player p) {
		builderMode.remove(p);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have &cdisabled &fyour &dbuilder mode!"));
		p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 110.5, 0.5));
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().clear();
		p.getOpenInventory().getBottomInventory().clear();
		p.getOpenInventory().getTopInventory().clear();
		p.getInventory().setContents(items.get(p));
		p.getInventory().setArmorContents(armor.get(p));
		p.getInventory().setHeldItemSlot(0);
		checkVisibility(p);
		items.remove(p);
		armor.remove(p);
	}
	
	private static void checkVisibility(Player p) {
		ResultSet rs = MainDatabase.getResultSet("SELECT hubVisibility FROM PlayerInfo WHERE uuid='" + p.getUniqueId().toString() + "';");
		try {
			if(rs.next()) {
				if(!rs.getBoolean("hubVisibility")) {
					VisibilityListener.localVisibilityToggle(p, rs.getBoolean("hubVisibility"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if(!e.getPlayer().getWorld().equals(Bukkit.getWorld("builders")) && builderMode.contains(e.getPlayer())) {
			disableBuilder(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(builderMode.contains(e.getPlayer())) {
			disableBuilder(e.getPlayer());
		}
	}

}
