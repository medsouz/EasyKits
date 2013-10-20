package info.TrenTech.EasyKits;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

	private EasyKits plugin;
	public EventListener(EasyKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerClickEvent(InventoryClickEvent event){
		if(event.getInventory().getTitle().contains("Kit:")){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			String kitName = event.getInventory().getTitle().replace("Kit: ", "");
			if(event.getSlot() == 44){
				DataSource.instance.doKitEquipCheck(player, kitName);
				player.closeInventory();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginEvent(PlayerJoinEvent event){
		File file = new File(this.plugin.getDataFolder() + "/players/", event.getPlayer().getName() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		DataSource.instance.saveConfig(file, playerConfig);
	}
	
}
