package info.TrenTech.EasyKits;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Utils {

	static void saveConfig(File file, YamlConfiguration config) {
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
	
	static String formatTime(int timeInSec) {
		int days = timeInSec / 86400;
		int dRemainder = timeInSec % 86400;
		int hours = dRemainder / 3600;
		int hRemainder = dRemainder % 3600;
		int minutes = hRemainder / 60;
		int seconds = hRemainder % 60;
		String time = null;
		if(days > 0){
			String dys = " Days";
			if(days == 1){
				dys = " Day";
			}
			time = days + dys;	
		}		
		if((hours > 0) || (hours == 0 && days > 0)){
			String hrs = " Hours";
			if(hours == 1){
				hrs = " Hour";
			}
			if(time != null){
				time = time + ", " + hours + hrs;
			}else{
				time = hours + hrs;
			}		
		}
		if((minutes > 0) || (minutes == 0 && days > 0) || (minutes == 0 && hours > 0)){
			String min = " Minutes";
			if(minutes == 1){
				min = " Minute";
			}
			if(time != null){
				time = time + ", " + minutes + min;	
			}else{
				time = minutes + min;
			}			
		}
		if(seconds > 0){
			String sec = " Seconds";
			if(seconds == 1){
				sec = " Second";
			}
			if(time != null){
				time = time + ", " + seconds + sec;
			}else{
				time = seconds + sec;
			}			
		}
		return time;
	}
}
