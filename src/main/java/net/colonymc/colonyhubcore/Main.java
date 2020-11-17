package net.colonymc.colonyhubcore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.colonymc.colonyhubcore.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyspigotapi.player.PublicHologram;
import net.colonymc.colonyapi.database.MainDatabase;
import net.colonymc.colonyhubcore.commands.AboutCommand;
import net.colonymc.colonyhubcore.commands.BuilderModeCommand;
import net.colonymc.colonyhubcore.commands.PluginCommand;
import net.colonymc.colonyhubcore.commands.PvPModeCommand;
import net.colonymc.colonyhubcore.commands.SetupPlayer;
import net.colonymc.colonyhubcore.commands.SpawnCommand;
import net.colonymc.colonyhubcore.fun.battlebox.BattleBox;
import net.colonymc.colonyhubcore.fun.battlebox.BattleBoxCommand;
import net.colonymc.colonyhubcore.fun.commands.CookieCommand;
import net.colonymc.colonyhubcore.fun.commands.KaboomCommand;
import net.colonymc.colonyhubcore.fun.doublejump.DoubleJumpListener;
import net.colonymc.colonyhubcore.fun.pvpmode.PvpMode;
import net.colonymc.colonyhubcore.menus.HelpCommandsMenu;
import net.colonymc.colonyhubcore.menus.HelpfulMenu;
import net.colonymc.colonyhubcore.menus.ServerSelector;
import net.colonymc.colonyhubcore.npcs.LatestDonators;
import net.colonymc.colonyhubcore.npcs.LatestVoters;
import net.colonymc.colonyhubcore.pms.MessageCommand;
import net.colonymc.colonyhubcore.pms.MessageListeners;
import net.colonymc.colonyhubcore.pms.ReplyCommand;
import net.colonymc.colonyhubcore.util.ChatListener;
import net.colonymc.colonyhubcore.util.InteractionListeners;
import net.colonymc.colonyhubcore.util.JoinListener;
import net.colonymc.colonyhubcore.util.LeaveListener;
import net.colonymc.colonyhubcore.util.PortalListener;
import net.colonymc.colonyhubcore.util.items.EnderButtListener;
import net.colonymc.colonyhubcore.util.items.VisibilityListener;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class Main extends JavaPlugin {
	
	static Main instance;
	private Location spawn;
	private static final PvpMode pvpInstance = new PvpMode();
	private final BattleBox box = new BattleBox();
	private LatestDonators donatorsInstance;
	private LatestVoters votersInstance;
	private final File configFile = new File(this.getDataFolder(), "config.yml");
	private final FileConfiguration config = new YamlConfiguration();
	private final ArrayList<PublicHologram> publicHolos = new ArrayList<>();
	boolean started = false;
	
	public void onEnable() {
		instance = this;
		if(MainDatabase.isConnected()) {
			ArrayList<String> pluginNames = new ArrayList<>();
			for(Plugin pl : Bukkit.getPluginManager().getPlugins()) {
				pluginNames.add(pl.getName());
			}
			if(!pluginNames.contains("ColonySpigotAPI") || !pluginNames.contains("PlaceholderAPI") || !pluginNames.contains("Citizens")) {
				System.out.println(" » The plugin is missing some dependencies! It won't enable!");
				Bukkit.getPluginManager().disablePlugin(Main.this);
				return;
			}
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				new Placeholders(this).register();
	        }
			setupConfigs();
			setupSpawn();
			setupConditions();
			startNPCs();
			initializeCommands();
			initializeListeners();
			started = true;
			System.out.println(" » ColonyHubCore has been sucessfully enabled!");
		}
		else {
			System.out.println(" » ColonyHubCore couldn't connect to the main database!");
		}
	}

	public void onDisable() {
		if(started) {
			ArrayList<String> pluginNames = new ArrayList<>();
			for(Plugin pl : Bukkit.getPluginManager().getPlugins()) {
				pluginNames.add(pl.getName());
			}
			if(!pluginNames.contains("ColonySpigotAPI") || !pluginNames.contains("PlaceholderAPI") || !pluginNames.contains("Citizens")) {
				System.out.println(" » ColonyHubCore has been sucessfully disabled!");
				return;
			}
			stopNPCs();
			disablePvpPlayers();
			disableBuilderPlayers();
			hidePublicHolograms();
		}
		System.out.println(" » ColonyHubCore has been sucessfully disabled!");
	}
	
	private void setupConditions() {
		Bukkit.getWorld("world").setStorm(false);
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setFoodLevel(20);
			p.setHealth(20);
			p.setAllowFlight(true);
			new ScoreboardManager(p);
			p.teleport(Main.getInstance().getSpawn());
			p.getOpenInventory().getBottomInventory().clear();
			p.getOpenInventory().getTopInventory().clear();
			p.getInventory().clear();
			p.setItemOnCursor(new ItemStack(Material.AIR));
			p.getInventory().setHeldItemSlot(0);
			p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
			p.getInventory().setItem(0, new ItemStackBuilder(Material.NETHER_STAR).name("&5&lServer Selector &7(Right-Click)").addTag("type", new NBTTagString("selector")).build());
			p.getInventory().setItem(2, new ItemStackBuilder(Material.GOLD_AXE).name("&5&lEnable PvP Mode &7(Right-Click)").addTag("type", new NBTTagString("axe")).glint(true).build());
			p.getInventory().setItem(4, new ItemStackBuilder(Material.ENDER_PEARL).name("&5&lEnder Butt &7(Right-Click)").addTag("type", new NBTTagString("pearl")).glint(true).build());
			p.getInventory().setItem(8, new ItemStackBuilder(Material.INK_SACK).name("&5&lPlayer Visibility &7(Right-Click)").addTag("type", new NBTTagString("visibility")).durability((short) 10).build());
		}
	}
	
	private void setupConfigs() {
			try {
				if(!configFile.exists()) {
					configFile.getParentFile().mkdirs();
					saveResource("config.yml", false);
				}
				config.load(configFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}

	private void setupSpawn(){
		double x = config.getDouble("spawn.x");
		double y = config.getDouble("spawn.y");
		double z = config.getDouble("spawn.z");
		spawn = new Location(Bukkit.getWorld("world"), x, y, z);
	}
	
	private void startNPCs() {
		donatorsInstance = new LatestDonators();
		donatorsInstance.initialize();
		votersInstance = new LatestVoters();
		votersInstance.initialize();
	}
	
	private void initializeCommands() {
		this.getCommand("battlebox").setExecutor(new BattleBoxCommand());
		this.getCommand("setupplayer").setExecutor(new SetupPlayer());
		this.getCommand("pvpmode").setExecutor(new PvPModeCommand());
		this.getCommand("buildermode").setExecutor(new BuilderModeCommand());
		this.getCommand("about").setExecutor(new AboutCommand());
		this.getCommand("about").setTabCompleter(new AboutCommand());
		this.getCommand("plugin").setExecutor(new PluginCommand());
		this.getCommand("spawn").setExecutor(new SpawnCommand());
		this.getCommand("message").setExecutor(new MessageCommand());
		this.getCommand("reply").setExecutor(new ReplyCommand());
		this.getCommand("server").setExecutor(new ServerSelector());
		this.getCommand("menu").setExecutor(new HelpfulMenu());
		this.getCommand("help").setExecutor(new HelpCommandsMenu());
		this.getCommand("cookie").setExecutor(new CookieCommand());
		this.getCommand("kaboom").setExecutor(new KaboomCommand());
	}
	
	private void initializeListeners() {
		Bukkit.getPluginManager().registerEvents(box, this);
		Bukkit.getPluginManager().registerEvents(pvpInstance, this);
		Bukkit.getPluginManager().registerEvents(donatorsInstance, this);
		Bukkit.getPluginManager().registerEvents(votersInstance, this);
		Bukkit.getPluginManager().registerEvents(new PortalListener(), this);
		Bukkit.getPluginManager().registerEvents(new PluginCommand(), this);
		Bukkit.getPluginManager().registerEvents(new BuilderModeCommand(), this);
		Bukkit.getPluginManager().registerEvents(new InteractionListeners(), this);
		Bukkit.getPluginManager().registerEvents(new MessageListeners(), this);
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new DoubleJumpListener(), this);
		Bukkit.getPluginManager().registerEvents(new EnderButtListener(), this);
		Bukkit.getPluginManager().registerEvents(new VisibilityListener(), this);
		Bukkit.getPluginManager().registerEvents(new ServerSelector(), this);
		Bukkit.getPluginManager().registerEvents(new HelpfulMenu(), this);
		Bukkit.getPluginManager().registerEvents(new HelpCommandsMenu(), this);
	}
	
	private void stopNPCs() {
		donatorsInstance.destroy();
		votersInstance.destroy();
	}
	
	private void hidePublicHolograms() {
		for(int i = 0; i < publicHolos.size(); i++) {
			if(i < publicHolos.size()) {
				PublicHologram p = publicHolos.get(0);
				p.destroy();
				publicHolos.remove(p);
			}
		}
	}
	
	private void disablePvpPlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PvpMode.isPvping(p)) {
				pvpInstance.disablePvpMode(p);
			}
		}
	}
	
	private void disableBuilderPlayers() {
		for(int i = 0; i < BuilderModeCommand.builderMode.size(); i++) {
			Player p = BuilderModeCommand.builderMode.get(0);
			BuilderModeCommand.disableBuilder(p);
		}
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static PvpMode getPvpInstance() {
		return pvpInstance;
	}
	
	public BattleBox getBox() {
		return box;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}

	public Location getSpawn(){
		return spawn.clone();
	}

}
