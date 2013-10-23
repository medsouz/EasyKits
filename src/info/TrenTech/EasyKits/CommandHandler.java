package info.TrenTech.EasyKits;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
						DataSource.instance.dispose();
						try {
							DataSource.instance.connect();
						} catch (Exception e) {
							this.plugin.log.severe(String.format("[%s] - Unable to connect to database!", new Object[] { this.plugin.getDescription().getName() }));
						}
						sender.sendMessage(ChatColor.GREEN + "Reloaded!");
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
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
									player.sendMessage(ChatColor.GREEN + "Kit saved!");
								}else{
									player.sendMessage(ChatColor.RED + args[1] + " already exists!");
								}
							}else{
								player.sendMessage(ChatColor.YELLOW + "/kit create [kitname]");
							}
						}else{
							sender.sendMessage("You do not have permission!");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Must be a player!");
					}

				}else if(args[0].equalsIgnoreCase("remove")){
					if(sender.hasPermission("EasyKits.cmd.remove")){
						if(args.length == 2){
							if(DataSource.instance.kitExist(args[1])){
								DataSource.instance.deleteKit(args[1]);
								sender.sendMessage(ChatColor.GREEN + "Kit removed!");
							}else{
								sender.sendMessage(ChatColor.RED + "Kit does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit remove [kitname]");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}else if(args[0].equalsIgnoreCase("give")){
					if(sender.hasPermission("EasyKits.cmd.give")){
						if(args.length == 3){
							if(this.plugin.getServer().getPlayer(args[1]) != null){
								Player player = this.plugin.getServer().getPlayer(args[1]);
								if(DataSource.instance.kitExist(args[2])){
									if(DataSource.instance.kitEquip(player, args[2])){
										sender.sendMessage(ChatColor.GREEN + "gave kit to " + args[1]);
										player.sendMessage(ChatColor.GREEN + sender.getName() + " gave you a kit!");
									}else{
										sender.sendMessage(ChatColor.RED + "Player has insufficient inventory space!");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Kit does not exist!");
								}
							}else{
								sender.sendMessage(ChatColor.RED + "Player does not exist! Make sure they are online!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/kit give [player] [kitname]");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}else if(args[0].equalsIgnoreCase("book")){
					if(sender instanceof Player){
						Player player = (Player) sender;
						if(player.hasPermission("EasyKits.cmd.list")){
							player.getInventory().addItem(EasyKits.getCustomItem(Items.Kits));
						}else{
							player.sendMessage(ChatColor.RED + "You do not have permission!");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Must be a player!");
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
						sender.sendMessage(ChatColor.RED + "Must be a player!");
					}
					
				}else if(args.length == 1){				
					if(sender instanceof Player){
						Player player = (Player)sender;
						if(DataSource.instance.kitExist(args[0])){						
							DataSource.instance.doKitEquipCheck(player, args[0]);
						}else{
							player.sendMessage(ChatColor.RED + "Kit does not exist!");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Must be a player!");
					}
				}
				
			}else{
				sender.sendMessage(ChatColor.GREEN + "Command List:");
				sender.sendMessage(ChatColor.YELLOW + "/kit create [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit remove [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit book");
				sender.sendMessage(ChatColor.YELLOW + "/kit show [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit give [player] [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit reload");
			}
		}
		return true;
	}

	
}
