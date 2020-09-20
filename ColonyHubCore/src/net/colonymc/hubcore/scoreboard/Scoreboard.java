package net.colonymc.hubcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import me.clip.placeholderapi.PlaceholderAPI;
import net.colonymc.hubcore.Main;

public class Scoreboard implements Listener {
	
	public static void linesUpdate(Player p) {
		org.bukkit.scoreboard.Scoreboard sb = p.getScoreboard();
		if(sb.getObjective("ColonyMC [Lobby]").getDisplayName() != null) {
			String dn = sb.getObjective("ColonyMC [Lobby]").getDisplayName();
			if(dn.equals(ChatColor.translateAlternateColorCodes('&', "&5&lColonyMC &7[Lobby]"))){
				sb.getTeam("rank").setSuffix(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, "&d%vault_group%")));
				sb.getTeam("ping").setSuffix(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, "&d%player_ping%ms")));
				sb.getTeam("factions").setSuffix(ChatColor.translateAlternateColorCodes('&', "&cMaintenance"));
				sb.getTeam("server1").setSuffix(ChatColor.translateAlternateColorCodes('&', " &cOffline"));
				sb.getTeam("server2").setSuffix(ChatColor.translateAlternateColorCodes('&', " &cOffline"));
				sb.getTeam("scrollinfo").setSuffix(ChatColor.translateAlternateColorCodes('&', "&dcolonymc.net"));
			}
		}
	}	
	
	public org.bukkit.scoreboard.Scoreboard scoreboardNormalCreate(Player p) {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("ColonyMC [Lobby]", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5&lColonyMC &7[Lobby]"));
		Team header = b.registerNewTeam("header");
		header.addEntry(ChatColor.translateAlternateColorCodes('&', "&f&m-+--------------+-"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&f&m-+--------------+-")).setScore(15);
		Team spacer1 = b.registerNewTeam("spacer");
		spacer1.addEntry(ChatColor.translateAlternateColorCodes('&', "&r "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r ")).setScore(14);
		Team pinfo = b.registerNewTeam("pinfo");
		pinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&5&lPlayer Info:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5&lPlayer Info:")).setScore(13);
		Team rank = b.registerNewTeam("rank");
		rank.addEntry(ChatColor.translateAlternateColorCodes('&', "&0"));
		rank.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fRank: &d&l"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&0")).setScore(12);
		Team money = b.registerNewTeam("ping");
		money.addEntry(ChatColor.translateAlternateColorCodes('&', "&1"));
		money.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fPing: &d"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(11);
		Team spacer2 = b.registerNewTeam("spacer2");
		spacer2.addEntry(ChatColor.translateAlternateColorCodes('&', "&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r")).setScore(10);
		Team sinfo = b.registerNewTeam("sinfo");
		sinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&5&lServer Info:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5&lServer Info:")).setScore(9);
		Team vp = b.registerNewTeam("factions");
		vp.addEntry(ChatColor.translateAlternateColorCodes('&', "&2"));
		vp.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fSkyblock: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(8);
		Team envoy = b.registerNewTeam("server1");
		envoy.addEntry(ChatColor.translateAlternateColorCodes('&', "&4"));
		envoy.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &f&kOOOOOOO:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&4")).setScore(7);
		Team koth = b.registerNewTeam("server2");
		koth.addEntry(ChatColor.translateAlternateColorCodes('&', "&5"));
		koth.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &f&kOOOOOOO:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5")).setScore(6);
		Team spacer3 = b.registerNewTeam("spacer3");
		spacer3.addEntry(ChatColor.translateAlternateColorCodes('&', " "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', " ")).setScore(5);
		Team scrollinfo = b.registerNewTeam("scrollinfo");
		scrollinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&6"));
		scrollinfo.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &dplay."));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&6")).setScore(4);
		Team footer = b.registerNewTeam("footer");
		footer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&f&m-+--------------+-"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&f&m-+--------------+-")).setScore(3);
		return b;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(Main.getInstance().getConfig().get("players." + p.getUniqueId().toString()) == null) {
			org.bukkit.scoreboard.Scoreboard b = scoreboardNormalCreate(p);
			p.setScoreboard(b);
		}
	}
}
