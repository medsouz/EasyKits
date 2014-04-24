package info.TrenTech.EasyKits;

import java.io.File;
import java.util.ArrayList;

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
		if(event.getInventory().getTitle().contains("EasyKits Kit:")){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			String kitName = event.getInventory().getTitle().replace("EasyKits Kit: ", "");
			if(event.getSlot() == 44){
				DataSource.instance.doKitEquipCheck(player, kitName);
				player.closeInventory();
			}
		}
		if(event.getInventory().getTitle().contains("EasyKits:")){
			Player player = (Player) event.getWhoClicked();
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getType() == Material.BOOK && event.getCurrentItem().hasItemMeta()){
					if(event.getCurrentItem().getItemMeta().hasDisplayName()){
						if(event.getCurrentItem().getItemMeta().getDisplayName().contains("EasyKits Kit:")){
							String kitName = event.getCurrentItem().getItemMeta().getDisplayName().replace("EasyKits Kit: ", "");
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
		Utils.saveConfig(file, playerConfig);
		String joinKit = this.plugin.getConfig().getString("First-Join-Kit");
		if(!player.hasPlayedBefore()) {		
			 if(DataSource.instance.kitExist(joinKit)){
				 player.sendMessage(ChatColor.DARK_GREEN + "Have a kit to get you started!");
				 DataSource.instance.kitEquip(player, joinKit);
			 }
		}
		if(player.hasPermission("EasyKits.cmd.update") && EasyKits.update){
		    player.sendMessage(ChatColor.YELLOW + "Update Available: " + EasyKits.name + " for " + EasyKits.version);
		    player.sendMessage(ChatColor.YELLOW + "Type /kit update if you would like to update!");
		}
				
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(player.getItemInHand().getType() == Material.BOOK && player.getItemInHand().getItemMeta().hasDisplayName()){
				if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("EasyKits")){
					Inventory kitInv = this.plugin.getServer().createInventory(player, 54, "EasyKits:");
					String[] list = DataSource.instance.getKitList();
					if(list.length <= 9){
						kitInv = this.plugin.getServer().createInventory(player, 9, "EasyKits:");
					}else if(list.length > 9 && list.length <= 18){
						kitInv = this.plugin.getServer().createInventory(player, 18, "EasyKits:");
					}else if(list.length > 18 && list.length <= 27){
						kitInv = this.plugin.getServer().createInventory(player, 27, "EasyKits:");
					}else if(list.length > 27 && list.length <= 36){
						kitInv = this.plugin.getServer().createInventory(player, 36, "EasyKits:");
					}else if(list.length > 36 && list.length <= 45){
						kitInv = this.plugin.getServer().createInventory(player, 45, "EasyKits:");
					}
					int index = 0;
					boolean warning = false;
					for(String kit : list){
						if(player.hasPermission("EasyKits.kits." + kit) && !kit.equalsIgnoreCase(plugin.getConfig().getString("First-Join-Kit"))){
							ItemStack kitItem = new ItemStack(Material.BOOK);
							ItemMeta itemMeta = kitItem.getItemMeta();
							itemMeta.setDisplayName("EasyKits Kit: " + kit);
							if(this.plugin.econSupport){
								double price = DataSource.instance.getKitPrice(kit);
								if(price > 0){
									ArrayList<String> lore = new ArrayList<String>();
									lore.add(ChatColor.GREEN + "$" + price);
									itemMeta.setLore(lore);
								}
							}
							kitItem.setItemMeta(itemMeta);
							if(index < 54){
								kitInv.addItem(kitItem);
								index++;
							}else{
								warning = true;
							}					
						}			
					}						
					player.openInventory(kitInv);
					if(warning == true){
						player.sendMessage(ChatColor.DARK_PURPLE + "There are more kits than can fit in the book. Use /kit list!");
					}
				}
			}
		}
	}
}
