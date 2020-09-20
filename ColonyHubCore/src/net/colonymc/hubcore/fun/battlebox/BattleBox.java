package net.colonymc.hubcore.fun.battlebox;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.player.ColonyPlayer;
import net.colonymc.api.player.Title;
import net.colonymc.api.player.TitleAction;
import net.colonymc.hubcore.Main;
import net.colonymc.hubcore.commands.SetupPlayer;
import net.colonymc.hubcore.fun.pvpmode.PvpMode;
import net.colonymc.hubcore.scoreboard.BattleBoxBoard;

public class BattleBox implements Listener {
	
	ArrayList<Team> teams = new ArrayList<Team>();
	ArrayList<Block> blocks = new ArrayList<Block>();
	HashMap<Team, Integer> points = new HashMap<Team, Integer>();
	ArrayList<Projectile> thrown = new ArrayList<Projectile>();
	int round = 0;
	int startingIn = 15;
	int ticksLeft = 12000;
	BukkitTask run;
	BukkitTask countdownToStart;
	boolean isCountingDown = false;
	boolean isDraw = false;
	boolean isJoinable = false;
	boolean hasStarted = false;
	boolean isRoundRunning = false;
	boolean hasEnded = false;
	Team winner;
	
	public BattleBox() {
		teams.add(new Team(new ArrayList<Fighter>(), new Location(Bukkit.getWorld("battlebox"), -11, 5, 4, 180, 0), Color.RED));
		teams.add(new Team(new ArrayList<Fighter>(), new Location(Bukkit.getWorld("battlebox"), -8, 5, -16, 0, 0), Color.BLUE));
		points.put(teams.get(0), 0);
		points.put(teams.get(1), 0);
	}
	
	public void addPlayerToGame(Player p) {
		if(!hasStarted) {
			if(PvpMode.isPvping(p)) {
				Main.getPvpInstance().disablePvpMode(p);
			}
			Team t = getRandomTeam();
			t.add(new Fighter(p, t, false));
			p.setGameMode(GameMode.SURVIVAL);
			p.getInventory().clear();
			p.getOpenInventory().getTopInventory().clear();
			p.setItemOnCursor(new ItemStack(Material.AIR));
			p.setScoreboard(new BattleBoxBoard().scoreboardNormalCreate(p));
			setInventory(p);
			t.teleport(p);
			for(Team te : teams) {
				te.sendMessage(" &5&l» &d" + p.getName() + " &fhas joined the game!");
			}
			checkToStart();
		}
	}
	
	public void start() {
		run = new BukkitRunnable() {
			@Override
			public void run() {
				if(ticksLeft == 0) {
					if(points.get(teams.get(0)) == points.get(teams.get(1))) {
						endRound(null);
					}
					else {
						endRound(points.get(teams.get(0)) > points.get(teams.get(1)) ? teams.get(0) : teams.get(1));
					}
					cancel();
				}
				else if(ticksLeft == 12000) {
					for(Team t : teams) {
						t.sendTitle("&c&lFIGHT!", 10, 40, 10);
						t.playSound(Sound.ENDERDRAGON_GROWL, 1);
					}
					ticksLeft--;
				}
				else {
					if(teams.get(0).getFighters().size() == 0 || teams.get(1).getFighters().size() == 0) {
						round = 4;
						end((teams.get(0).getFighters().size() == 0 ? teams.get(1) : teams.get(0)));
					}
					ticksLeft--;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void startNextRound() {
		if(round == 0) {
			addBlocks(true);
		}
		if(round == 0) {
			hasStarted = true;
			isJoinable = false;
		}
		new BukkitRunnable() {
			int ticksLeft = 10;
			@Override
			public void run() {
				if(ticksLeft == 0) {
					start();
					isRoundRunning = true;
					cancel();
				}
				else {
					ticksLeft--;
					for(Team t : teams) {
						t.playSound(Sound.CLICK, 1);
						t.sendMessage(" &5&l» &fRound " + (round + 1) + " starting in &d" + (ticksLeft + 1) + " &fseconds!");
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
	public void end(Team winner) {
		if(round == 4) {
			points.put(winner, points.get(winner) + 100 + getExtraPoints(winner));
			this.winner = winner;
			for(Team t : teams) {
				t.teleport();
				for(Fighter f : t.getFighters()) {
					Player p = f.getPlayer();
					p.setHealth(20);
					p.setItemOnCursor(new ItemStack(Material.AIR));
					p.getOpenInventory().getTopInventory().clear();
					p.getInventory().clear();
				}
			}
			clearArena();
			run.cancel();
			removeAllPlayers();
		}
	}
	
	public void endRound(Team winner) {
		isRoundRunning = false;
		ticksLeft = 12000;
		run.cancel();
		round++;
		for(Team t : teams) {
			for(Fighter p : t.getFighters()) {
				if(p.isDead()) {
					removeDeadSpectator(p.getPlayer());
				}
				else {
					p.getPlayer().setHealth(20);
					setInventory(p.getPlayer());
				}
			}
		}
		for(Team t : teams) {
			t.teleport();
		}
		clearArena();
		new BukkitRunnable() {
			int c = 0;
			@Override
			public void run() {
				if(c == 0) {
					if(winner != null) {
						for(Team t : teams) {
							if(winner.equals(t)) {
								int pointsAdded = 100 + getExtraPoints(winner);
								points.put(winner, points.get(winner) + pointsAdded);
								t.sendTitle("&a&l+" + (pointsAdded) + " points", 10, 80, 10);
								t.sendSubtitle("&fYou have won this round!", 10, 80, 10);
								t.playSound(Sound.LEVEL_UP, 1);
							}
							else {
								t.sendTitle("&c&l+0 points", 10, 80, 10);
								t.sendSubtitle("&fYou have lost this round!", 10, 80, 10);
								t.playSound(Sound.GHAST_SCREAM, 1);
							}
						}
					}
					else {
						for(Team t : teams) {
							t.sendTitle("&6&l+0 points", 10, 80, 10);
							t.sendSubtitle("&fYou have ran out of time!", 10, 80, 10);
							t.playSound(Sound.GHAST_SCREAM, 1);
						}
					}
				}
				else if(c == 80) {
					startNextRound();
					cancel();
				}
				c++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		
	}
	
	public void killPlayer(Player p, Player killer) {
		Fighter.getByPlayer(p).setDead(true);
		addDeadSpectator(p);
		p.playSound(p.getLocation(), Sound.BAT_DEATH, 2, 1);
		new Title(TitleAction.TITLE).text("&c&lYOU DIED!").fadeIn(2).duration(40).fadeOut(2).send(p);
		new Title(TitleAction.SUBTITLE).text("&cYou will respawn in the next round!").fadeIn(2).duration(40).fadeOut(2).send(p);
		if(killer != null) {
			Fighter.getByPlayer(killer).addKill();
			points.put(Fighter.getByPlayer(killer).getTeam(), points.get(Fighter.getByPlayer(killer).getTeam()) + 10);
			killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 2, 1);
			killer.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou killed &d" + p.getName() + "&f!"));
		}
		for(Team t : teams) {
			if(killer != null) {
				t.sendMessage(" &5&l» &d" + p.getName() + " &fwas killed by &d" + killer.getName() + "&f!");
			}
			else {
				t.sendMessage(" &5&l» &d" + p.getName() + " &fdied!");
			}
		}
	}
	
	public void addDeadSpectator(Player p) {
		if(!ColonyPlayer.getByPlayer(p).isFlying()) {
			ColonyPlayer.getByPlayer(p).togglePlayerFlight();
		}
		p.setFlying(true);
		p.teleport(blocks.get(0).getLocation().add(0, 10, 0));
		p.setHealth(20);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, true, true), true);
		p.getInventory().clear();
		p.setItemOnCursor(new ItemStack(Material.AIR));
		p.getOpenInventory().getTopInventory().clear();
		for(Team t : teams) {
			t.hidePlayer(p);
		}
	}
	
	public void removeDeadSpectator(Player p) {
		respawnPlayer(p);
		for(Team t : teams) {
			t.showPlayer(p);
		}
		Fighter.getByPlayer(p).setDead(false);
	}
	
	public void removeAllPlayers() {
		for(Team t : teams) {
			for(Fighter f : t.getFighters()) {
				if(f.isDead()) {
					respawnPlayer(f.getPlayer());
				}
			}
		}
		new BukkitRunnable() {
			int c = 0;
			@Override
			public void run() {
				if(c == 200) {
					for(Team t : teams) {
						t.setupPlayers();
					}
					hasEnded = false;
					isRoundRunning = false;
					hasStarted = false;
					isJoinable = false;
					blocks.clear();
					points.put(teams.get(0), 0);
					points.put(teams.get(1), 0);
					round = 0;
					ticksLeft = 12000;
					startingIn = 15;
					winner = null;
					cancel();
				}
				else if(c == 0) {
					hasEnded = true;
					if(winner != null) {
						for(Team t : teams) {
							if(winner.equals(t)) {
								t.sendTitle("&a&lVICTORY!", 10, 200, 10);
								t.sendSubtitle("&fYou have won this game!", 10, 200, 10);
								t.playSound(Sound.LEVEL_UP, 1);
							}
							else {
								t.sendTitle("&c&lDEFEAT!", 10, 200, 10);
								t.sendSubtitle("&fYou have lost this game!", 10, 200, 10);
								t.playSound(Sound.GHAST_SCREAM, 1);
							}
						}
					}
					else {
						for(Team t : teams) {
							t.sendTitle("&6&lDRAW!", 10, 200, 10);
							t.sendSubtitle("&fYou have ran out of time!", 10, 200, 10);
							t.playSound(Sound.GHAST_SCREAM, 1);
						}
					}
				}
				c++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void removePlayer(Player p) {
		Fighter f = Fighter.getByPlayer(p);
		if(f.isDead()) {
			for(Team t : teams) {
				t.showPlayer(f.getPlayer());
			}
		}
		f.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
		f.getPlayer().setGameMode(GameMode.ADVENTURE);
		if(ColonyPlayer.getByPlayer(f.getPlayer()).isFlying()) {
			ColonyPlayer.getByPlayer(f.getPlayer()).togglePlayerFlight();
		}
		f.getTeam().remove(Fighter.getByPlayer(p));
		for(Team t : teams) {
			t.sendMessage(" &5&l» &fThe player &d" + p.getName() + " &fhas left the game!");
		}
		SetupPlayer.setupPlayer(p);
	}
	
	public void setJoinable(boolean isJoinable) {
		this.isJoinable = isJoinable;
	}
	
	public boolean isJoinable() {
		return isJoinable;
	}
	
	public boolean hasStarted() {
		return hasStarted;
	}
	
	public boolean hasRoundStarted() {
		return isRoundRunning;
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	public ArrayList<Team> getTeams() {
		return teams;
	}
	
	public int getTimeLeft() {
		return ticksLeft/20;
	}
	
	public int getStartingIn() {
		return startingIn;
	}
	
	public String getStatus() {
		if(hasStarted) {
			return "Running";
		}
		else if(hasEnded) {
			return "Ended";
		}
		else if(isCountingDown) {
			return "Starting in " + startingIn;
		}
		else {
			return "Waiting";
		}
	}
	
	public int getPoints(Team t) {
		return points.get(t);
	}
	
	public int getKills(Fighter f) {
		return f.getKills();
	}
	
	private void clearArena() {
		for(Projectile p : thrown) {
			p.remove();
		}
		addBlocks(false);
	}
	
	private int getExtraPoints(Team t) {
		int extra = 0;
		for(Fighter f : t.getFighters()) {
			if(!f.isDead()) {
				extra = extra + 20;
			}
		}
		return extra;
	}
	
	private void respawnPlayer(Player p) {
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setHealth(20);
		if(ColonyPlayer.getByPlayer(p).isFlying()) {
			ColonyPlayer.getByPlayer(p).togglePlayerFlight();
		}
		for(Team t : teams) {
			t.showPlayer(p);
		}
		setInventory(p.getPlayer());
	}
	
	@SuppressWarnings("deprecation")
	private void checkForWin() {
		Team winner = null;
		for(Block b : blocks) {
			if(b.getData() != 14 && b.getData() != 11) {
				return;
			}
		}
		winner = blocks.get(0).getData() == 14 ? teams.get(0) : teams.get(1);
		endRound(winner);
	}
	
	private void setInventory(Player p) {
		Fighter f = Fighter.getByPlayer(p);
		p.getInventory().clear();
		p.getOpenInventory().getTopInventory().clear();
		p.setItemOnCursor(new ItemStack(Material.AIR));
		p.getInventory().setArmorContents(f.getTeam().getArmorContents());
		p.getInventory().setContents(f.getTeam().getInventoryContents());
		p.updateInventory();
	}
	
	private Team getRandomTeam() {
		if(teams.get(0).getFighters().size() % 2 == 0 && teams.get(1).getFighters().size() % 2 == 1) {
			if(teams.get(1).getFighters().size() - teams.get(0).getFighters().size() >= 1) {
				return teams.get(0);
			}
			else {
				return teams.get(1);
			}
		}
		else if(teams.get(0).getFighters().size() % 2 == 1 && teams.get(1).getFighters().size() % 2 == 0) {
			if(teams.get(0).getFighters().size() - teams.get(1).getFighters().size() >= 1) {
				return teams.get(1);
			}
			else {
				return teams.get(0);
			}
		}
		else {
			return (teams.get(0).getFighters().size() > teams.get(1).getFighters().size() ? teams.get(1) : teams.get(0));
		}
	}
	
	private void checkToStart() {
		int c = 0;
		for(Team t : teams) {
			c = c + t.getFighters().size();
		}
		if(c >= 2) {
			countdownToStart = new BukkitRunnable() {
				@Override
				public void run() {
					if(startingIn == 0) {
						isCountingDown = false;
						startNextRound();
						cancel();
					}
					else {
						isCountingDown = true;
						startingIn--;
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
	}
	
	private void addBlocks(boolean shouldAdd) {
		for(int x = 0; x < 3; x++) {
			for(int z = 0; z < 3; z++) {
				Block b = new Location(Bukkit.getWorld("battlebox"), -10, 3, -7).add(x, 0, z).getBlock();
				b.setType(Material.WOOL);
				if(shouldAdd) {
					blocks.add(b);
				}
			}
		}
	}
	
	@EventHandler
	public void onShoot(ProjectileLaunchEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			if(Fighter.getByPlayer(p) != null) {
				Main.getInstance().getBox().thrown.add(e.getEntity());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(Fighter.getByPlayer(p) != null) {
				if(e.getDamager() instanceof Player) {
					Player damager = (Player) e.getDamager();
					if(Fighter.getByPlayer(damager) != null) {
						if(Fighter.getByPlayer(damager).isDead() || Fighter.getByPlayer(p).getTeam().equals(Fighter.getByPlayer(damager).getTeam()) || !Main.getInstance().getBox().hasStarted() || Main.getInstance().getBox().hasEnded() || !Main.getInstance().getBox().hasRoundStarted()) {
							e.setCancelled(true);
						}
						else {
							e.setCancelled(false);
							if(e.getFinalDamage() > p.getHealth()) {
								e.setCancelled(true);
								Main.getInstance().getBox().killPlayer(p, (e.getDamager() instanceof Player ? (Player) e.getDamager() : null));
							}
						}
					}
					else {
						e.setCancelled(true);
					}
				}
				else if(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
					if(Fighter.getByPlayer(damager) != null) {
						if(Fighter.getByPlayer(damager).isDead() || Fighter.getByPlayer(p).getTeam().equals(Fighter.getByPlayer(damager).getTeam()) || !Main.getInstance().getBox().hasStarted() || Main.getInstance().getBox().hasEnded() || !Main.getInstance().getBox().hasRoundStarted()) {
							e.setCancelled(true);
						}
						else {
							e.setCancelled(false);
							if(e.getFinalDamage() > p.getHealth()) {
								e.setCancelled(true);
								Main.getInstance().getBox().killPlayer(p, damager);
							}
						}
					}
					else {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(Fighter.getByPlayer(p) != null) {
			Main.getInstance().getBox().removePlayer(p);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		if(Main.getInstance().getBox().hasStarted() || Main.getInstance().getBox().isCountingDown) {
			Player p = e.getPlayer();
			if(Fighter.getByPlayer(p) != null && !Fighter.getByPlayer(p).isDead()) {
				if(Main.getInstance().getBox().hasRoundStarted()) {
					if(Main.getInstance().getBox().blocks.contains(e.getBlock())) {
						e.setCancelled(false);
						Main.getInstance().getBox().checkForWin();
					}
					else {
						e.setCancelled(true);
					}
				}
				else {
					e.setCancelled(true);
				}
			}
			else if(Fighter.getByPlayer(p) != null && Fighter.getByPlayer(p).isDead()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		if(e.getFrom().equals(Bukkit.getWorld("battlebox")) && Fighter.getByPlayer(p) != null) {
			Main.getInstance().getBox().removePlayer(p);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(Fighter.getByPlayer(p) != null && !Fighter.getByPlayer(p).isDead()) {
			if(Main.getInstance().getBox().hasRoundStarted()) {
				if(!Main.getInstance().getBox().blocks.contains(e.getBlock())) {
					e.setCancelled(true);
				}
				else {
					e.getBlock().setType(Material.AIR);
					e.setCancelled(false);
				}
			}
			else {
				e.setCancelled(true);
			}
		}
		else if(Fighter.getByPlayer(p) != null && Fighter.getByPlayer(p).isDead()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(Fighter.getByPlayer(p) != null && !Fighter.getByPlayer(p).isDead()) {
			e.setCancelled(false);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryChange(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(Fighter.getByPlayer(p) != null && !Fighter.getByPlayer(p).isDead()) {
			if(e.getSlot() != 36 && e.getSlot() != 37 && e.getSlot() != 38 && e.getSlot() != 39) {
				e.setCancelled(false);
			}
			else {
				e.setCancelled(true);
			}
		}
		else if(Fighter.getByPlayer(p) != null && Fighter.getByPlayer(p).isDead()) {
			e.setCancelled(true);
		}
	}

}
