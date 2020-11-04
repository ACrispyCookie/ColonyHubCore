package net.colonymc.colonyhubcore.util.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.itemstacks.NBTItems;
import net.colonymc.colonyhubcore.Main;

public class EnderButtListener implements Listener {
	
	public void setupEnderpearlRunnable(final Item item) {
	    (new BukkitRunnable() {
	        public void run() {
	          if (item.isDead()) {
	            cancel();
	          }
	          if (item.getVelocity().getX() == 0.0D || item.getVelocity().getY() == 0.0D || item.getVelocity().getZ() == 0.0D) {
	            Player player = (Player)item.getPassenger();
	            item.remove();
	            if (player != null) {
	              player.teleport(player.getLocation().add(0.0D, 0.5D, 0.0D));
	            }
	            cancel();
	          }
	          else if(item.getPassenger() == null) {
	              item.remove();
	              cancel();
	          }
	        
	        }
	      }).runTaskTimer(Main.getInstance(), 2L, 1L);
	  }
	  
	  @EventHandler
	  public void onPlayerInteract(PlayerInteractEvent event) {
	    Action action = event.getAction();
	    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
	      Player player = event.getPlayer();
	      ItemStack itemStack = player.getItemInHand();
	      if(itemStack.getType() == Material.ENDER_PEARL && NBTItems.hasTag(itemStack, "type") && NBTItems.getString(itemStack, "type").equals("pearl")) {
	        event.setCancelled(true);
	        event.setUseItemInHand(Event.Result.DENY);
	        event.setUseInteractedBlock(Event.Result.DENY);
        	if((player.getLocation().getY() <= 300 && player.getLocation().getY() >= 31) && (player.getLocation().getX() <= 200 && player.getLocation().getX() >= -200) && (player.getLocation().getZ() >= -200 && player.getLocation().getZ() <= 200)) {
                Item item = player.getWorld().dropItem(player.getLocation().add(0.0D, 0.5D, 0.0D), new ItemStack(Material.ENDER_PEARL, 16));
                item.setPickupDelay(10000);
                item.setVelocity(player.getLocation().getDirection().normalize().multiply(1.5F));
                item.setPassenger(player);
                player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2.0F, 1.5F);
                setupEnderpearlRunnable(item);
                player.updateInventory();
        	}
        	else {
                player.updateInventory();
        		player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou cannot use this here."));
        	}
	      } 
	    } 
	  }
	  
	  @EventHandler
	  public void onLeave(PlayerQuitEvent e) {
		  Player p = e.getPlayer();
		  if(p.getVehicle() != null && p.getVehicle().getType() == EntityType.DROPPED_ITEM) {
			  p.getVehicle().remove();
		  }
	  }

}
