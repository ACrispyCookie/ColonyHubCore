package net.colonymc.colonyhubcore.fun.doublejump;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.colonymc.colonyhubcore.Main;
import net.colonymc.colonyspigotlib.lib.player.ColonyPlayer;
import net.colonymc.colonyhubcore.fun.battlebox.Fighter;

public class DoubleJumpListener implements Listener {
	
	final ArrayList<Player> onTheAirPlayers = new ArrayList<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setAllowFlight(true);
		p.setFlying(false);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(onTheAirPlayers.contains(e.getPlayer())) {
			onTheAirPlayers.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		if(e.getNewGameMode() == GameMode.SURVIVAL) {
			new BukkitRunnable() {
				@Override
				public void run() {
					p.setAllowFlight(true);
					p.setFlying(false);
				}
			}.runTaskLater(Main.getInstance(), 2);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
    public void attemptDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(onTheAirPlayers.contains(player) ||  player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || ColonyPlayer.getByPlayer(player).isFlying() || Fighter.getByPlayer(player) != null) {
            return;
        }
        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        Vector direction = player.getLocation().getDirection();
        direction.setY(1.4);
        player.setVelocity(direction);
        onTheAirPlayers.add(player);
        player.getLocation().getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES,0, 20);
        player.playSound(player.getLocation(), Sound.EXPLODE, 2, 1);
    }
    
    @EventHandler
    public void damageFall(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(!onTheAirPlayers.contains(event.getEntity()) || event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void refresh(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!onTheAirPlayers.contains(player)) {
            return;
        }
        Location belowPlayer = player.getLocation().subtract(0,0.1,0);
        Block block = belowPlayer.getBlock();
        if(block.isEmpty() || block.isLiquid()) {
            return;
        }
        if(isNonGroundMaterial(block.getType())) {
            return;
        }
        player.setAllowFlight(true);
        onTheAirPlayers.remove(player);
    }
    
    private boolean isNonGroundMaterial(Material type) {
        return type == Material.LADDER ||
                type == Material.VINE ||
                type == Material.LONG_GRASS ||
                type == Material.DOUBLE_PLANT ||
                type == Material.YELLOW_FLOWER ||
                type == Material.RED_ROSE ||
                type == Material.COBBLE_WALL ||
                type == Material.TORCH ||
                type == Material.WALL_BANNER ||
                type == Material.WALL_SIGN ||
                type.toString().contains("FENCE") || // Filters out all fences and gates
                type.toString().contains("DOOR"); // Filters out doors and trapdoors
    }
	
}
