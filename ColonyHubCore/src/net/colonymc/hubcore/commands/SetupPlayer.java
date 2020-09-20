package net.colonymc.hubcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.hubcore.MainMessages;
import net.colonymc.hubcore.scoreboard.Scoreboard;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class SetupPlayer implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission("colonyhub.setupplayer")) {
				if(args.length == 1) {
					if(Bukkit.getPlayerExact(args[0]) != null) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou reset the player &d" + Bukkit.getPlayerExact(args[0]).getName() + "&f!"));
						setupPlayer(Bukkit.getPlayerExact(args[0]));
					}
					else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online"));
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou reset yourself!"));
					setupPlayer(p);
				}
			}
			else {
				p.sendMessage(MainMessages.noPerm);
			}
		}
		else {
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: /setupplayer <player>");
			if(args.length == 1) {
				if(Bukkit.getPlayerExact(args[0]) != null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou reset the player &d" + Bukkit.getPlayerExact(args[0]).getName() + "&f!"));
					setupPlayer(Bukkit.getPlayerExact(args[0]));
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online"));
				}
			}
			else {
				sender.sendMessage(usage);
			}
		}
		return false;
	}
	
	public static void setupPlayer(Player p) {
		p.setGameMode(GameMode.ADVENTURE);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setAllowFlight(true);
		p.setScoreboard(new Scoreboard().scoreboardNormalCreate(p));
		p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 110.5, 0.5));
		p.getOpenInventory().getBottomInventory().clear();
		p.getOpenInventory().getTopInventory().clear();
		p.getInventory().clear();
		p.setItemOnCursor(new ItemStack(Material.AIR));
		p.getInventory().setHeldItemSlot(0);
		p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
		p.getInventory().setItem(0, new ItemStackBuilder(Material.NETHER_STAR).name("&5&lServer Selector &7(Right-Click)").addTag("type", new NBTTagString("selector")).build());
		p.getInventory().setItem(2, new ItemStackBuilder(Material.GOLD_AXE).name("&5&lEnable PvP Mode &7(Right-Click)").addTag("type", new NBTTagString("axe")).unbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).glint(true).build());
		p.getInventory().setItem(4, new ItemStackBuilder(Material.ENDER_PEARL).name("&5&lEnder Butt &7(Right-Click)").addTag("type", new NBTTagString("pearl")).glint(true).build());
		p.getInventory().setItem(8, new ItemStackBuilder(Material.INK_SACK).name("&5&lPlayer Visibility &7(Right-Click)").addTag("type", new NBTTagString("visibility")).durability((short) 10).build());
	}

}
