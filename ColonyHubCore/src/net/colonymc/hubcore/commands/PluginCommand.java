package net.colonymc.hubcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import net.colonymc.api.messages.Message;

public class PluginCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!p.hasPermission("*")) {
				new Message("&5&lCustom Plugins").addRecipient(p).centered(true).send();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonyHubCore: &fThis plugin is responsible for everything that is unique to this lobby!\n"
						+ " &5&l» &dColonyModerationSystem: &fThis plugin is responsible for making moderation super easy on the whole network!\n"
						+ " &5&l» &dColonyVotes: &fThis plugin is responsible for delivering every vote correctly!"));
			}
			else {
				new Message("&5&lAll Plugins (" + Bukkit.getPluginManager().getPlugins().length + ")").addRecipient(p).centered(true).send();
				String message = "";
				int i = 0;
				for(Plugin pl : Bukkit.getPluginManager().getPlugins()) {
					i++;
					if(i != Bukkit.getPluginManager().getPlugins().length) {
						message = message + ChatColor.LIGHT_PURPLE + pl.getName() + ", ";
					}
					else {
						message = message + ChatColor.LIGHT_PURPLE + pl.getName();
					}
				}
				sender.sendMessage(message);
			}
		}
		return false;
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(!e.getPlayer().hasPermission("*")) {
			if((e.getMessage().contains(" ") && e.getMessage().substring(0, e.getMessage().indexOf(' ')).contains(":")) || (!e.getMessage().contains(" ") && e.getMessage().contains(":"))) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease do not use this syntax on your commands!"));
			}
		}
		else if(e.getMessage().equals("/plugins") || e.getMessage().equals("/pl") || e.getMessage().equals("/ver") || e.getMessage().equals("/version")) {
			e.setMessage("/plugin");
		}
	}


}
