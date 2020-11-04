package net.colonymc.colonyhubcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyhubcore.MainMessages;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fTeleporting you to &dspawn!"));
			p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 110.5, 0.5));
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

}
