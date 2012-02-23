package tw.lachu.MineProfession;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TrackPlacement extends SerialData implements Listener{
	private HashMap<Integer,HashMap<Integer,HashSet<Integer>>> data;
	private MineProfession mp;
	private File trackFile;
	
	@SuppressWarnings("unchecked")
	public TrackPlacement(MineProfession mp, File file){
		this.mp = mp;
		this.trackFile = file;
		Object obj = super.load(trackFile);
		if(obj!=null){
			data = (HashMap<Integer,HashMap<Integer,HashSet<Integer>>>)obj;
			mp.log.info("MineProfession: Finish reading track placement data.");
		}else{
			data = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
			mp.log.info("MineProfession: Cannot read track placement data from "+trackFile.toString());
		}
	}
	
	public void save(boolean backup){
		
		{ //gc
			Set<Integer> keys1 = data.keySet();
			for (Integer key1 : keys1) {
				Set<Integer> keys2 = data.get(key1).keySet();
				for (Integer key2 : keys2) {
					if (data.get(key1).get(key2).isEmpty()) {
						data.get(key1).remove(key2);
					}
				}
				if (data.get(key1).isEmpty()) {
					data.remove(key1);
				}
			}
		}
		
		if(super.save(data, trackFile)){
			mp.log.info("MineProfession: Placement tracking data saved.");
		}else{
			mp.log.info("MineProfession: Cannot save placement tracking data to "+trackFile.toString());
		}
		
		if(backup){
			if(super.save(data, super.getBackupFile("trackPlacement", trackFile))){
				mp.log.info("MineProfession: Placement tracking data backup saved.");
			}else{
				mp.log.info("MineProfession: Cannot save placement tracking data backup.");
			}
		}
	}
	
	public synchronized boolean isHumanPlaced(int x,int y,int z){
		HashMap<Integer,HashSet<Integer>> xMap;
		HashSet<Integer> zMap;
		return (xMap=data.get(x))!=null && (zMap=xMap.get(z))!=null && zMap.contains(y);
	}
	
	public boolean isHumanPlaced(Block block){
		return isHumanPlaced(block.getX(),block.getY(),block.getZ());
	}
	
	private synchronized void setHumanPlaced(int x,int y,int z){
		HashMap<Integer,HashSet<Integer>> xMap = data.get(x);
		if(xMap==null){
			xMap = new HashMap<Integer,HashSet<Integer>>();
			data.put(x, xMap);
		}
		HashSet<Integer> zMap = xMap.get(z);
		if(zMap==null){
			zMap = new HashSet<Integer>();
			xMap.put(z, zMap);
		}
		zMap.add(y);
	}
	
	private synchronized void unsetHumanPlaced(int x,int y,int z){
		data.get(x).get(z).remove(y);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event){
		List<String> types = mp.getConfig().getStringList("track-placement");
		for(String type:types){
			if(type.equals(event.getBlock().getType().name())){
				//mp.log.info("place "+event.getBlock().getX()+", "+event.getBlock().getY()+", "+event.getBlock().getZ());
				setHumanPlaced(event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getWorld()!=null && isHumanPlaced(event.getBlock())){
			Collection<ItemStack> drops = event.getBlock().getDrops();
			Location loc = event.getBlock().getLocation();
			event.getBlock().setType(Material.getMaterial("AIR"));
			unsetHumanPlaced(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
			for(ItemStack drop:drops){
				loc.getWorld().dropItem(loc, drop);
			}
		}
	}
}
