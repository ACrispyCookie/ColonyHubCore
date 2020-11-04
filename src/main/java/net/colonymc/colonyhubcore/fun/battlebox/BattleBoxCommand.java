package net.colonymc.colonyhubcore.fun.battlebox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyhubcore.Main;
import net.colonymc.colonyhubcore.MainMessages;
import net.md_5.bungee.api.ChatColor;

public class BattleBoxCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/battlebox join/leave" + (p.hasPermission("*") ? "/start/stop" : ""));
			if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("join"))) {
				if(Main.getInstance().getBox().hasStarted()) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe game has already started!"));
				}
				else if(!Main.getInstance().getBox().isJoinable()) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThere is no available game right now!"));
				}
				else {
					if(Fighter.getByPlayer(p) == null) {
						Main.getInstance().getBox().addPlayerToGame(p);
					}
					else {
						p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are already in the game!"));
					}
				}
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("start")) {
					if(p.hasPermission("*")) {
						if(Main.getInstance().getBox().isJoinable()) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe game is already joinable!"));
						}
						else {
							Main.getInstance().getBox().setJoinable(true);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou changed the status of the game to &ajoinable&f!"));
						}
					}
					else {
						p.sendMessage(usage);
					}
				}
				else if(args[0].equalsIgnoreCase("stop")) {
					if(p.hasPermission("*")) {
						if(!Main.getInstance().getBox().isJoinable()) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe game is already closed!"));
						}
						else {
							Main.getInstance().getBox().setJoinable(false);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou changed the status of the game to &cnot joinable&f!"));
						}
					}
					else {
						p.sendMessage(usage);
					}
				}
				else if(args[0].equalsIgnoreCase("leave")) {
					if(Fighter.getByPlayer(p) != null) {
						Main.getInstance().getBox().removePlayer(p);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have left the game of &dBattleBox&f!"));
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are not currently in a game of BattleBox!"));
					}
				}
				else {
					p.sendMessage(usage);
				}
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

}
