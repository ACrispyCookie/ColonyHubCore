package net.colonymc.hubcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import net.colonymc.hubcore.Main;
import net.colonymc.hubcore.fun.battlebox.Fighter;

public class BattleBoxBoard {
	
	public static void linesUpdate(Player p) {
		org.bukkit.scoreboard.Scoreboard sb = p.getScoreboard();
		if(sb.getObjective("BattleBox").getDisplayName() != null) {
			String dn = sb.getObjective("BattleBox").getDisplayName();
			if(dn.equals(ChatColor.translateAlternateColorCodes('&', "&5&lColonyMC &7[BattleBox]"))){
				sb.getTeam("timeLeft").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + getFormatted(Main.getInstance().getBox().getTimeLeft())));
				sb.getTeam("status").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + Main.getInstance().getBox().getStatus()));
				sb.getTeam("points").setSuffix(ChatColor.translateAlternateColorCodes('&', "&fs: &d" + Main.getInstance().getBox().getPoints(Fighter.getByPlayer(p).getTeam())));
				sb.getTeam("kills").setSuffix(ChatColor.translateAlternateColorCodes('&', "&f: &d" + Fighter.getByPlayer(p).getKills()));
				sb.getTeam("opp").setSuffix(ChatColor.translateAlternateColorCodes('&', "&fleft: &d" + getLeft(Fighter.getByPlayer(p).getTeam())));
				sb.getTeam("opppoints").setSuffix(ChatColor.translateAlternateColorCodes('&', "&fts: &d" + getOppPoints(Fighter.getByPlayer(p).getTeam())));
				sb.getTeam("scrollinfo").setSuffix(ChatColor.translateAlternateColorCodes('&', "&dcolonymc.net"));
			}
		}
	}	
	
	public org.bukkit.scoreboard.Scoreboard scoreboardNormalCreate(Player p) {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("BattleBox", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5&lColonyMC &7[BattleBox]"));
		Team header = b.registerNewTeam("header");
		header.addEntry(ChatColor.translateAlternateColorCodes('&', "&f&m-+--------------+-"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&f&m-+--------------+-")).setScore(15);
		Team spacer4 = b.registerNewTeam("spacer4");
		spacer4.addEntry(ChatColor.translateAlternateColorCodes('&', "&f"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&f")).setScore(14);
		Team status = b.registerNewTeam("status");
		status.addEntry(ChatColor.translateAlternateColorCodes('&', "&6"));
		status.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fStatus: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&6")).setScore(13);
		Team spacer1 = b.registerNewTeam("spacer");
		spacer1.addEntry(ChatColor.translateAlternateColorCodes('&', "&r "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r ")).setScore(12);
		Team pinfo = b.registerNewTeam("pinfo");
		pinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&5&lTime left:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5&lTime left:")).setScore(11);
		Team rank = b.registerNewTeam("timeLeft");
		rank.addEntry(ChatColor.translateAlternateColorCodes('&', "&0"));
		rank.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&0")).setScore(10);
		Team spacer3 = b.registerNewTeam("spacer3");
		spacer3.addEntry(ChatColor.translateAlternateColorCodes('&', " "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', " ")).setScore(9);
		Team money = b.registerNewTeam("points");
		money.addEntry(ChatColor.translateAlternateColorCodes('&', "&1"));
		money.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fYour point"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(8);
		Team vp = b.registerNewTeam("kills");
		vp.addEntry(ChatColor.translateAlternateColorCodes('&', "&2"));
		vp.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fYour kills"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(7);
		Team spacer2 = b.registerNewTeam("spacer2");
		spacer2.addEntry(ChatColor.translateAlternateColorCodes('&', "&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r")).setScore(6);
		Team oppp = b.registerNewTeam("opppoints");
		oppp.addEntry(ChatColor.translateAlternateColorCodes('&', "&3"));
		oppp.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fTheir poin"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&3")).setScore(5);
		Team envoy = b.registerNewTeam("opp");
		envoy.addEntry(ChatColor.translateAlternateColorCodes('&', "&4"));
		envoy.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fOpponents "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&4")).setScore(4);
		Team spacer = b.registerNewTeam("spacer1");
		spacer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r")).setScore(3);
		Team scrollinfo = b.registerNewTeam("scrollinfo");
		scrollinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&5"));
		scrollinfo.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &dplay."));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5")).setScore(2);
		Team footer = b.registerNewTeam("footer");
		footer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&f&m-+--------------+-"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&f&m-+--------------+-")).setScore(1);
		return b;
	}
	
	private static String getFormatted(int seconds) {
		String s = "";
		int minutes = seconds/60;
		int secs = seconds - minutes * 60;
		s = (minutes) + "m ";
		s = s + (secs) + "s";
		return s;
	}
	
	private static String getLeft(net.colonymc.hubcore.fun.battlebox.Team t) {
		net.colonymc.hubcore.fun.battlebox.Team toCount = Main.getInstance().getBox().getTeams().get(0).equals(t) ? Main.getInstance().getBox().getTeams().get(1) : Main.getInstance().getBox().getTeams().get(0);
		int c = 0;
		for(Fighter f : toCount.getFighters()) {
			if(!f.isDead()) {
				c++;
			}
		}
		return String.valueOf(c);
	}
	
	private static String getOppPoints(net.colonymc.hubcore.fun.battlebox.Team t) {
		net.colonymc.hubcore.fun.battlebox.Team toCount = Main.getInstance().getBox().getTeams().get(0).equals(t) ? Main.getInstance().getBox().getTeams().get(1) : Main.getInstance().getBox().getTeams().get(0);
		return String.valueOf(Main.getInstance().getBox().getPoints(toCount));
	}

}
