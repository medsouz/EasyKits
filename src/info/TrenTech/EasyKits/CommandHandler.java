package info.TrenTech.EasyKits;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import info.TrenTech.EasyKits.EasyKits;
import info.TrenTech.EasyKits.EasyKits.Items;

public class CommandHandler implements CommandExecutor {
	private EasyKits plugin;

	public CommandHandler(EasyKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("kit")) {
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("reload")){
					if(sender.hasPermission("EasyKits.cmd.reload")){
						this.plugin.reloadConfig();
						this.plugin.saveConfig();
		                File folder = new File(plugin.getDataFolder() + "/players/");
		                File[] files = folder.listFiles();
					    for(File file : files){
					    	YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
							try {
								playerConfig.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
					    }
						DataSource.instance.dispose();
						try {
							DataSource.instance.connect();
						} catch (Exception e) {
							this.plugin.log.severe(String.format("[%s] - Unable to connect to database!", new Object[] { this.plugin.getDescription().getName() }));
						}
						sender.sendMessage(ChatColor.DARK_GREEN + "EasyKits Reloaded!");
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}				
				}else if(args[0].equalsIgnoreCase("create")){
					if(sender instanceof Player){
						Player player = (Player) sender;
						if(player.hasPermission("EasyKits.cmd.create")){
							if (args.length == 2){
								if(!DataSource.instance.kitExist(args[1])){
									ItemStack[] inv = player.getInventory().getContents();
									ItemStack[] arm = player.getInventory().getArmorContents();
									DataSource.instance.saveKit(args[1], inv, arm);
									player.sendMessage(ChatColor.DARK_GREEN + "Kit saved!");
								}else{
									player.sendMessage(ChatColor.DARK_RED + args[1] + " already exists!");
								}
							}else{
								player.sendMessage(ChatColor.YELLOW + "/kit create [kitname]");
								player.sendMessage(ChatColor.YELLOW + "/kit create [kitname]");
							}
						}else{
							sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
					}				
				}else if(args[0].equalsIgnoreCase("cooldown")){
					if(sender.hasPermission("EasyKits.cmd.create")){
						if((args.length == 3) && (args[2].matches("(^\\d*)(?i)[s]$") || args[2].matches("(^\\d*)(?i)[m]$") || args[2].matches("(^\\d*)(?i)[h]$") || args[2].matches("(^\\d*)(?i)[d]$"))){
							if(DataSource.instance.kitExist(args[1])){
								DataSource.instance.setKitCooldown(args[1], args[2]);
								sender.sendMessage(ChatColor.DARK_GREEN + "Kit cooldown set to " + args[2]);
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit cooldown [kitname] [cooldown]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}
				}else if(args[0].equalsIgnoreCase("maxuse")){
					if(sender.hasPermission("EasyKits.cmd.create")){
						if((args.length == 3) && isInt(args[2])){
							if(DataSource.instance.kitExist(args[1])){
								DataSource.instance.setKitMaxUse(args[1], Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.DARK_GREEN + "Kit max-use set to " + args[2]);
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit maxuse [kitname] [max-use]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}
				}else if(args[0].equalsIgnoreCase("price")){
					if(sender.hasPermission("EasyKits.cmd.create")){
						if((args.length == 3) && isDouble(args[2])){
							if(DataSource.instance.kitExist(args[1])){
								DataSource.instance.setKitPrice(args[1], Double.parseDouble(args[2]));
								sender.sendMessage(ChatColor.DARK_GREEN + "Kit price set to $" + args[2]);
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit price [kitname] [price]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}					
				}else if(args[0].equalsIgnoreCase("remove")){
					if(sender.hasPermission("EasyKits.cmd.remove")){
						if(args.length == 2){
							if(DataSource.instance.kitExist(args[1])){
								DataSource.instance.deleteKit(args[1]);
								sender.sendMessage(ChatColor.DARK_GREEN + "Kit removed!");
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit remove [kitname]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}					
				}else if(args[0].equalsIgnoreCase("give")){
					if(sender.hasPermission("EasyKits.cmd.give")){
						if(args.length == 3){
							if(this.plugin.getServer().getPlayer(args[1]).isOnline()){
								Player player = this.plugin.getServer().getPlayer(args[1]);
								if(DataSource.instance.kitExist(args[2])){
									if(DataSource.instance.kitEquip(player, args[2])){
										sender.sendMessage(ChatColor.DARK_GREEN + "gave kit to " + args[1]);
										player.sendMessage(ChatColor.DARK_GREEN + sender.getName() + " gave you a kit!");
									}else{
										sender.sendMessage(ChatColor.DARK_RED + "Player has insufficient inventory space!");
									}
								}else{
									sender.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
								}
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Player does not exist! Make sure they are online!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit give [player] [kitname]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}					
				}else if(args[0].equalsIgnoreCase("book")){
					if(sender instanceof Player){
						Player player = (Player) sender;
						if(player.hasPermission("EasyKits.cmd.list")){
							player.getInventory().addItem(EasyKits.getCustomItem(Items.Kits));
						}else{
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
					}
				}else if(args[0].equalsIgnoreCase("list")){
					if(sender.hasPermission("EasyKits.cmd.list")){
						String[] list = DataSource.instance.getKitList();
						sender.sendMessage(ChatColor.UNDERLINE +""+ ChatColor.DARK_GREEN + "Kits:");
						String joinKit = this.plugin.getConfig().getString("First-Join-Kit");
						for(String kit : list){
							if(sender.hasPermission("EasyKits.kits." + kit) || kit.equalsIgnoreCase(joinKit) || sender.hasPermission("EasyKits.kits.*")){
								double price = DataSource.instance.getKitPrice(kit);
								if(price > 0){
									sender.sendMessage(ChatColor.YELLOW + "- " + kit + ": " + ChatColor.DARK_GREEN + "$" + price);	
								}else{
									sender.sendMessage(ChatColor.YELLOW + "- " + kit);	
								}								
							}					
						}						
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have Permission");
					}
				}else if(args[0].equalsIgnoreCase("show")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						if(args.length == 2){
							DataSource.instance.showKit(player, args[1]);
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit show [kitname]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
					}
					
				}else if(args.length == 1){				
					if(sender instanceof Player){
						Player player = (Player)sender;
						if(DataSource.instance.kitExist(args[0])){				
							DataSource.instance.doKitEquipCheck(player, args[0]);
						}else{
							player.sendMessage(ChatColor.DARK_RED + "Kit does not exist!");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
					}
				}
				
			}else{
				sender.sendMessage(ChatColor.DARK_GREEN + "Command List:");
				sender.sendMessage(ChatColor.YELLOW + "/kit create [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit remove [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit maxuse [kitname] [cooldown]");
				sender.sendMessage(ChatColor.YELLOW + "/kit cooldown [kitname] [cooldown]");
				sender.sendMessage(ChatColor.YELLOW + "/kit price [kitname] [price]");
				sender.sendMessage(ChatColor.YELLOW + "/kit book");
				sender.sendMessage(ChatColor.YELLOW + "/kit list");
				sender.sendMessage(ChatColor.YELLOW + "/kit show [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit give [player] [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit reload");
			}
		}
		return true;
	}

	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {}
		return false;
	}
	
	public boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {}
		return false;
	}
	
}
