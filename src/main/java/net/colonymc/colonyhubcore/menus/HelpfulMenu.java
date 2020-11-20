package net.colonymc.colonyhubcore.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import net.colonymc.colonyspigotapi.Main;
import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotapi.api.itemstack.SkullItemBuilder;

public class HelpfulMenu implements Listener, InventoryHolder, CommandExecutor {

	Inventory inv;
	Player p;
	
	public HelpfulMenu(Player p) {
		this.p = p;
		inv = Bukkit.createInventory(this, 45, "Helpful Menu");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public HelpfulMenu() {
		
	}
	
	public void fillInventory() {
		inv.setItem(4, new ItemStackBuilder(Material.NETHER_STAR).name("&5&lServer Help").lore("\n&fShows you the\n"
				+ "&davailable commands&f!\n").build());
		inv.setItem(21, new ItemStackBuilder(Material.BOOK_AND_QUILL).glint(true).name("&5&lOnline Store").lore("\n&fGives you a URL which\n"
				+ "&fgets you to our &dOnline Store&f!\n ").build());
		inv.setItem(23, new ItemStackBuilder(Material.COMPASS).glint(true).name("&5&lServer Selector").lore("\n&fShows you another menu\n"
				+ "&fwith all the available &dservers&f!\n ").build());
		inv.setItem(39, new SkullItemBuilder().url("http://textures.minecraft.net/texture/43467a531978d0b8fd24f56285c72734d84f5ec88e0b47c49323362979b323af").name("&d&lWebsite").lore("\n&fGives you a URL which\n"
				+ "&fgets you to our &dWebsite!\n ").build());
		inv.setItem(40, new SkullItemBuilder().url("http://textures.minecraft.net/texture/3685a0be743e9067de95cd8c6d1ba21ab21d37371b3d597211bb75e43279").name("&b&lTwitter Profile").lore("\n&fGives you a URL which\n"
				+ "&fgets you to our &dTwitter Profile!\n ").build());
		inv.setItem(41, new SkullItemBuilder().url("http://textures.minecraft.net/texture/7873c12bffb5251a0b88d5ae75c7247cb39a75ff1a81cbe4c8a39b311ddeda").name("&5&lDiscord Server").lore("\n&fGives you a URL which\n"
				+ "&fgets you to our &dDiscord Server!\n ").build());
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof HelpfulMenu) {
			HelpfulMenu menu = (HelpfulMenu) e.getInventory().getHolder();
			e.setCancelled(true);
			if(e.getSlot() == 4) {
				menu.p.closeInventory();
				new HelpCommandsMenu(menu.p);
			}
			else if(e.getSlot() == 23) {
				menu.p.closeInventory();
				new ServerSelector(menu.p);
			}
			else if(e.getSlot() == 21) {
				menu.p.closeInventory();
				menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can visit our online store @ &dhttps://store.colonymc.net"));
			}
			else if(e.getSlot() == 39) {
				menu.p.closeInventory();
				menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can visit our website @ &dhttps://colonymc.net"));
			}
			else if(e.getSlot() == 40) {
				menu.p.closeInventory();
				menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can visit our twitter profile @ &dhttps://twitter.com/@Colony_MC"));
			}
			else if(e.getSlot() == 41) {
				menu.p.closeInventory();
				menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can join our discord server @ &dhttps://colonymc.net/discord"));
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			new HelpfulMenu(p);
		}
		return false;
	}

}
