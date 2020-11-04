package net.colonymc.colonyhubcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyhubcore.Main;


public class PvPModeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length == 1) {
				if(p.hasPermission("colonyhub.pvpcommand")) {
					if(Bukkit.getPlayerExact(args[0]) != null) {
						Main.getPvpInstance().togglePvpMode(Bukkit.getPlayerExact(args[0]));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou toggled &d" + Bukkit.getPlayerExact(args[0]).getName() + "'s &fpvp mode!"));
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
					}
				}
				else {
					Main.getPvpInstance().togglePvpMode(p);
				}
			}
			else {
				Main.getPvpInstance().togglePvpMode(p);
			}
		}
		else {
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/pvpmode <player>");
			if(args.length == 1) {
				if(Bukkit.getPlayerExact(args[0]) != null) {
					Main.getPvpInstance().togglePvpMode(Bukkit.getPlayerExact(args[0]));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou toggled &d" + Bukkit.getPlayerExact(args[0]).getName() + "'s &fpvp mode!"));
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
				}
			}
			else {
				sender.sendMessage(usage);
			}
		}
		return false;
	}

}
