package net.colonymc.colonyhubcore.fun.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CookieCommand implements CommandExecutor, Listener{
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(args.length >= 1) {
    		Player target = Bukkit.getPlayerExact(args[0]);
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("*")) {
    	    		if(target == null) {
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fPlayer is not online!"));
    					return false;
    				}
    				else if(args.length == 1 || args[1].equals("1")) {
    		    		ItemStack cookie = new ItemStack(Material.COOKIE, 1);
    		    		ItemMeta cookiemeta = cookie.getItemMeta();
    	    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + target.getPlayerListName() + "&f a special cookie!"));
    	    			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + target.getPlayerListName() + " &fgot &d1x &fspecial cookie!"));
    	    			cookiemeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lSpecial Cookie"));
    	    			cookiemeta.addEnchant(Enchantment.DURABILITY, -1, false);
    	    			ArrayList<String> lore = new ArrayList<>();
    	    			lore.add("");
    	    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fThis &d&lSpecial Cookie &fwas given"));
    	    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fto &d" + target.getPlayerListName() + "&f from &d" + p.getPlayerListName() + "&f!"));
    	    			cookiemeta.setLore(lore);
    	    			cookie.setItemMeta(cookiemeta);
    	    			target.getInventory().addItem(cookie);
    	                return true;
    				}
    				else if(args.length == 2){
    		    		int amount = Integer.parseInt(args[1]);
    		    		ItemStack cookie = new ItemStack(Material.COOKIE, amount);
    		    		ItemMeta cookiemeta = cookie.getItemMeta();
    	    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + "x" + amount + "&f special cookies to &d" + target.getPlayerListName() + "&f!"));
    	    			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + target.getPlayerListName() + " &fgot &d" + amount + "x &fspecial cookies!"));
    	    			cookiemeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lSpecial Cookie"));
    	    			cookiemeta.addEnchant(Enchantment.DURABILITY, -1, false);
    	    			ArrayList<String> lore = new ArrayList<>();
    	    			lore.add("");
    	    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fThis &d&lSpecial Cookie &fwas given"));
    	    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fto &d" + target.getPlayerListName() + "&f from &d" + p.getPlayerListName() + "&f!"));
    	    			cookiemeta.setLore(lore);
    	    			cookie.setItemMeta(cookiemeta);
    	    			target.getInventory().addItem(cookie);
    	                return true;
    				}
    			}
    			else {
    				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot execute this command."));
    			}
    		}
			else if(args.length == 1 || args[1] == "1") {
	    		ItemStack cookie = new ItemStack(Material.COOKIE, 1);
	    		ItemMeta cookiemeta = cookie.getItemMeta();
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + target.getPlayerListName() + "&f a special cookie!"));
    			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + target.getPlayerListName() + " &fgot &d1x &fspecial cookie!"));
    			cookiemeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lSpecial Cookie"));
    			cookiemeta.addEnchant(Enchantment.DURABILITY, -1, false);
    			ArrayList<String> lore = new ArrayList<>();
    			lore.add("");
    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fThis &d&lSpecial Cookie &fwas given"));
    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fto &d" + target.getPlayerListName() + "&f from &d" + sender.getName() + "&f!"));
    			cookiemeta.setLore(lore);
    			cookie.setItemMeta(cookiemeta);
    			target.getInventory().addItem(cookie);
                return true;
			}
			else if(args.length == 2){
	    		int amount = Integer.parseInt(args[1]);
	    		ItemStack cookie = new ItemStack(Material.COOKIE, amount);
	    		ItemMeta cookiemeta = cookie.getItemMeta();
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + "x" + amount + "&f special cookies to &d" + target.getPlayerListName() + "&f!"));
    			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + target.getPlayerListName() + " &fgot &d" + amount + "x &fspecial cookies!"));
    			cookiemeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lSpecial Cookie"));
    			cookiemeta.addEnchant(Enchantment.DURABILITY, -1, false);
    			ArrayList<String> lore = new ArrayList<>();
    			lore.add("");
    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fThis &d&lSpecial Cookie &fwas given"));
    			lore.add(ChatColor.translateAlternateColorCodes('&', "&fto &d" + target.getPlayerListName() + "&f from &d" + sender.getName() + "&f!"));
    			cookiemeta.setLore(lore);
    			cookie.setItemMeta(cookiemeta);
    			target.getInventory().addItem(cookie);
                return true;
			}
    	}
    	else {

		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/cookie <player> [amount]"));
        return false;
    	}
    	return false;
    }
	
 
}
	
