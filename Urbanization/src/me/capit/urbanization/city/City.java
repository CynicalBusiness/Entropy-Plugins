package me.capit.urbanization.city;

import java.util.Map;
import java.util.TreeMap;

import me.capit.entropy.EntropyLoader;
import me.capit.entropy.Registerable;
import me.capit.entropy.Registrar;
import me.capit.urbanization.Urbanization;
import me.capit.urbanization.group.Group;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class City implements ConfigurationSerializable, Registerable {
	private static final long serialVersionUID = -317734004326108165L;
	public static final short maxGroups = 255;
	public static final int rankZeros = 3;
	
	private String name,tag,motd,desc;
	private Registrar<Group> groups = new Registrar<Group>();
	
	public City(String name){
		
	}
	
	@SuppressWarnings("unchecked")
	public City(Map<String,Object> data) throws ClassCastException, NullPointerException, IllegalArgumentException{
		setName((String) data.get("NAME"));
		
		setTag(data.containsKey("TAG") ? (String) data.get("TAG") : null);
		setMOTD(data.containsKey("MOTD") ? (String) data.get("MOTD") : null);
		setDescription(data.containsKey("DESC") ? (String) data.get("DESC") : null);
		
		Map<String,Object> groupMap = (Map<String,Object>) data.get("GROUPS");
		for (String k : groupMap.keySet()){
			groups.register((Group) groupMap.get(k));
		}
	}
	
	// NAME DATA
	public final String getName(){
		return tag!=null ? tag : name;
	}
	public final void setName(String name) throws NullPointerException, IllegalArgumentException, ClassCastException{
		Urbanization module = EntropyLoader.getDataRegistry().getModuleAs("Urbanization", Urbanization.class);
		if (name.matches(module.getConfig().getAs("city_name_pattern", String.class))) this.name=name; 
		else throw new IllegalArgumentException("Name does not match format!");
	}
	
	// MOTD DATA
	public final String getMOTD(){
		return motd!=null ? motd : "No MOTD set.";
	}
	public final void setMOTD(String motd){
		this.motd=motd;
	}
	public final void clearMOTD(){
		setMOTD(null);
	}
	
	// DESCRIPTION DATA
	public final String getDescription(){
		return desc!=null ? desc : "Default city description.";
	}
	public final void setDescription(String desc){
		this.desc=desc;
	}
	public final void clearDescription(){
		setDescription(null);
	}
	
	// TAG DATA
	public final String getTag(){
		return getName();
	}
	public final void setTag(String tag) throws IllegalArgumentException, ClassCastException{
		if (tag==null){this.tag=null; return;}
		Urbanization module = EntropyLoader.getDataRegistry().getModuleAs("Urbanization", Urbanization.class);
		if (tag.matches(module.getConfig().getAs("city_tag_pattern", String.class))) this.tag=tag;
		else throw new IllegalArgumentException("Tag does not match format!");
	}
	public final void clearTag(){
		setTag(null);
	}
	
	// INTERFACE OVERRIDES
	@Override
	public String getUniqueString() {
		return name;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> data = new TreeMap<String,Object>();
		data.put("NAME", name); data.put("TAG", tag);
		data.put("MOTD", motd); data.put("DESC", desc);
		
		Map<String,Object> groupMap = new TreeMap<String,Object>();
		for (Group g : groups.getRegistered()){
			groupMap.put(g.getUniqueString(), g);
		}
		data.put("GROUPS", groupMap);
		
		return data;
	}

}
