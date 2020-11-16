package net.colonymc.colonyhubcore.pms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyhubcore.MainMessages;

public class MessageCommand implements CommandExecutor {

	final String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/msg <player> <message>");
	final String playerNotOnline = ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is currently not online!");
	String playerHasToggledMsg = ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player has turned their messages off!");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player psender = (Player) sender;
			if(args.length >= 2) {
				if(Bukkit.getPlayer(args[0]) != null) {
					if(Bukkit.getPlayer(args[0]) != psender) {
						Player recipient = Bukkit.getPlayer(args[0]);
						if(Conversation.conversations.containsKey(psender) && 
								(Conversation.conversations.get(psender).recipient == recipient || 
								Conversation.conversations.get(psender).recipient == psender)) {
							List<String> text = new ArrayList<>(Arrays.asList(args.clone()));
							text.remove(0);
							StringBuilder message = new StringBuilder();
							for(String s : text) {
								message.append(s).append(" ");
							}
							Conversation conv = Conversation.conversations.get(psender);
							conv.resetTimer();
							conv.sendMessage(psender, message.toString());
						}
						else {
							List<String> text = new ArrayList<>(Arrays.asList(args.clone()));
							text.remove(0);
							StringBuilder message = new StringBuilder();
							for(String s : text) {
								message.append(s).append(" ");
							}
							Conversation conv = new Conversation(psender, recipient);
							conv.open();
							conv.sendMessage(psender, message.toString());
						}
					}
					else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot message yourself!"));
					}
				}
				else {
					sender.sendMessage(playerNotOnline);
				}
			}
			else {
				sender.sendMessage(usage);
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}
	
}
