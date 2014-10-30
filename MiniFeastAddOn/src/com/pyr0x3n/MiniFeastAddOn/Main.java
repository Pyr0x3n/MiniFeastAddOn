package com.pyr0x3n.MiniFeastAddOn;

import java.util.logging.Level;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import me.libraryaddict.Hungergames.Configs.FeastConfig;
import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
    public FeastConfig config; 
    public MainConfig mainConfig; 
	public World world;
	public HashMap<Integer, Location> miniFeast = new HashMap<Integer, Location>() ; 
	
	@Override
	public void onEnable() {
		saveDefaultConfig();	
		this.config = HungergamesApi.getConfigManager().getFeastConfig();
		this.mainConfig = HungergamesApi.getConfigManager().getMainConfig();
		getServer().getPluginManager().registerEvents(this, this);
		this.world=Bukkit.getWorlds().get(0);
		for (int i = 0; i < getConfig().getInt("nbOfMiniFeast"); i++){
			miniFeast.put(setMiniFeastTime(), randomLoc());
		}

        //debug		
		Iterator<Integer> keySetIterator = miniFeast.keySet().iterator();
		while(keySetIterator.hasNext()){
		  Integer key = keySetIterator.next();
		  Bukkit.getServer().getLogger().log(Level.INFO, "[MiniFeastAddOn]" +
				  "Mini feast time: " + key + " Location xyz:" + miniFeast.get(key).getX() +";" + miniFeast.get(key).getY() +";"
				  +miniFeast.get(key).getZ());
		}
	}
	
	
	public Location randomLoc(){
        Location spawn;
        spawn = world.getSpawnLocation();
        int dist = config.getFeastMaxDistanceFromSpawn();
        Location feastLoc= new Location(spawn.getWorld(), spawn.getX()
                + (dist <= 0 ? 0 : (new Random().nextInt((dist * 2) + 1) - dist)), -1, spawn.getZ()
                + (dist <= 0 ? 0 : (new Random().nextInt((dist * 2) + 1) - dist)));
        feastLoc.setY( getHighestBlockLoc(feastLoc).getY()+1);
        return feastLoc;
	}
	
	public int  setMiniFeastTime(){
		Random r = new Random();
		int Low = mainConfig.getTimeForInvincibility();
		int High = config.getFeastGenerateTime();
		return  r.nextInt(High-Low) + Low;
	}
	
	public  Location getHighestBlockLoc(Location loc){
		Location newLoc = null;
		for(int h = 255; h != -1; h--){
			if((newLoc = new Location(loc.getWorld(), loc.getBlockX(), h, loc.getBlockZ())).getBlock().getType() != Material.AIR){
				return newLoc;
			}
		}
		return newLoc;
	}
	
	 @EventHandler
	    public void onSecond(TimeSecondEvent event) {
		 int time=HungergamesApi.getHungergames().currentTime;
		 if (miniFeast.containsKey(time)){
			 LibsFeastManager.getFeastManager().generateChests(miniFeast.get(time), 1);
			 Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
						'&',getConfig().getString("miniFeastMessage")));
		 }
	 }
}
