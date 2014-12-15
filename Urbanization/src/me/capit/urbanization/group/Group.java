package me.capit.urbanization.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.capit.entropy.Registerable;
import me.capit.urbanization.Urbanization;
import me.capit.urbanization.city.City;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Group implements ConfigurationSerializable, Registerable {
	private static final long serialVersionUID = -4733520071370122570L;

	public final int ID;
	
	private String name;
	private List<UUID> players;
	private List<String> perms;
	
	public Group(int ID, String name){
		this(ID,name,new ArrayList<UUID>(),new ArrayList<String>());
	}
	
	public Group(int ID, String name, List<UUID> players, List<String> perms){
		this.ID = ID; this.name = name;
		this.players = players;
		this.perms = perms;
	}
	
	@SuppressWarnings("unchecked")
	public Group(Map<String,Object> data){
		ID = (short) data.get("ID");
		name = (String) data.get("NAME");
		
		perms = (List<String>) data.get("PERMISSIONS");
		for (String s : (List<String>) data.get("PLAYERS")) players.add(UUID.fromString(s));
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public String getUniqueString() {
		return "G"+Urbanization.addZerosToLength(String.valueOf(ID), City.rankZeros);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> data = new TreeMap<String,Object>();
		data.put("ID", ID); data.put("NAME", name);
		data.put("PERMISSIONS", perms);
		
		List<String> spl = new ArrayList<String>();
		for (UUID id : players) spl.add(id.toString());
		data.put("PLAYERS", spl);
		
		return data;
	}
	
	public List<String> getPermissions(){
		return perms;
	}
	public Group addPermission(String perm){
		perms.add(perm); return this;
	}
	public Group removePermission(String perm){
		perms.remove(perm); return this;
	}

	public List<UUID> getPlayers(){
		return players;
	}
	public Group addPlayer(UUID player){
		players.add(player); return this;
	}
	public Group removePlayer(UUID player){
		players.remove(player); return this;
	}
}
