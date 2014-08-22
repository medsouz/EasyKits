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
				if(item == null){
					item = new ItemStack(Material.AIR);
				}
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
		return true;
	}
	
	public void revertChanges(Player player, ItemStack[] oldInv, ItemStack[] oldArm){
		player.getInventory().setContents(oldInv);
		player.getInventory().setArmorContents(oldArm);
		player.sendMessage(ChatColor.DARK_RED + "Insufficient inventory space!");
	}
	
	public void doKitEquipCheck(final Player player, final String kitName){
		boolean cooldown = true;
		boolean maxUse = true;
		boolean charge = true;		
		boolean pass = true;
		if(!player.hasPermission("EasyKits.kits." + kitName.toLowerCase())){
			player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
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
				player.sendMessage(ChatColor.DARK_GREEN + "Kit equipped!");
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
				player.sendMessage(ChatColor.DARK_RED + "You have reached the max number of uses for this kit!");
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
				player.sendMessage(ChatColor.DARK_RED + "You must wait " + remaining + " before you can use this kit again!");
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
			player.sendMessage(ChatColor.DARK_RED + "You need at least $" + price + "!");
		}
		return b;
	}
	
	public void doKitCharge(Player player, String kitName){
		double price = getKitPrice(kitName.toLowerCase());
		if(price != 0){
			plugin.economy.withdrawPlayer(player, price);
			player.sendMessage(ChatColor.DARK_GREEN + "Charged $" + price);
		}
	}
	
	public void showKit(Player player, String kitName){
		if(player.hasPermission("EasyKits.cmd.show")){
			if(DataSource.instance.kitExist(kitName)){
				ItemStack[] inv = DataSource.instance.getKitInventory(kitName.toLowerCase());
				ItemStack[] arm = DataSource.instance.getKitArmor(kitName.toLowerCase());
				if(player.hasPermission("EasyKits.kits." + kitName.toLowerCase()) && !kitName.toLowerCase().equalsIgnoreCase(plugin.getConfig().getString("First-Join-Kit"))){
					Inventory showInv = plugin.getServer().createInventory(player, 45, "EasyKits Kit: " + kitName.toLowerCase());
					showInv.setContents(inv);								
					int index = 36;
					for(ItemStack a : arm){
						showInv.setItem(index, a);
						index++;
					}	
					ItemStack getKit = new ItemStack(Material.NETHER_STAR);
					ItemMeta getKitMeta = getKit.getItemMeta();
					getKitMeta.setDisplayName(ChatColor.GREEN + "Get " + kitName.toLowerCase());
					ArrayList<String> getKitLores = new ArrayList<String>();
					getKitLores.add(ChatColor.DARK_PURPLE+ "Click to equip kit!");
					getKitMeta.setLore(getKitLores);
					getKit.setItemMeta(getKitMeta);								
					showInv.setItem(44, getKit);
					player.openInventory(showInv);					
				}
			}else{
				player.sendMessage(ChatColor.DARK_RED + "Kit does not Exist!");
			}
		}else{
			player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
		}
	}

}
