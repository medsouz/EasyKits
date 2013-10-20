package info.TrenTech.EasyKits;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import info.TrenTech.EasyKits.EasyKits;

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
							if (args.length == 3){
								if(!DataSource.instance.kitExist(args[1])){
									if(args[2].equalsIgnoreCase("Private") || args[2].equalsIgnoreCase("Public") || args[2].equalsIgnoreCase("Default")){
										String type = args[2];
										if(type.equalsIgnoreCase("Private")){
											type = player.getName();
										}
										ItemStack[] inv = player.getInventory().getContents();
										ItemStack[] arm = player.getInventory().getArmorContents();
										DataSource.instance.saveKit(args[1], type, inv, arm);
										player.sendMessage(ChatColor.GREEN + "Kit saved!");
									}else{
										player.sendMessage(ChatColor.RED + "Invalid Type!");
									}
								}else{
									player.sendMessage(ChatColor.RED + args[1] + " already exists!");
								}
							}else{
								player.sendMessage(ChatColor.YELLOW + "/kit create [kitname] [type]");
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
								@SuppressWarnings("unchecked")
								List<String> kitList = (List<String>) this.plugin.getConfig().getList("Kits");
								kitList.remove(args[1]);
								this.plugin.saveConfig();
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
						
					}
					
				}else if(args[0].equalsIgnoreCase("list")){
					if(sender.hasPermission("EasyKits.cmd.list")){
						String[] kitList = DataSource.instance.getKitList();
						sender.sendMessage(ChatColor.GREEN + "Kits:");
						for(String kit : kitList){
							String type = DataSource.instance.getKitType(kit);
							if(type.equalsIgnoreCase("Public") || type.equalsIgnoreCase(sender.getName()) || (type.equalsIgnoreCase("Default") && sender.hasPermission("EasyKits.kits." + kit)) || sender.hasPermission("EasyKits.cmd.create")){
								sender.sendMessage(ChatColor.YELLOW + "- " + kit);
							}
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}else if(args[0].equalsIgnoreCase("show")){
					if(sender instanceof Player){
						Player player = (Player)sender;
						if(sender.hasPermission("EasyKits.cmd.show")){
							if(args.length == 2){
								if(DataSource.instance.kitExist(args[1])){
									ItemStack[] inv = DataSource.instance.getKitInventory(args[1]);
									ItemStack[] arm = DataSource.instance.getKitArmor(args[1]);
									if((DataSource.instance.getKitType(args[1]).equalsIgnoreCase("Default") && player.hasPermission("EasyKits.kits." + args[1])) || (DataSource.instance.getKitType(args[1]).equalsIgnoreCase(player.getName()) || (DataSource.instance.getKitType(args[1]).equalsIgnoreCase("Public")))){
										Inventory showInv = this.plugin.getServer().createInventory(player, 45, "Kit: " + args[1]);
										showInv.setContents(inv);								
										int index = 36;
										for(ItemStack a : arm){
											showInv.setItem(index, a);
											index++;
										}	
										ItemStack star = new ItemStack(Material.NETHER_STAR);
										ItemMeta meta = star.getItemMeta();
										meta.setDisplayName(ChatColor.GREEN + args[1]);
										ArrayList<String> lores = new ArrayList<String>();
										lores.add(ChatColor.DARK_PURPLE+ "Click to equip kit!");
										meta.setLore(lores);
										star.setItemMeta(meta);								
										showInv.setItem(44, star);
										player.openInventory(showInv);					
									}
								}
							}else{
								sender.sendMessage(ChatColor.YELLOW + "/kit show [kitname]");
							}
						}else{
							sender.sendMessage(ChatColor.RED + "You do not have permission!");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Must be a player!");
					}
					
				}else if(args.length == 1){				
					if(sender instanceof Player){
						Player player = (Player)sender;
						if(DataSource.instance.kitExist(args[0])){
							if((DataSource.instance.getKitType(args[0]).equalsIgnoreCase("Default") && player.hasPermission("EasyKits.kits." + args[0])) || (DataSource.instance.getKitType(args[0]).equalsIgnoreCase(player.getName()) || (DataSource.instance.getKitType(args[0]).equalsIgnoreCase("Public")))){							
								if(player.hasPermission("EasyKits.kits.cooldown") && !player.isOp()){
									DataSource.instance.timedEquip(player, args[0]);
								}else{
									if(DataSource.instance.kitEquip(player, args[0])){
										player.sendMessage(ChatColor.GREEN + "Kit Equipped!");
									}
								}
							}else{
								player.sendMessage(ChatColor.RED + "You do not have permission to obtain this kit!");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Kit does not exist!");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Must be a player!");
					}
				}
				
			}else{
				sender.sendMessage(ChatColor.GREEN + "Command List:");
				sender.sendMessage(ChatColor.YELLOW + "/kit create [kitname] [type]");
				sender.sendMessage(ChatColor.YELLOW + "/kit remove [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit list");
				sender.sendMessage(ChatColor.YELLOW + "/kit show [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit give [player] [kitname]");
				sender.sendMessage(ChatColor.YELLOW + "/kit reload");
			}
		}
		return true;
	}

	
}
