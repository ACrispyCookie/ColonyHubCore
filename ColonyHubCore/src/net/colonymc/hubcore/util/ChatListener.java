package net.colonymc.hubcore.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import net.colonymc.hubcore.Main;
import net.colonymc.hubcore.fun.battlebox.Fighter;
import net.colonymc.hubcore.fun.battlebox.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatListener implements Listener {
	
	@EventHandler
	public void chatFormatter(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(Fighter.getByPlayer(p) != null) {
			Fighter f = Fighter.getByPlayer(p);
			if(!e.isCancelled()) {
				ArrayList<Player> players = new ArrayList<Player>();
				for(Team t : Main.getInstance().getBox().getTeams()) {
					for(Fighter fi : t.getFighters()) {
						players.add(fi.getPlayer());
					}
				}
				e.getRecipients().clear();
				TextComponent name = new TextComponent((f.getTeam().getColor() == Color.BLUE ? ChatColor.BLUE + "[BLUE] " + f.getPlayer().getName() : ChatColor.RED + "[RED] " + f.getPlayer().getName()));
				TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', ": " + ChatColor.WHITE + e.getMessage()));
				TextComponent finalmsg = new TextComponent("");
				finalmsg.addExtra(name);
				finalmsg.addExtra(msg);
				for(Player pl : players) {
					pl.spigot().sendMessage(finalmsg);
				}
			}
		}
		else {
			if(!e.isCancelled()) {
				ArrayList<Player> players = new ArrayList<Player>();
				for(Player pl : e.getRecipients()) {
					if(Fighter.getByPlayer(pl) == null) {
						players.add(pl);
					}
				}
				e.getRecipients().clear();
				TextComponent c = null;
				if(e.getPlayer().hasPermission("prince.store")) {
					c = new TextComponent(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, "%vault_prefix%")));
					c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/store"));
					c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fClick here to get a link to our &dstore!"))}));
				}
				TextComponent name;
				if(p.hasPermission("prince.store")) {
					name = new TextComponent(p.getName());
				}
				else {
					name = new TextComponent(ChatColor.GRAY + p.getName());
				}
				name.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));
				name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fClick here to message &d" + p.getName()))}));
				ArrayList<TextComponent> msg = new ArrayList<TextComponent>();
				String[] words = e.getMessage().split(" ");
				for(String s : words) {
					if((s.contains("https://") || s.contains("http://")) && s.contains(".")) {
						TextComponent t = new TextComponent((p.hasPermission("prince.store") ? "" : ChatColor.GRAY) + s + " ");
						t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, s));
						msg.add(t);
					}
					else {
						msg.add(new TextComponent((p.hasPermission("prince.store") ? "" : ChatColor.GRAY)  + s + " "));
					}
				}
				TextComponent finalmsg = new TextComponent("");
				if(c != null) {
					finalmsg.addExtra(c);
				}
				finalmsg.addExtra(name);
				finalmsg.addExtra(ChatColor.translateAlternateColorCodes('&', " &8» " + ChatColor.getByChar(COLOR.getByPlayer(p))));
				for(TextComponent t : msg) {
					finalmsg.addExtra(t);
				}
				for(Player pl : players) {
					pl.spigot().sendMessage(finalmsg);
				}
			}
		}
	}
	
	enum COLOR {
		ADMIN('c'),
		COLONY('5');
		
		char color;
		
		COLOR(char c){
			color = c;
		}
		
		public static char getByPlayer(Player p) {
			if(p.hasPermission("*")) {
				return ADMIN.color;
			}
			else if(p.hasPermission("colony.store")){
				return COLONY.color;
			}
			return '7';
		}
	}

}
