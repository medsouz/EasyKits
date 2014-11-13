package info.TrenTech.EasyKits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import info.TrenTech.EasyKits.EventListener;
import info.TrenTech.EasyKits.CommandHandler;

public class EasyKits extends JavaPlugin{
	
	public static EasyKits plugin;
	public final Logger log = Logger.getLogger("Minecraft");
	public Economy economy;
	public static HashMap<UUID, String> players = new HashMap<UUID, String>();
	public boolean econSupport = true;
	private CommandHandler cmdExecutor;
	public static HashMap<String, String> messages = new HashMap<String, String>();
	
    @Override
    public void onEnable(){

    	new DataSource(this);
    	new Notifications(this);
    	registerEvents(this, new EventListener(this), new SignListener(this));
    	
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    	
		File file = new File(getDataFolder(), "messages.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){		
			config.set("Permission-Denied", "&4You do not have Permission");
			config.set("Kit-Equip", "&2%K equipped!");
			config.set("Kit-Exist", "&4%K already exists!");
			config.set("Kit-Not-Exist", "&4%K does not exist!");
			config.set("Max-Use", "&4You have reached the max number of uses for this kit!");
			config.set("Cooldown", "&4You must wait %T before you can use this kit again!");
			config.set("Insufficient-Funds", "&4You need at least $%M");
			config.set("Money-Charged", "&2Charged $%M");
			config.set("Inventory-Space", "&4Insufficient inventory space!");
			config.set("First-Join-Kit", "&2Have a kit to get you started!");
			config.set("Kit-Book-Full", "&5There are more kits than can fit in the book. Use /kit list!");	
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Notifications notify = new Notifications(this, config);
		notify.getMessages();
		
		this.cmdExecutor = new CommandHandler(this);
		getCommand("kit").setExecutor(cmdExecutor);
		
		if (!setupEconomy()) {
        	log.warning(String.format("[%s] Vault not found! Economy support disabled!", new Object[] {getDescription().getName()}));
        	econSupport = false;
		}else{
			log.info(String.format("[%s] Vault found! Economy support enabled!", new Object[] { getDescription().getName() }));
		}
		
		try {
			DataSource.instance.connect();
		} catch (Exception e) {
            log.severe(String.format("[%s] Disabled! Unable to connect to database!", new Object[] {getDescription().getName()}));
            getServer().getPluginManager().disablePlugin(this);
            return;
		}
		
		if(!DataSource.instance.tableExist()){
			DataSource.instance.createTable();
			log.warning(String.format("[%s] Creating database!", new Object[] {getDescription().getName()}));
		}
			
    	if(getConfig().getString("Cooldown-Delay") != null){
    		getConfig().set("Cooldown-Delay", null);
    		saveConfig();
    	}
    	
		if(getConfig().getString("Max-Number-Of-Uses") != null){
    		getConfig().set("Max-Number-Of-Uses", null);
    		saveConfig();
		}
		
		String[] kits = DataSource.instance.getKitList();
		for(String kit : kits){
			if(DataSource.instance.getKitCooldown(kit) == null){
				log.warning(String.format("[%s] Fixing table entry!", new Object[] {getDescription().getName()}));
				DataSource.instance.setKitCooldown(kit, Integer.toString(0));
			}	
		}
		
		Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
    	for(Player player : onlinePlayers){
    		players.put(player.getUniqueId(), player.getName());
    	}
		
    }
    
	public static enum Items{
		Kits;
	}
	
	public static ItemStack getCustomItem(Items item){
		ItemStack itemStack = null;
		ItemMeta itemMeta;
		ArrayList<String> lore;
		switch (item){
		case Kits:
			itemStack = new ItemStack(Material.BOOK, 1);
			itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName("EasyKits");
			lore = new ArrayList<String>();
			lore.clear();
			lore.add(ChatColor.GREEN + "Big ol list of kits!");
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
	}
	
	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	
	private boolean setupEconomy() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if(plugin != null){
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			try{		
				economy = (Economy) economyProvider.getProvider();
				return true;
			}catch(NullPointerException e){
				return false;
			}
		}else{
			return false;
		}
	}
}
