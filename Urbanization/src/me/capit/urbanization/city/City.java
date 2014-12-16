package me.capit.urbanization.city;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.capit.entropy.EntropyLoader;
import me.capit.entropy.Registerable;
import me.capit.entropy.Registrar;
import me.capit.entropy.economy.EconomyAccount;
import me.capit.urbanization.Urbanization;
import me.capit.urbanization.group.Group;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class City implements ConfigurationSerializable, Registerable {
	private static final long serialVersionUID = -317734004326108165L;
	public static final short maxGroups = 255;
	public static final int rankZeros = 3;
	
	private String name,tag,motd,desc;
	private int defaultGroup;
	private Registrar<Group> groups = new Registrar<Group>();
	private EconomyAccount account;
	
	public final UUID ID;
	
	
	public City(String name, UUID owner) throws NullPointerException, IllegalArgumentException{
		setName(name);
		
		groups.register(new Group(0, "Admin").addPermission("*").addPlayer(owner));
		groups.register(new Group(maxGroups, "Default"));
		defaultGroup = maxGroups;
		
		ID = UUID.randomUUID();
		account = EntropyLoader.getDataRegistry().getAccount("@"+ID.toString());
	}
	
	@SuppressWarnings("unchecked")
	public City(Map<String,Object> data) throws ClassCastException, NullPointerException, IllegalArgumentException{
		setName((String) data.get("NAME"));
		ID = UUID.fromString((String) data.get("ID"));
		
		account = EntropyLoader.getDataRegistry().getAccount("@"+ID.toString());
		
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
	
	// GROUP DATA
	public Group getGroupAtRank(int rank){
		for (Group g : groups.getRegistered()){
			if (g.ID==rank) return getGroupAtString(g.getUniqueString());
		}
		return null;
	}
	public Group getGroupAtString(String ID){
		return groups.getRegisteredObject(ID);
	}
	public Group getDefaultGroup(){
		return getGroupAtRank(getDefaultGroupRank());
	}
	public int getDefaultGroupRank(){
		return defaultGroup;
	}
	public void createNewGroup(int rank, String name){
		if (rank>0 && rank<maxGroups) groups.register(new Group(rank,name));
	}
	
	// PLAYER DATA
	public void addPlayer(UUID player){
		getDefaultGroup().addPlayer(player);
	}
	public boolean playerInCity(UUID player){
		return getPlayerGroup(player)!=null;
	}
	public Group getPlayerGroup(UUID player){
		for (Group g : groups.getRegistered()){
			if (g.getPlayers().contains(player)) return g;
		}
		return null;
	}
	public void setPlayerGroup(UUID player, int rank){
		if (rank>=0 && rank<maxGroups && playerInCity(player) && getGroupAtRank(rank)!=null){
			getPlayerGroup(player).removePlayer(player);
			getGroupAtRank(rank).addPlayer(player);
		}
	}
	public void setOwner(UUID player){
		if (!playerInCity(player)) return;
		Group admin = getGroupAtRank(maxGroups);
		UUID owner = admin.getPlayers().get(0);
		admin.removePlayer(owner);
		admin.addPlayer(player);
		getDefaultGroup().removePlayer(player);
		getDefaultGroup().addPlayer(owner);
	}
	
	// MOTD DATA
	public final String getMOTD(){
		return motd!=null ? motd : "";
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
	
	// ECONOMY DATA
	public final EconomyAccount getAccount(){
		return account;
	}
	public final boolean hasFunds(double funds){
		return getAccount().getAccountValue()>=funds;
	}
	public final boolean withdraw(double funds){
		return getAccount().withdraw(funds);
	}
	public final void deposit(double funds){
		getAccount().deposit(funds);
	}
	
	// INTERFACE OVERRIDES
	@Override
	public String getUniqueString() {
		return ID.toString();
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
