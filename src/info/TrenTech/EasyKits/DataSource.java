package info.TrenTech.EasyKits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
	
	public void saveKit(String kitName, ItemStack[] invArray, ItemStack[] armorArray){
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
	    if(getKitInv(kitName.toLowerCase()) == null){
	    	createKit(kitName.toLowerCase(), inv.toByteArray(), armor.toByteArray());
	    }else{
		    saveKitSQL(kitName.toLowerCase(), inv.toByteArray(), armor.toByteArray());	
	    }
	}
	
	public ItemStack[] getKitInventory(String kitName){
        byte[] byteInv =  getKitInv(kitName.toLowerCase());
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
        byte[] byteArm =  getKitArm(kitName.toLowerCase());
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
		ItemStack[] inv = getKitInventory(kitName.toLowerCase());
		ItemStack[] arm = getKitArmor(kitName.toLowerCase());
		Inventory tempInv = plugin.getServer().createInventory(player, 36);
		Inventory tempArm = plugin.getServer().createInventory(player, 9);
		tempInv.setContents(player.getInventory().getContents());
		tempArm.setContents(player.getInventory().getArmorContents());
		int index = 0;
		for(ItemStack item : inv){
			if(item == null){
				item = new ItemStack(Material.AIR);
			}
			if(tempInv.getItem(index) == null){
				tempInv.setItem(index, item);
			}else if(tempInv.firstEmpty() > -1){
				tempInv.addItem(item);			
			}else{
				int amount = item.getAmount();
				HashMap<Integer, ? extends ItemStack> matchItem = player.getInventory().all(item);				
				int size = matchItem.size();
				if(size < item.getMaxStackSize()){
					size = item.getMaxStackSize() - size;
					if(amount <= size){
						tempInv.setItem(index, item);
					}else{
						Notifications notify = new Notifications("Inventory-Space", 0, null);
						player.sendMessage(notify.getMessage());
						return false;					
					}
				}else{
					for(int i = 10; i <= 36; i++){
						if(i == 36){
							Notifications notify = new Notifications("Inventory-Space", 0, null);
							player.sendMessage(notify.getMessage());
							return false;
						}
						size = size - item.getMaxStackSize();
						if(size < item.getMaxStackSize()){
							if(amount <= size){					
								tempInv.setItem(index, item);
							}
						}
					}
					Notifications notify = new Notifications("Inventory-Space", 0, null);
					player.sendMessage(notify.getMessage());
					return false;
				}
			}
			index++;
		}
		index = 0;
		for(ItemStack item : arm){
			if(index == 0){
				if(item != null){
					if(tempArm.getItem(0) == null){
						tempArm.setItem(0, item);
					}else if(tempInv.firstEmpty() > -1){
						tempInv.addItem(item);
					}else{
						Notifications notify = new Notifications("Inventory-Space", 0, null);
						player.sendMessage(notify.getMessage());
						return false;
					}
				}
			}
			if(index == 1){
				if(item != null){
					if(tempArm.getItem(1) == null){
						tempArm.setItem(1, item);
					}else if(tempInv.firstEmpty() > -1){
						tempInv.addItem(item);
					}else{
						Notifications notify = new Notifications("Inventory-Space", 0, null);
						player.sendMessage(notify.getMessage());
						return false;
					}
				}
			}
			if(index == 2){
				if(item != null){
					if(tempArm.getItem(2) == null){
						tempArm.setItem(2, item);
					}else if(tempInv.firstEmpty() > -1){
						tempInv.addItem(item);
					}else{
						Notifications notify = new Notifications("Inventory-Space", 0, null);
						player.sendMessage(notify.getMessage());
						return false;
					}
				}
			}
			if(index == 3){
				if(item != null){
					if(tempArm.getItem(3) == null){
						tempArm.setItem(3, item);
					}else if(tempInv.firstEmpty() > -1){
						tempInv.addItem(item);
					}else{
						Notifications notify = new Notifications("Inventory-Space", 0, null);
						player.sendMessage(notify.getMessage());
						return false;
					}
				}
				break;
			}
			index++;
		}
		player.getInventory().setContents(tempInv.getContents());
		if(tempArm.getItem(0) != null){
			player.getInventory().setBoots(tempArm.getItem(0));
		}
		if(tempArm.getItem(1) != null){
			player.getInventory().setLeggings(tempArm.getItem(1));
		}
		if(tempArm.getItem(2) != null){
			player.getInventory().setChestplate(tempArm.getItem(2));
		}
		if(tempArm.getItem(3) != null){
			player.getInventory().setHelmet(tempArm.getItem(3));
		}
		return true;
	}
	
	public void doKitEquipCheck(final Player player, final String kitName){
		boolean cooldown = true;
		boolean maxUse = true;
		boolean charge = true;		
		boolean pass = true;
		if(!player.hasPermission("EasyKits.kits." + kitName.toLowerCase())){
			Notifications notify = new Notifications("Permission-Denied", 0, kitName);
			player.sendMessage(notify.getMessage());
			pass = false;
		}
		if(player.hasPermission("EasyKits.kits.maxuse") && !player.isOp()){
			if(!doMaxUseCheck(player, kitName.toLowerCase())){
				pass = false;
			}
		}else{
			maxUse = false;
		}
		if(player.hasPermission("EasyKits.kits.cooldown") && !player.isOp()){
			if(!doCooldownCheck(player, kitName.toLowerCase())){
				pass = false;
			}
		}else{
			cooldown = false;
		}
		if(plugin.econSupport && !player.hasPermission("EasyKits.kits.bypassprice")){
			if(!doPriceCheck(player, kitName.toLowerCase())){
				pass = false;
			}
		}else{
			charge = false;
		}
		if(pass){
			if(DataSource.instance.kitEquip(player, kitName)){
				if(maxUse){
					setMaxUse(player, kitName.toLowerCase());
				}
				if(cooldown){
					setCooldown(player, kitName.toLowerCase());
				}
				if(charge){
					doKitCharge(player, kitName.toLowerCase());
				}
				Notifications notify = new Notifications("Kit-Equip", 0, kitName);
				player.sendMessage(notify.getMessage());
			}
		}
	}
	
	public boolean doMaxUseCheck(Player player, String kitName){
		boolean b = true;		
		File file = new File(plugin.getDataFolder() + "/players/", player.getUniqueId() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		if(playerConfig.getString(kitName.toLowerCase() + ".Max-Use") != null){
			int playerMaxUse = playerConfig.getInt(kitName.toLowerCase() + ".Max-Use");
			int kitMaxUse = getKitMaxUse(kitName.toLowerCase());
			if(playerMaxUse >= kitMaxUse && kitMaxUse != 0){
				Notifications notify = new Notifications("Max-Use", 0, kitName);
				player.sendMessage(notify.getMessage());
				b = false;
			}
		}	
		return b;
	}

	public void setMaxUse(Player player, String kitName){
		File file = new File(plugin.getDataFolder() + "/players/", player.getUniqueId() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		int playerMaxUse = 0;
		if(playerConfig.getString(kitName.toLowerCase() + ".Max-Use") != null){
			playerMaxUse = playerConfig.getInt(kitName + ".Max-Use");
		}
		playerConfig.set(kitName.toLowerCase() + ".Max-Use", playerMaxUse + 1);
		Utils.saveConfig(file, playerConfig);
	}
	
	public boolean doCooldownCheck(Player player, String kitName){
		boolean b = true;
		File file = new File(plugin.getDataFolder() + "/players/", player.getUniqueId() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		if(playerConfig.getString(kitName.toLowerCase() + ".Cooldown") != null){
			String playerKitCooldown = playerConfig.getString(kitName.toLowerCase() + ".Cooldown");
			String kitCooldown = getKitCooldown(kitName.toLowerCase());
			Date date = new Date();
			Date cooldownDate = null;
			try {
				cooldownDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(playerKitCooldown);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long compare = TimeUnit.MILLISECONDS.toSeconds(date.getTime() - cooldownDate.getTime());
			int time = 0;
			if(kitCooldown.matches("(^\\d*)(?i)[s]$")){
				time = Integer.parseInt(kitCooldown.replace("s", ""));
			}else if(kitCooldown.matches("(^\\d*)(?i)[m]$")){
				time = Integer.parseInt(kitCooldown.replace("m", "")) * 60;
			}else if(kitCooldown.matches("(^\\d*)(?i)[h]$")){
				time = Integer.parseInt(kitCooldown.replace("h", "")) * 3600;
			}else if(kitCooldown.matches("(^\\d*)(?i)[d]$")){
				time = Integer.parseInt(kitCooldown.replace("d", "")) * 86400;
			}	
			if(!(time - compare <= 0)){
				b = false;
				String remaining = Utils.formatTime((int) (time - compare));
				Notifications notify = new Notifications("Cooldown", 0, remaining);
				player.sendMessage(notify.getMessage());
			}
		}
		return b;
	}
	
	public void setCooldown(Player player, String kitName){
		File file = new File(plugin.getDataFolder() + "/players/", player.getUniqueId() + ".yml");
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String strDate = dateFormat.format(date).toString();
		playerConfig.set(kitName.toLowerCase() + ".Cooldown", strDate);
		Utils.saveConfig(file, playerConfig);
	}
	
	public boolean doPriceCheck(Player player, String kitName){
		boolean b = true;
		double price = getKitPrice(kitName.toLowerCase());
		double balance = plugin.economy.getBalance(player);
		if(balance < price){
			b = false;
			Notifications notify = new Notifications("Insufficient-Funds", price, kitName);
			player.sendMessage(notify.getMessage());
		}
		return b;
	}
	
	public void doKitCharge(Player player, String kitName){
		double price = getKitPrice(kitName.toLowerCase());
		if(price != 0){
			plugin.economy.withdrawPlayer(player, price);
			Notifications notify = new Notifications("Money-Charged", price, kitName);
			player.sendMessage(notify.getMessage());
		}
	}
	
	public void showKit(Player player, String kitName){
		if(player.hasPermission("EasyKits.cmd.show")){
			if(DataSource.instance.kitExist(kitName)){
				ItemStack[] inv = DataSource.instance.getKitInventory(kitName.toLowerCase());
				ItemStack[] arm = DataSource.instance.getKitArmor(kitName.toLowerCase());
				boolean canUseKit = player.hasPermission("EasyKits.kits." + kitName.toLowerCase());
				if((canUseKit || player.hasPermission("EasyKits.show." + kitName.toLowerCase())) && !kitName.toLowerCase().equalsIgnoreCase(plugin.getConfig().getString("First-Join-Kit"))){
					Inventory showInv = plugin.getServer().createInventory(player, 45, "EasyKits Kit: " + kitName.toLowerCase());
					showInv.setContents(inv);								
					int index = 36;
					for(ItemStack a : arm){
						showInv.setItem(index, a);
						index++;
					}
					if(canUseKit){
						ItemStack getKit = new ItemStack(Material.NETHER_STAR);
						ItemMeta getKitMeta = getKit.getItemMeta();
						getKitMeta.setDisplayName(ChatColor.GREEN + "Get " + kitName.toLowerCase());
						ArrayList<String> getKitLores = new ArrayList<String>();
						getKitLores.add(ChatColor.DARK_PURPLE+ "Click to equip kit!");
						getKitMeta.setLore(getKitLores);
						getKit.setItemMeta(getKitMeta);
						showInv.setItem(44, getKit);
					}
					player.openInventory(showInv);					
				}
			}else{
				Notifications notify = new Notifications("Kit-Not-Exist", 0, kitName);
				player.sendMessage(notify.getMessage());
			}
		}else{
			Notifications notify = new Notifications("Permission-Denied", 0, kitName);
			player.sendMessage(notify.getMessage());
			player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
		}
	}

}
