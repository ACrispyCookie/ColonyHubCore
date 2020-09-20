package net.colonymc.hubcore;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholders extends PlaceholderExpansion {
	
	Plugin plugin;
	
	public Placeholders(Plugin main) {
		this.plugin = main;
	}
	
	@Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if(p == null) {
            return "";
        }
        if(identifier.equals("rank_prefix")) {
        	if(PlaceholderAPI.setPlaceholders(p, "%vault_group%").equalsIgnoreCase("knight")) {
        		return "&7[Knight]";
        	}
        	else {
            	String s = PlaceholderAPI.setPlaceholders(p, "%vault_prefix%");
            	return s.substring(0, s.length() - 5);
        	}
        }
        return null;
    }
	
	@Override
    public boolean persist(){
        return true;
    }
	
	@Override
    public boolean canRegister(){
        return true;
    }

	@Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

	@Override
    public String getIdentifier(){
        return "colonymc";
    }

	@Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

}
