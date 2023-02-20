package net.colonymc.colonyhubcore.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyhubcore.Main;
import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;

public class HelpCommandsMenu implements Listener, InventoryHolder, CommandExecutor {

	Inventory inv;
	Player p;
	
	public HelpCommandsMenu(Player p) {
		this.p = p;
		inv = Bukkit.createInventory(this, 36, "Help Commands");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public HelpCommandsMenu() {
		
	}
	
	public void fillInventory() {
		inv.setItem(22, new ItemStackBuilder(Material.PAPER).glint(true).name("&5&lLobby Commands").lore("\n&d/help &fto access this menu.\n"
				+ "&d/menu &fto access a helpful menu about the server.\n"
				+ "&d/vote &fto vote for the server and help him grow.\n"
				+ "&d/server &fto access a menu for all the available servers.\n"
				+ "&d/report &fto report someone to the staff team.\n"
				+ "&d/msg &fto private message someone.\n"
				+ "&d/media &fto apply for the Media rank.\n"
				+ "&d/apply &fto apply for the Helper rank.\n"
				+ "&d/discord &fto get a URL for our discord server.\n ").build());
		inv.setItem(4, new ItemStackBuilder(Material.NETHER_STAR).name("&5&lHelpful Menu").lore("\n&fClick to access the &dhelpful menu&f.").build());
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof HelpCommandsMenu) {
			HelpCommandsMenu menu = (HelpCommandsMenu) e.getInventory().getHolder();
			e.setCancelled(true);
			if(e.getSlot() == 4) {
				menu.p.closeInventory();
				new HelpfulMenu(menu.p);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			new HelpCommandsMenu(p);
		}
		return false;
	}
	
}
