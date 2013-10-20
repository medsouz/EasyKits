package info.TrenTech.EasyKits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DataSource extends SQLMethods{

	private static EasyKits plugin;
	public DataSource(EasyKits plugin) {
		DataSource.plugin = plugin;
	}
	
	public static DataSource instance = new DataSource(plugin);
	
	public void saveKit(String kitName, String type, ItemStack[] invArray, ItemStack[] armorArray){
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		for(ItemStack item : invArray){
			if(item != null){
				itemList.add(item);
			}
		}
		ItemStack[] newInvArray = itemList.toArray(new ItemStack[itemList.size()]);
		ByteArrayOutputStream inv = new ByteArrayOutputStream();
		ByteArrayOutputStream armor = new ByteArrayOutputStream();
	    try {
	        BukkitObjectOutputStream invObjOS = new BukkitObjectOutputStream(inv);
	        invObjOS.writeObject(newInvArray);
	        invObjOS.close();
	        BukkitObjectOutputStream armorObjOS = new BukkitObjectOutputStream(armor);
	        armorObjOS.writeObject(armorArray);
	        armorObjOS.close();
	    } catch (IOException ioexception) {
	        ioexception.printStackTrace();
	    }
	    if(getKitInv(kitName) == null){
	    	createKit(kitName, type, inv.toByteArray(), armor.toByteArray());
	    }else{
		    saveKit(kitName, type, inv.toByteArray(), armor.toByteArray());	
	    }
	}
	
	public ItemStack[] getKitInventory(String kitName){
        byte[] byteInv =  getKitInv(kitName);
		ByteArrayInputStream ByteArIS = new ByteArrayInputStream(byteInv);
        Object inv = null;
        try {
            BukkitObjectInputStream invObjIS = new BukkitObjectInputStream(ByteArIS);
            inv = invObjIS.readObject();
            invObjIS.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
		return (ItemStack[]) inv;
	}
	
	public ItemStack[] getKitArmor(String kitName){
        byte[] byteArm =  getKitArm(kitName);
		ByteArrayInputStream ByteArIS = new ByteArrayInputStream(byteArm);
        Object armor = null;
        try {
            BukkitObjectInputStream invObjIS = new BukkitObjectInputStream(ByteArIS);
            armor = invObjIS.readObject();
            invObjIS.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
		return (ItemStack[]) armor;
	}

	public boolean kitEquip(Player player, String kitName){
		ItemStack[] inv = getKitInventory(kitName);
		ItemStack[] arm = getKitArmor(kitName);
		ItemStack[] oldInv = player.getInventory().getContents();
		ItemStack[] oldArm = player.getInventory().getArmorContents();
		for(ItemStack item : inv){
			if(player.getInventory().firstEmpty() > -1){
				player.getInventory().addItem(item);
			}else{
				int amount = item.getAmount();
				HashMap<Integer, ? extends ItemStack> matchItem = player.getInventory().all(item);
				int size = matchItem.size();
				if(size < item.getMaxStackSize()){
					size = item.getMaxStackSize() - size;
					if(amount <= size){
						player.getInventory().addItem(item);
					}else{
						revertChanges(player, oldInv, oldArm );
						return false;					
					}
				}else{
					for(int i = 10; i <= 36; i++){
						if(i == 36){
							revertChanges(player, oldInv, oldArm );
							return false;
						}
						size = size - item.getMaxStackSize();
						if(size < item.getMaxStackSize()){
							if(amount <= size){
								player.getInventory().addItem(item);
							}
						}
					}
				}
			}
		}
		int index = 0;
		for(ItemStack item : arm){
			if(index == 0){
				if(item != null){
					if(player.getInventory().getBoots() == null){
						player.getInventory().setBoots(item);
					}else if(player.getInventory().firstEmpty() > -1){
						player.getInventory().addItem(item);
					}else{
						revertChanges(player, oldInv, oldArm );
						return false;
					}
				}
			}
			if(index == 1){
				if(item != null){
					if(player.getInventory().getLeggings() == null){
						player.getInventory().setLeggings(item);
					}else if(player.getInventory().firstEmpty() > -1){
						player.getInventory().addItem(item);
					}else{
						revertChanges(player, oldInv, oldArm );
						return false;
					}
				}
			}
			if(index == 2){
				if(item != null){
					if(player.getInventory().getChestplate() == null){
						player.getInventory().setChestplate(item);
					}else if(player.getInventory().firstEmpty() > -1){
						player.getInventory().addItem(item);
					}else{
						revertChanges(player, oldInv, oldArm );
						return false;
					}
				}
			}
			if(index == 3){
				if(item != null){
					if(player.getInventory().getHelmet() == null){
						player.getInventory().setHelmet(item);
					}else if(player.getInventory().firstEmpty() > -1){
						player.getInventory().addItem(item);
					}else{
						revertChanges(player, oldInv, oldArm );
						return false;
					}
				}
			}
			index++;			
		}			
		return true;
	}
	
	public void revertChanges(Player player, ItemStack[] oldInv, ItemStack[] oldArm){
		player.getInventory().setContents(oldInv);
		player.getInventory().setArmorContents(oldArm);
		player.sendMessage(ChatColor.RED + "Insufficient inventory space!");
	}
	
	public void saveConfig(File file, YamlConfiguration config) {
		try {
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			config.save(file);
		} catch (IOException e) {
			System.err.println("[MoneyDrop] Error writing config");
			e.printStackTrace();
		}
	}
	
	public void doKitEquipCheck(final Player player, final String kitName){
		boolean b = true;
		if(player.hasPermission("EasyKits.kits.maxuse") && !player.isOp()){
			File file = new File(plugin.getDataFolder() + "/players/", player.getName() + ".yml");
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
			int maxUse = plugin.getConfig().getInt("Max-Number-Of-Uses");
			int playerMaxUse = 0;
			if(playerConfig.getString(kitName + ".Max-Use") != null){
				playerMaxUse = playerConfig.getInt(kitName + ".Max-Use");
			}
			if( playerMaxUse < maxUse){
				playerConfig.set(kitName + ".Max-Use", playerMaxUse + 1);
				try {
					playerConfig.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				player.sendMessage(ChatColor.RED + "You have reached the max number of uses for this kit!");
				b = false;
			}
		}
		if(b == true){
			if(player.hasPermission("EasyKits.kits.cooldown") && !player.isOp()){
				final int cooldown = plugin.getConfig().getInt("Cooldown-Delay");
				player.sendMessage(ChatColor.YELLOW + "Cooldown initiated! Please wait " + cooldown + " seconds!");
				plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
					public void run(){
						if(DataSource.instance.kitEquip(player, kitName)){
							player.sendMessage(ChatColor.GREEN + "Kit Equipped!");
						}				
					}
				}, 20*cooldown);
			}else{
				if(DataSource.instance.kitEquip(player, kitName)){
					player.sendMessage(ChatColor.GREEN + "Kit Equipped!");
				}	
			}
		}
	}
	
}
