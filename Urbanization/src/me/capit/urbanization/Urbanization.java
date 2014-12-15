package me.capit.urbanization;

import java.util.Map;
import java.util.TreeMap;

import me.capit.entropy.Registrar;
import me.capit.entropy.module.EntropyModule;
import me.capit.entropy.module.ModuleMeta;
import me.capit.urbanization.city.City;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Urbanization extends EntropyModule {
	private static final long serialVersionUID = -7024584539096221029L;
	
	public static String addZerosToLength(String num, int zeros){
		if (num.length()>=zeros) return num;
		for (int i=0; i<(zeros-num.length()); i++){
			num = "0"+num;
		}
		return num;
	}
	
	public static Registrar<City> cities = new Registrar<City>();

	public Urbanization(JavaPlugin plugin, ModuleMeta meta) {
		super(plugin, meta);
	}
	
	@Override
	protected Map<String, Object> registerConfiguration(){
		Map<String,Object> data = new TreeMap<String,Object>();
		data.put("city_name_pattern", "^[A-Za-z0-9-_]{4,16}$");
		data.put("city_tag_pattern", "^[A-Z]{2,3}$");
		return data;
	}
	
	@Override
	protected void onEnable() {
		log("Loading Urbanization...");
		FileConfiguration cit = getDataConfig("cities.yml");
		for (String key : cit.getKeys(false)){
			City c = (City) cit.get(key);
			cities.register(c);
			log("  Registered &5"+c.getName()+"&f.");
		}
	}
	
	@Override
	protected void onDisable() {
		
	}
}
