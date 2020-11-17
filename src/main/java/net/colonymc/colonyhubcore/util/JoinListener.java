package net.colonymc.colonyhubcore.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.colonymc.colonyhubcore.scoreboard.ScoreboardManager;
import net.minecraft.server.v1_8_R3.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyspigotapi.messages.Message;
import net.colonymc.colonyspigotapi.player.Title;
import net.colonymc.colonyspigotapi.player.TitleAction;
import net.colonymc.colonyapi.database.MainDatabase;
import net.colonymc.colonyhubcore.Main;
import net.colonymc.colonyhubcore.util.items.VisibilityListener;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class JoinListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
		p.getInventory().setHeldItemSlot(0);
		p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
		p.getInventory().setItem(0, new ItemStackBuilder(Material.NETHER_STAR).name("&5&lServer Selector &7(Right-Click)").addTag("type", new NBTTagString("selector")).build());
		p.getInventory().setItem(2, new ItemStackBuilder(Material.GOLD_AXE).name("&5&lEnable PvP Mode &7(Right-Click)").addTag("type", new NBTTagString("axe")).glint(true).unbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).build());
		p.getInventory().setItem(4, new ItemStackBuilder(Material.ENDER_PEARL).name("&5&lEnder Butt &7(Right-Click)").addTag("type", new NBTTagString("pearl")).glint(true).build());
		p.getInventory().setItem(8, new ItemStackBuilder(Material.INK_SACK).name("&5&lPlayer Visibility &7(Right-Click)").addTag("type", new NBTTagString("visibility")).durability((short) 10).build());
		new ScoreboardManager(p);
		checkVisibility(p);
		e.setJoinMessage(null);
		sendMotd(p);
		if(p.hasPermission("king.store") && !p.hasPermission("staff.store")) {
			e.setJoinMessage("\n" + ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + p.getDisplayName() + " &fhas joined the lobby!\n "));
		}
		p.teleport(Main.getInstance().getSpawn());
		if(p.hasPermission("*")) {
			sendFirework(p);
		}
		p.setGameMode(GameMode.ADVENTURE);
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1.5f);
		if(p.hasPlayedBefore()) {
			Title title = new Title(TitleAction.TITLE);
			title.text("&fWelcome back, &d" + p.getName()).fadeIn(5).duration(60).fadeOut(5).send(p);
		}
		else {
			Title title = new Title(TitleAction.TITLE);
			title.text("&fWelcome, &d" + p.getName()).fadeIn(5).duration(60).fadeOut(5).send(p);
		}
	}
	
	private void sendMotd(Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&m*-*&f&m-*-*-*-&d&m*-*-*-*-*-*-&f&m*-*-*&d&m-*-*-*-*-*-*&f&m-*-*-*-&d&m*-*"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		new Message("&fWelcome, &6&l" + p.getName() + " &fto &d&lColony&f&lMC").addRecipient(p).centered(true).send();
		new Message("&7&l&o{{ &7One of the best networks  &7&l&o}}").addRecipient(p).centered(true).send();
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lWEBSITE &dhttps://colonymc.net"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lSTORE   &dhttps://store.colonymc.net"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lDISCORD &dhttps://colonymc.net/discord"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&m*-*&f&m-*-*-*-&d&m*-*-*-*-*-*-&f&m*-*-*&d&m-*-*-*-*-*-*&f&m-*-*-*-&d&m*-*"));
		
	}

	public void checkVisibility(Player p) {
		ResultSet rs = MainDatabase.getResultSet("SELECT hubVisibility FROM PlayerInfo WHERE uuid='" + p.getUniqueId().toString() + "';");
		try {
			if(rs.next()) {
				if(!rs.getBoolean("hubVisibility")) {
					VisibilityListener.localVisibilityToggle(p, rs.getBoolean("hubVisibility"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(Player pl : VisibilityListener.disabledVis) {
			pl.hidePlayer(p);
		}
	}
	
	/*public void sendMaintenanceMode(Player p) {
		BukkitRunnable message = new BukkitRunnable() {
			@Override
			public void run() {
				p.sendMessage("");
				new Message("&d&lMAINTENANCE MODE").addRecipient(p).centered(true).send();
				new Message("&fThe server is currently on maintenance mode!").addRecipient(p).centered(true).send();
				new Message("&fYou can still play around in the lobby but you can't").addRecipient(p).centered(true).send();
				new Message("&fjoin any gamemode! If you want to stay updated about").addRecipient(p).centered(true).send();
				new Message("&fthe release of the server you can join our discord").addRecipient(p).centered(true).send();
				new Message("&fserver @ &dhttps://colonymc.net/discord").addRecipient(p).centered(true).send();
				p.sendMessage("");
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		};
		message.runTaskLater(Main.getInstance(), 20L);
	}*/
	
	public void sendFirework(Player p) {
		BukkitRunnable fw = new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if(i < 7) {
					Firework fw = (Firework) p.getWorld().spawn(p.getLocation(), Firework.class);
					FireworkMeta fwm = fw.getFireworkMeta();
					Builder builder = FireworkEffect.builder();
					fwm.addEffect(builder.flicker(true).withColor(Color.FUCHSIA).build());
					fwm.addEffect(builder.trail(true).build());
					fwm.addEffect(builder.withFade(Color.PURPLE).build());
					fwm.setPower(2);
					fw.setFireworkMeta(fwm);
					i++;
				}
				if(i == 3) {
					cancel();
				}
			}
		};
		fw.runTaskTimer(Main.getInstance(), 0L, 10L);
	}

}
