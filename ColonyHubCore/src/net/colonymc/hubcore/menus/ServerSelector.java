package net.colonymc.hubcore.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;
import net.colonymc.api.Main;
import net.colonymc.api.itemstacks.InventoryUtils;
import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.NBTItems;

public class ServerSelector implements Listener, InventoryHolder, CommandExecutor  {

	Inventory inv;
	Player p;
	
	public ServerSelector(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 27, "Select a server...");
		InventoryUtils.fillInventory(this, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).glint(true).build());
		inv.setItem(11, new ItemStackBuilder(Material.GRASS).name("&d&l&k:&d&lSkyblock Guilds&k:").lore("\n&fFeatures:\n  &f- &dCustom Viking Items\n  &f- &dCustom Guild System\n  &f- &dCustom Bosses/Items\n \n&fRequires &d1.8\n \n&fStatus: &cMaintenance\n&fOnline: &d" + PlaceholderAPI.setPlaceholders(p, "%bungee_skyblock%") + "/100").glint(true).build());
		inv.setItem(13, new ItemStackBuilder(Material.BARRIER).name("&d&kOOOOOOOOO").lore("\n&7&oComing soon...").glint(true).build());
		inv.setItem(15, new ItemStackBuilder(Material.BARRIER).name("&d&kOOOOOOOOO").lore("\n&7&oComing soon...").glint(true).build());
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ServerSelector() {
		
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ServerSelector) {
			ServerSelector menu = (ServerSelector) e.getInventory().getHolder();
			e.setCancelled(true);
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 11) {
					menu.p.chat("/play skyblock");
				}
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getType() == Material.NETHER_STAR && NBTItems.hasTag(p.getItemInHand(), "type") && NBTItems.getString(p.getItemInHand(), "type").equals("selector")) {
				p.openInventory(new ServerSelector(p).getInventory());
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			new ServerSelector(p);
		}
		return false;
	}

}
