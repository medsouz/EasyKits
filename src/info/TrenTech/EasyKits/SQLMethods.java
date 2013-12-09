package info.TrenTech.EasyKits;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLMethods extends SQLUtils{

	public boolean loaded = false;
    private Object lock = new Object();
	
	public boolean econColumnExist() {
		boolean b = false;
		try {
			Statement statement = getConnection().createStatement();
			DatabaseMetaData md = statement.getConnection().getMetaData();
			ResultSet rs = md.getColumns(null, null, "kits" , "Price");
			if (rs.next()){
				b = true;	
			}		
		} catch (SQLException ex) { }
		return b;
	}
	
	public void createEconColumn(){
		synchronized (lock) {
			try {
				PreparedStatement statement;	
				statement = prepare("ALTER TABLE kits ADD Price DOUBLE");
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}	
	}
	
	public boolean cooldownColumnExist() {
		boolean b = false;
		try {
			Statement statement = getConnection().createStatement();
			DatabaseMetaData md = statement.getConnection().getMetaData();
			ResultSet rs = md.getColumns(null, null, "kits" , "Cooldown");
			if (rs.next()){
				b = true;	
			}		
		} catch (SQLException ex) { }
		return b;
	}
	
	public void createCooldownColumn(){
		synchronized (lock) {
			try {
				PreparedStatement statement;	
				statement = prepare("ALTER TABLE kits ADD Cooldown TEXT");
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}	
	}
	
	public boolean tableExist() {
		boolean b = false;
		try {
			Statement statement = getConnection().createStatement();
			DatabaseMetaData md = statement.getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, "kits" , null);
			if (rs.next()){
				b = true;	
			}		
		} catch (SQLException ex) { }
		return b;
	}
	
	public void createTable() {
		synchronized (lock) {
			try {
				PreparedStatement statement;	
				statement = prepare("CREATE TABLE kits( id INTEGER PRIMARY KEY, Kit TEXT, Inventory BLOB, Armor BLOB, Price DOUBLE, Cooldown TEXT)");
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}			
	}
	
	public boolean kitExist(String kitName) {
		boolean b = false;
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Kit").equalsIgnoreCase(kitName)) {
					b = true;
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return b;
	}
	
	public void createKit(String kitName, byte[] inv, byte[] armor) {
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("INSERT into kits (Kit, Inventory, Armor, Price) VALUES (?, ?, ?, ?)");	
				statement.setString(1, kitName);
				statement.setBytes(2, inv);
				statement.setBytes(3, armor);
				statement.setDouble(4, 0);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setKitCooldown(String kitName, String cooldown){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE kits SET Cooldown = ? WHERE Kit = ?");
				statement.setString(1, cooldown);
				statement.setString(2, kitName);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setKitPrice(String kitName, double price){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE kits SET Price = ? WHERE Kit = ?");
				statement.setDouble(1, price);
				statement.setString(2, kitName);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void saveKitSQL(String kitName, byte[] inv, byte[] armor) {
		synchronized (lock) {
			try {
				if(inv != null){
					PreparedStatement statement = prepare("UPDATE kits SET Inventory = ? WHERE Kit = ?");				
					statement.setBytes(1, inv);
					statement.setString(2, kitName);
					statement.executeUpdate();
				}
				if(armor != null){
					PreparedStatement statement = prepare("UPDATE kits SET Armor = ? WHERE Kit = ?");
					statement.setBytes(1, armor);
					statement.setString(2, kitName);
					statement.executeUpdate();
				}		
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public byte[] getKitInv(String kitName) {
		byte[] inv = null;
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Kit").equalsIgnoreCase(kitName)) {
					inv = rs.getBytes("Inventory");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return inv;
	}
	
	public byte[] getKitArm(String kitName) {
		byte[] armor = null;
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Kit").equalsIgnoreCase(kitName)) {
					armor = rs.getBytes("Armor");
					break;
				}
			}	
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return armor;
	}
	
	public double getKitPrice(String kitName) {
		double price = 0;
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Kit").equalsIgnoreCase(kitName)) {
					price = rs.getDouble("Price");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return price;
	}
	
	public String getKitCooldown(String kitName) {
		String cooldown = null;
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Kit").equalsIgnoreCase(kitName)) {
					cooldown = rs.getString("Cooldown");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return cooldown;
	}
	
	public String[] getKitList() {
		List<String> shopList = new ArrayList<String>();
		try {
			PreparedStatement statement = prepare("SELECT * FROM kits");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				shopList.add(rs.getString("Kit"));
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		String[] kits = new String[shopList.size()];
		for (int i = 0; i < shopList.size(); i++) {
			kits[i] = ((String) shopList.get(i));
		}
		return kits;
	}
	
	public void deleteKit(String kitName) {		
		try {
			PreparedStatement statement = prepare("DELETE from kits WHERE Kit = ?");
			statement.setString(1, kitName);
			statement.executeUpdate();
		}catch (SQLException e) {
			System.out.println("Unable to connect to Database!");
			System.out.println(e.getMessage());
		} 
	}
}
