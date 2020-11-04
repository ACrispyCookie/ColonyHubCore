package net.colonymc.colonyhubcore.fun.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KaboomCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command name, String label, String[] args) {
		if(sender.hasPermission("*")) {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cKABOOM by " + sender.getName() + "!"));
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.getWorld().strikeLightningEffect(p.getLocation());
				p.setVelocity(p.getVelocity().add(new Vector(0f, 3f, 0f)));
			}
		}
		else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot execute this command."));
		}
		return false;
	}
	
	

}
