package net.colonymc.hubcore.fun.battlebox;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.player.Title;
import net.colonymc.api.player.TitleAction;
import net.colonymc.hubcore.commands.SetupPlayer;

public class Team {

	ArrayList<Fighter> players = new ArrayList<Fighter>();
	Location loc;
	Color c;
	
	public Team(ArrayList<Fighter> players, Location loc, Color c) {
		for(Fighter p : players) {
			this.players.add(p);
		}
		this.loc = loc;
		this.c = c;
	}
	
	public void add(Fighter p) {
		players.add(p);
	}
	
	public void remove(Fighter p) {
		Fighter.fighters.remove(p);
		players.remove(p);
	}
	
	public void teleport() {
		int x = 0;
		int z = 0;
		for(Fighter p : players) {
			p.getPlayer().teleport(loc.clone().add(x, 0, z));
			if(x == 3) {
				x = 0;
				z++;
			}
			else {
				x++;
			}
		}
	}
	
	public void teleport(Player p) {
		p.getPlayer().teleport(loc.clone());
	}
	
	public void sendMessage(String s) {
		for(Fighter p : players) {
			p.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public void playSound(Sound s, float pitch) {
		for(Fighter p : players) {
			p.getPlayer().playSound(p.getPlayer().getLocation(), s, 2, pitch);
		}
	}
	
	public void sendTitle(String s, int fadeIn, int duration, int fadeOut) {
		for(Fighter p : players) {
			new Title(TitleAction.TITLE).duration(duration).fadeIn(fadeIn).fadeOut(fadeOut).text(s).send(p.getPlayer());
		}
	}
	
	public void sendSubtitle(String s, int fadeIn, int duration, int fadeOut) {
		for(Fighter p : players) {
			new Title(TitleAction.SUBTITLE).duration(duration).fadeIn(fadeIn).fadeOut(fadeOut).text(s).send(p.getPlayer());
		}
	}
	
	public void setupPlayers() {
		for(Fighter p : players) {
			Fighter.fighters.remove(p);
		}
		for(Fighter p : players) {
			SetupPlayer.setupPlayer(p.getPlayer());
		}
		players.clear();
	}
	
	public void hidePlayer(Player p) {
		for(Fighter pl : players) {
			pl.getPlayer().hidePlayer(p);
		}
	}
	
	public void showPlayer(Player p) {
		for(Fighter pl : players) {
			pl.getPlayer().showPlayer(p);
		}
	}
	
	public ItemStack[] getArmorContents() {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta(); 
		meta.setColor(c);
		meta.spigot().setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(meta);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		chestplate.setItemMeta(meta);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		leggings.setItemMeta(meta);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		boots.setItemMeta(meta);
		return new ItemStack[] {boots, leggings, chestplate, helmet};
	}
	
	public ItemStack[] getInventoryContents() {
		ItemStack sword = new ItemStackBuilder(Material.IRON_SWORD).unbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).build();
		ItemStack bow = new ItemStackBuilder(Material.BOW).unbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).build();
		ItemStack shears = new ItemStackBuilder(Material.SHEARS).unbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).build();
		ItemStack wool = new ItemStackBuilder(Material.WOOL).amount(16).durability((short) (c == Color.RED ? 14 : 11)).build();
		ItemStack arrows = new ItemStackBuilder(Material.ARROW).amount(16).build();
		return new ItemStack[] {sword, bow, wool, shears, arrows};
	}
	
	public ArrayList<Fighter> getFighters(){
		return players;
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public Color getColor() {
		return c;
	}
}
