package info.TrenTech.EasyKits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
	
    @Override
    public void onEnable(){

    	new DataSource(this);
    	
    	registerEvents(this, new EventListener(this), new SignListener(this));
    	
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    
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
			economy = (Economy) economyProvider.getProvider();
			return true;
		}else{
			return false;
		}
	}
}
