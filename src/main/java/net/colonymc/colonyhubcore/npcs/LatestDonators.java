package net.colonymc.colonyhubcore.npcs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.colonymc.colonyspigotapi.api.holograms.PublicHologram;
import net.colonymc.colonyapi.database.MainDatabase;
import net.colonymc.colonyhubcore.Main;

public class LatestDonators implements Listener {

	final String noneTextures = "ewogICJ0aW1lc3RhbXAiIDogMTYwNTQ2Nzc0OTk4MSwKICAicHJvZmlsZUlkIiA6ICI2MDZlMmZmMGVkNzc0ODQyOWQ2Y2UxZDMzMjFjNzgzOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUXVlc3Rpb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZTA2M2NhZmI0NjdhNWM4ZGU0M2VjNzg2MTkzOTlmMzY5ZjRhNTI0MzRkYTgwMTdhOTgzY2RkOTI1MTZhMCIKICAgIH0KICB9Cn0=";
	final String noneSignature = "dHgbthHCLuigzTNiFFgsAKNVUBnAarJOl+a4vDZ73Gr3RZ/HAu8EGzCM4MfvBLg4ruxmlxzlUkZZw6r8vJdUiakYzuGLymlb/H4CTJznGcGEHYguZn4QQ4cdsm1cgHJ1xjq11HQS7CkH4FM8BowgZ1MLKXyC718sxrKod1GmD95h8G4a0qekFJlQWvifllMQo82JLyTBy60K1Gry9hPXiAOcxrhX30YWXfCmYX/WZ1iwO3A+ExCO4cbjI1i80CPIb2qAgmBmwoEEe0pPOz41Iue9nRMt+lcaGr+E+CGkEWzCh6dhybfshotEO7yhcCAwq3dBoS6WweeB3/pjH9ycujwROtrd/Y9Au3ApZelF3yT4iYUzqO3twW0z9ZxrMgW4mY/RksewqBnkLpuebw1b5WgHa9pNxjLtnVJ6WeQvkc5yzbSZzCmasa8XWySrIzrAHuE+3IMNzgeTLAT/KjZsr7hlZ+b/lxzLX+JOrIkJujPfiSTxgZmFVQlfexxgx4q5CnIFq35WTtAipVHCdpJ+PimlzfZW7vy5Coxexid32qVO3ryPR8evGE0qQup7ZNgQDQXLR79G06d4/64HT48dWTMj9wVZal+bCxb2fgFs7DKuU7mYsUfme/UR4rKGhcPHLgmpbE8oAzJX4xKQQhAn9RtB1mJXf0WRb3JRMh1N24Q=";
	NPC npc;
	final ArrayList<Donator> donators = new ArrayList<>();
	PublicHologram holo;
	BukkitTask update;
	BukkitTask updateLastLine;
	int currentIndex = 0;
	
	public void initialize() {
		ResultSet rs = MainDatabase.getResultSet("SELECT * FROM PlayerDonations ORDER BY timeDonated DESC LIMIT 5;");
		try {
			while(rs.next()) {
				donators.add(new Donator(MainDatabase.getName(rs.getString("uuid")), rs.getString("uuid"), rs.getString("packageName"), rs.getDouble("packagePrice"), rs.getLong("timeDonated")));
			}
			spawnNPC();
			holo = new PublicHologram(
					"&5&lLatest Donators\n" + getName(1, true) + "\n" + getName(2, false) + "\n" + getName(3, false) + "\n" + getName(4, false) + "\n" + getName(5, false) + "\n&fUpdates in &d10 &fseconds...\n&f(Click to get a link to the store!)", 
					new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("npcs.donator.world")), Main.getInstance().getConfig().getDouble("npcs.donator.x"), Main.getInstance().getConfig().getDouble("npcs.donator.y") + 2.85,
							Main.getInstance().getConfig().getDouble("npcs.donator.z")));
			holo.show();
			update = new BukkitRunnable() {
				@Override
				public void run() {
					next();
				}
			}.runTaskTimer(Main.getInstance(), 200, 200);
			updateLastLine = new BukkitRunnable() {
				int i = 10;
				@Override
				public void run() {
					holo.setLine(holo.getLineCount() - 2, "Updates in &d" + (i) + " &fseconds...");
					if(i == 1) {
						i = 10;
					}
					else {
						i--;
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		if(update != null) {
			update.cancel();
		}
		if(updateLastLine != null) {
			updateLastLine.cancel();
		}
		if(npc != null) {
			npc.despawn();
		}
		if(holo != null) {
			holo.destroy();
		}
	}
	
	private void next() {
		ArrayList<Donator> oldDonators = new ArrayList<>(donators);
		donators.clear();
		ResultSet rs = MainDatabase.getResultSet("SELECT * FROM PlayerDonations ORDER BY timeDonated DESC LIMIT 5;");
		try {
			while(rs.next()) {
				donators.add(new Donator(MainDatabase.getName(rs.getString("uuid")), rs.getString("uuid"), rs.getString("packageName"), rs.getDouble("packagePrice"), rs.getLong("timeDonated")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean shouldReset = false;
		if(donators.size() == donators.size()) {
			for(int i = 0; i < donators.size(); i++) {
				DonatorComparator comp = new DonatorComparator();
				if(comp.compare(donators.get(i), oldDonators.get(i)) == 0) {
					shouldReset = true;
					break;
				}
			}
		}
		else {
			shouldReset = true;
		}
		if(shouldReset) {
			currentIndex = 0;
			holo.setLine(1, getName(1, true));
			holo.setLine(2, getName(2, false));
			holo.setLine(3, getName(3, false));
			holo.setLine(4, getName(4, false));
			holo.setLine(5, getName(5, false));
		}
		else {
			if(currentIndex == 4) {
				changeNPC(0);
				holo.setLine(currentIndex + 1, getName(currentIndex + 1, false));
				holo.setLine(1, getName(1, true));
				currentIndex = 0;
			}
			else {
				changeNPC(currentIndex + 1);
				holo.setLine(currentIndex + 1, getName(currentIndex + 1, false));
				holo.setLine(currentIndex + 2, getName(currentIndex + 2, true));
				currentIndex++;
			}
		}
	}
	
	private void spawnNPC() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					npc = CitizensAPI.getNPCRegistry().getById(65);
					if(npc != null) {
						npc.spawn(new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("npcs.donator.world")), Main.getInstance().getConfig().getDouble("npcs.donator.x"), Main.getInstance().getConfig().getDouble("npcs.donator.y"),
								Main.getInstance().getConfig().getDouble("npcs.donator.z"), Main.getInstance().getConfig().getInt("npcs.donator.yaw"), Main.getInstance().getConfig().getInt("npcs.donator.pitch")));

						changeNPC(0);
						cancel();
					}
				} catch(IllegalStateException ignored) {
					
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 10L);
	}
	
	private void changeNPC(int index) {
		if(npc != null) {
			if(index < donators.size() && donators.get(index) != null) {
				npc.setName(ChatColor.translateAlternateColorCodes('&', "&d" + donators.get(index).getPlayerName()));
				npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, donators.get(index).getPlayerName());
				npc.data().setPersistent("cached-skin-uuid-name", donators.get(index).getPlayerName());
				npc.data().setPersistent("cached-skin-uuid", donators.get(index).getPlayerUuid());
			}
			else {
				npc.setName(ChatColor.translateAlternateColorCodes('&', "&7None"));
				npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, "MHF_Question");
				npc.data().setPersistent("cached-skin-uuid-name", "MHF_Question");
				npc.data().setPersistent("cached-skin-uuid", "606e2ff0-ed77-4842-9d6c-e1d3321c7838");
			}
			if(npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity){
				Skin.get((SkinnableEntity) npc.getEntity()).applyAndRespawn((SkinnableEntity) npc.getEntity());
			}
		}
	}
	
	private String getName(int lineIndex, boolean selected) {
		if(lineIndex == 0) {
			return ChatColor.translateAlternateColorCodes('&', "&5&lLatest Donators");
		}
		else if(lineIndex <= 5) {
			if(lineIndex - 1 < donators.size()) {
				if(selected) {
					return ChatColor.translateAlternateColorCodes('&', "&l#" + (lineIndex) + " - &d&l" + donators.get(lineIndex - 1).getPlayerName() + ": " + donators.get(lineIndex - 1).getPackageName());
				}
				else {
					return ChatColor.translateAlternateColorCodes('&', "#" + (lineIndex) + " - &d" + donators.get(lineIndex - 1).getPlayerName() + ": " + donators.get(lineIndex - 1).getPackageName());
				}
			}
			else {
				if(selected) {
					return ChatColor.translateAlternateColorCodes('&', "&l#" + (lineIndex) + " - &7&lNone");
				}
				else {
					return ChatColor.translateAlternateColorCodes('&', "#" + (lineIndex) + " - &7None");
				}
			}
		}
		else {
			if(lineIndex == 6) {
				return ChatColor.translateAlternateColorCodes('&', "Updates in &d10 &fseconds...");
			}
			else {
				return ChatColor.translateAlternateColorCodes('&', "&f(Click to get a link to the voting sites!)");
			}
		}
	}
	
	@EventHandler
	public void onClick(NPCRightClickEvent e) {
		if(e.getNPC().equals(npc)) {
	        e.getClicker().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can check our store @ &dhttps://store.colonymc.net"
	        		+ "\n &5&l» &f(Donating helps our network cover its cost and gives you awesome rewards!)"));
		}
	}
	
	@EventHandler
	public void onClick(NPCLeftClickEvent e) {
		if(e.getNPC().equals(npc)) {
	        e.getClicker().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou can check our store @ &dhttps://store.colonymc.net"
	        		+ "\n &5&l» &f(Donating helps our network cover its cost and gives you awesome rewards!)"));
		}
	}
}
