package info.TrenTech.EasyKits;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		if(event.getInventory().getTitle().contains("Kits:")){
			Player player = (Player) event.getWhoClicked();
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getType() == Material.BOOK && event.getCurrentItem().hasItemMeta()){
					if(event.getCurrentItem().getItemMeta().hasDisplayName()){
						if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Kit:")){
							String kitName = event.getCurrentItem().getItemMeta().getDisplayName().replace("Kit: ", "");
							event.setCancelled(true);
							String[] list = DataSource.instance.getKitList();
							for(String kit : list){
								if(kit.equalsIgnoreCase(kitName)){							
									DataSource.instance.showKit(player, kitName);
									break;
								}
							}
						}
					}					
				}
			}		
		}		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		File file = new File(this.plugin.getDataFolder() + "/players/", player.getName() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		DataSource.instance.saveConfig(file, playerConfig);
		String joinKit = this.plugin.getConfig().getString("First-Join-Kit");
		if(!player.hasPlayedBefore()) {		
			 if(DataSource.instance.kitExist(joinKit)){
				 player.sendMessage(ChatColor.DARK_GREEN + "Have a kit to get you started!");
				 DataSource.instance.kitEquip(player, joinKit);
			 }
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(player.getItemInHand().getType() == Material.BOOK && player.getItemInHand().getItemMeta().hasDisplayName()){
				if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Kits")){
					Inventory kitInv = this.plugin.getServer().createInventory(player, 27, "Kits:");
					String[] list = DataSource.instance.getKitList();
					ItemStack[] kits = new ItemStack[26];
					int index = 0;
					String joinKit = this.plugin.getConfig().getString("First-Join-Kit");
					for(String kit : list){
						if(player.hasPermission("EasyKits.kits." + kit) || kit.equalsIgnoreCase(joinKit)){
							ItemStack kitItem = new ItemStack(Material.BOOK);
							ItemMeta itemMeta = kitItem.getItemMeta();
							itemMeta.setDisplayName("Kit: " + kit);
							kitItem.setItemMeta(itemMeta);
							kits[index] = kitItem.clone();							
						}
						index++;
					}
					kitInv.setContents(kits);								
					player.openInventory(kitInv);
				}
			}
		}
	}
}
