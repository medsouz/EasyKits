package info.TrenTech.EasyKits;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Notifications {
	
	String kitName;
	String type;
	double money;
	String str;
	YamlConfiguration config;
	EasyKits plugin;

	public Notifications(EasyKits plugin) {
		this.plugin = plugin;
	}
	
	public Notifications(EasyKits plugin, YamlConfiguration messages) {
		this.config = messages;
		this.plugin = plugin;
	}
	
	public Notifications(String type, double money, String kitName){
		this.type = type;
		this.money = money;
		this.kitName = kitName;
	}
	
	public void getMessages(){
		EasyKits.messages.put("Permission-Denied", config.getString("Permission-Denied"));
		EasyKits.messages.put("Kit-Equip", config.getString("Kit-Equip"));
		EasyKits.messages.put("Kit-Exist", config.getString("Kit-Exist"));
		EasyKits.messages.put("Kit-Not-Exist", config.getString("Kit-Not-Exist"));
		EasyKits.messages.put("Max-Use", config.getString("Max-Use"));
		EasyKits.messages.put("Cooldown", config.getString("Cooldown"));
		EasyKits.messages.put("Insufficient-Funds", config.getString("Insufficient-Funds"));
		EasyKits.messages.put("Money-Charged", config.getString("Money-Charged"));
		EasyKits.messages.put("Inventory-Space", config.getString("Inventory-Space"));
		EasyKits.messages.put("First-Join-Kit", config.getString("First-Join-Kit"));
		EasyKits.messages.put("Kit-Book-Full", config.getString("Kit-Book-Full"));
	}
	
	public String getMessage(){
		String message = "";
		if(EasyKits.messages.get(type) != null){
			message = EasyKits.messages.get(type);
			if(message.contains("%K")){
				message = message.replaceAll("%K", kitName);
			}
			if(message.contains("%M")){
				message = message.replaceAll("%M", Double.toString(money));
			}
			if(message.contains("%T")){
				message = message.replaceAll("%T", kitName);
			}
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
}
