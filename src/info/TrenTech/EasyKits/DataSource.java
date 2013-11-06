package info.TrenTech.EasyKits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DataSource extends SQLMethods{

	private static EasyKits plugin;
	public DataSource(EasyKits plugin) {
		DataSource.plugin = plugin;
	}
	
	public static DataSource instance = new DataSource(plugin);
	
	public void saveKit(String kitName, ItemStack[] invArray, ItemStack[] armorArray, double price){
		ByteArrayOutputStream inv = new ByteArrayOutputStream();
		ByteArrayOutputStream armor = new ByteArrayOutputStream();
	    try {
	        BukkitObjectOutputStream invObjOS = new BukkitObjectOutputStream(inv);
	        invObjOS.writeObject(invArray);
	        invObjOS.close();
	        BukkitObjectOutputStream armorObjOS = new BukkitObjectOutputStream(armor);
	        armorObjOS.writeObject(armorArray);
	        armorObjOS.close();
	    } catch (IOException ioexception) {
	        ioexception.printStackTrace();
	    }
	    if(getKitInv(kitName) == null){
	    	createKit(kitName, inv.toByteArray(), armor.toByteArray(), price);
	    }else{
		    saveKitSQL(kitName, inv.toByteArray(), armor.toByteArray(), price);	
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
		String joinKit = plugin.getConfig().getString("First-Join-Kit");
		if(player.hasPermission("EasyKits.kits." + kitName) || kitName.equalsIgnoreCase(joinKit)){
			ItemStack[] inv = getKitInventory(kitName);
			ItemStack[] arm = getKitArmor(kitName);
			ItemStack[] oldInv = player.getInventory().getContents();
			ItemStack[] oldArm = player.getInventory().getArmorContents();
			int indexInv = 0;
			for(ItemStack item : inv){
				if(player.getInventory().getItem(indexInv) == null){
					if(item == null){
						item = new ItemStack(Material.AIR);
					}
					player.getInventory().setItem(indexInv, item);
				}else if(player.getInventory().firstEmpty() > -1){
					if(item == null){
						item = new ItemStack(Material.AIR);
					}
					player.getInventory().addItem(item);				
				}else{
					int amount = item.getAmount();
					HashMap<Integer, ? extends ItemStack> matchItem = player.getInventory().all(item);
					int size = matchItem.size();
					if(size < item.getMaxStackSize()){
						size = item.getMaxStackSize() - size;
						if(amount <= size){
							player.getInventory().setItem(indexInv, item);
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
									player.getInventory().setItem(indexInv, item);
								}
							}
						}
					}
				}
				indexInv++;
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
		}else{
			player.sendMessage(ChatColor.RED + "You do not have permission!");
			return false;
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
			System.err.println("Error writing config");
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
						if(plugin.econSupport && !player.hasPermission("EasyKits.kits.bypassprice")){
							doKitEcon(player, kitName);
						}else{
							if(DataSource.instance.kitEquip(player, kitName)){
								player.sendMessage(ChatColor.GREEN + "Kit equipped!");
							}
						}			
					}
				}, 20*cooldown);
			}else{
				if(plugin.econSupport && !player.hasPermission("EasyKits.kits.bypassprice")){
					doKitEcon(player, kitName);
				}else{
					if(DataSource.instance.kitEquip(player, kitName)){
						player.sendMessage(ChatColor.GREEN + "Kit equipped!");
					}
				}
			}
		}
	}
	
	public void doKitEcon(Player player, String kitName){
		double price = getKitPrice(kitName);
		double balance = plugin.economy.getBalance(player.getName());
		if(balance >= price){
			if(DataSource.instance.kitEquip(player, kitName)){
				plugin.economy.withdrawPlayer(player.getName(), price);
				player.sendMessage(ChatColor.GREEN + "Charged $" + price);
				player.sendMessage(ChatColor.GREEN + "Kit equipped!");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You need at least $" + price + "!");
		}
	}
	
	public void showKit(Player player, String kitName){
		if(player.hasPermission("EasyKits.cmd.show")){
			if(DataSource.instance.kitExist(kitName)){
				ItemStack[] inv = DataSource.instance.getKitInventory(kitName);
				ItemStack[] arm = DataSource.instance.getKitArmor(kitName);
				String joinKit = plugin.getConfig().getString("First-Join-Kit");
				if(player.hasPermission("EasyKits.kits." + kitName) || kitName.equalsIgnoreCase(joinKit)){
					Inventory showInv = plugin.getServer().createInventory(player, 45, "Kit: " + kitName);
					showInv.setContents(inv);								
					int index = 36;
					for(ItemStack a : arm){
						showInv.setItem(index, a);
						index++;
					}	
					ItemStack getKit = new ItemStack(Material.NETHER_STAR);
					ItemMeta getKitMeta = getKit.getItemMeta();
					getKitMeta.setDisplayName(ChatColor.GREEN + "Get " + kitName);
					ArrayList<String> getKitLores = new ArrayList<String>();
					getKitLores.add(ChatColor.DARK_PURPLE+ "Click to equip kit!");
					getKitMeta.setLore(getKitLores);
					getKit.setItemMeta(getKitMeta);								
					showInv.setItem(44, getKit);
					player.openInventory(showInv);					
				}
			}else{
				player.sendMessage(ChatColor.RED + "Kit does not Exist!");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You do not have permission!");
		}
	}
	
}
