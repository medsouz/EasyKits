package info.TrenTech.EasyKits;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import info.TrenTech.EasyKits.EventListener;
import info.TrenTech.EasyKits.CommandHandler;

public class EasyKits extends JavaPlugin{
	
	public static EasyKits plugin;
	public final Logger log = Logger.getLogger("Minecraft");
	private EventListener eventlistener;
	private CommandHandler cmdExecutor;
	
    @Override
    public void onEnable(){
    	
    	new DataSource(this);

		this.eventlistener = new EventListener(this);
		getServer().getPluginManager().registerEvents(this.eventlistener, this);
    	
    	getConfig().options().copyDefaults(true);
    	saveConfig();
		this.cmdExecutor = new CommandHandler(this);
		getCommand("kit").setExecutor(cmdExecutor);
		
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
    }
    
    @Override
    public void onDisable(){
    	DataSource.instance.dispose();
    	log.info(String.format("[%s] Disabled Version %s", new Object[] {getDescription().getName(), getDescription().getVersion() }));
    }
    
}

