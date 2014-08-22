package info.TrenTech.EasyKits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener{

	private EasyKits plugin;
	
	public SignListener(EasyKits plugin) {
		this.plugin = plugin;
	}

	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignUse(PlayerInteractEvent event) {
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if (((action == Action.RIGHT_CLICK_BLOCK) && (block.getType().equals(Material.WALL_SIGN))) || ((action == Action.RIGHT_CLICK_BLOCK) && (block.getType().equals(Material.SIGN_POST)))) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String[] line = sign.getLines();
			String kitSign = ChatColor.DARK_BLUE + "[Kit]";
			if (line[0].equalsIgnoreCase(kitSign)) {
				String kitName = line[1];			
				if (DataSource.instance.kitExist(kitName)) {
					if(this.plugin.getConfig().getString("Sign-Use").equalsIgnoreCase("show")){
						DataSource.instance.showKit(player, kitName.toLowerCase());
					}else if(this.plugin.getConfig().getString("Sign-Use").equalsIgnoreCase("equip")){
						DataSource.instance.doKitEquipCheck(player, kitName.toLowerCase());
						player.updateInventory();
					}else{
						player.sendMessage(ChatColor.RED + "ERROR: Check your config!");
					}			
				} else {
					player.sendMessage(ChatColor.RED + line[1] + " Does Not Exist!");
				}		
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignPlace(SignChangeEvent event) {
		String[] line = event.getLines();
		Player player = event.getPlayer();
		if (line[0].equalsIgnoreCase("[Kit]")) {
			String kitName = line[1];
			if (DataSource.instance.kitExist(kitName)) {
				if (player.hasPermission("EasyKits.cmd.create")) {
					String newLine = ChatColor.DARK_BLUE + "[Kit]";
					if(DataSource.instance.getKitPrice(kitName) > 0){
						String price = ChatColor.DARK_GREEN + "$" + Double.toString(DataSource.instance.getKitPrice(kitName));
						event.setLine(2, price);
					}					
					event.setLine(0, newLine);					
					event.setLine(3, null);
				} else {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You Do Not Have Permission!");
				}
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + line[1] + " Does Not Exist!");
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignBreak(BlockBreakEvent event) {
		Material block = event.getBlock().getType();
		Player player = event.getPlayer();
		if ((block.equals(Material.SIGN_POST)) || (block.equals(Material.WALL_SIGN))) {
			Sign sign = (Sign) event.getBlock().getState();
			String[] line = sign.getLines();
			String kit = ChatColor.DARK_BLUE + "[Kit]";
			if (line[0].equalsIgnoreCase(kit)){
				if(!player.hasPermission("EasyKits.sign")) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You Dont Not Have Permission to Break Kit Sign!");
				}
			}
		}
	}
	
}
