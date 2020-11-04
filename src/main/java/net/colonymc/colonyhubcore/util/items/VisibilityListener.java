package net.colonymc.colonyhubcore.util.items;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyspigotapi.itemstacks.NBTItems;
import net.colonymc.colonyapi.MainDatabase;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class VisibilityListener implements Listener {
	
	public static ArrayList<Player> disabledVis = new ArrayList<Player>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack i = p.getItemInHand();
			if(i != null && i.hasItemMeta() && NBTItems.hasTag(i, "type") && NBTItems.getString(i, "type").equals("visibility")) {
				toggleVisibility(p);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(disabledVis.contains(p)) {
			disabledVis.remove(p);
		}
	}

	private void toggleVisibility(Player p) {
		ResultSet rs = MainDatabase.getResultSet("SELECT hubVisibility FROM PlayerInfo WHERE uuid='" + p.getUniqueId().toString() + "';");
		try {
			if(rs.next()) {
				boolean visibility = rs.getBoolean("hubVisibility");
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				localVisibilityToggle(p, !visibility);
				MainDatabase.sendStatement("UPDATE PlayerInfo SET hubVisibility=" + (!visibility) + " WHERE uuid='" + p.getUniqueId().toString() + "';");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void localVisibilityToggle(Player p, boolean visibility) {
		if(!visibility) {
			p.getInventory().setItem(8, new ItemStackBuilder(Material.INK_SACK).name("&5&lPlayer Visibility &7(Right-Click)").durability((short) 8).addTag("type", new NBTTagString("visibility")).build());
			disabledVis.add(p);
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(!pl.hasPermission("staff.store")) {
					p.hidePlayer(pl);
				}
			}
		}
		else {
			p.getInventory().setItem(8, new ItemStackBuilder(Material.INK_SACK).name("&5&lPlayer Visibility &7(Right-Click)").durability((short) 10).addTag("type", new NBTTagString("visibility")).build());
			if(disabledVis.contains(p)) {
				disabledVis.remove(p);
			}
			for(Player pl : Bukkit.getOnlinePlayers()) {
				p.showPlayer(pl);
			}
		}
	}

}
