package net.colonymc.hubcore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AboutCommand implements CommandExecutor, TabCompleter {

	final String[] plugins = new String[] {"ColonyHubCore", "ColonyModerationSystem", "ColonyVotes"};
	final String[] lowerplugins = new String[] {"colonyhubcore", "colonymoderationsystem", "colonyvotes"};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/about <plugin>");
		if(args.length == 1) {
			if(Arrays.asList(lowerplugins).contains(args[0].toLowerCase())) {
				if(args[0].equalsIgnoreCase("ColonyHubCore")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonyHubCore: &fThis plugin was coded by the owner of the server &d&lACrispyCookie &fand it is responsible for everything unique to the lobby!"));
				}
				else if(args[0].equalsIgnoreCase("ColonyModerationSystem")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonyHubCore: &fThis plugin was coded by the owner of the server &d&lACrispyCookie &fand it is responsible for making the moderation process super easy across the whole network!"));
				}
				else if(args[0].equalsIgnoreCase("ColonyVotes")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonyVotes: &fThis plugin was coded by the owner of the server &d&lACrispyCookie &fand it is responsible for delivering every vote correctly!"));
				}
			}
			else {
				String pl = "";
				for(int i = 0; i < plugins.length; i++) {
					if(i + 1 == plugins.length) {
						pl = pl + plugins[i];
					}
					else {
						pl = pl + plugins[i] + ", ";
					}
					
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fValid plugins: &d" + pl));
			}
		}
		else {
			sender.sendMessage(usage);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		for(String s : plugins) {
			list.add(s);
		}
		return list;
	}

}
