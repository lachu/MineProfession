package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
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

public class TrackPlacement implements Listener{
	private HashMap<Integer,HashMap<Integer,HashSet<Integer>>> humanPlace;
	private MineProfession mp;
	private File trackFile;
	
	@SuppressWarnings("unchecked")
	public TrackPlacement(MineProfession mp, File woodFile){
		this.mp = mp;
		this.trackFile = woodFile;
		if(woodFile.exists()){
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.trackFile));
				Object result = ois.readObject();
				ois.close();
				humanPlace = (HashMap<Integer,HashMap<Integer,HashSet<Integer>>>)result;
			} catch (FileNotFoundException e) {
				humanPlace = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
				e.printStackTrace();
			} catch (IOException e) {
				humanPlace = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				humanPlace = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
				e.printStackTrace();
			}
		}else{
			humanPlace = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
		}
	}
	
	public void save(boolean backup){
		
		{
			Set<Integer> keys1 = humanPlace.keySet();
			for (Integer key1 : keys1) {
				Set<Integer> keys2 = humanPlace.get(key1).keySet();
				for (Integer key2 : keys2) {
					if (humanPlace.get(key1).get(key2).isEmpty()) {
						humanPlace.get(key1).remove(key2);
					}
				}
				if (humanPlace.get(key1).isEmpty()) {
					humanPlace.remove(key1);
				}
			}
		}
		
		
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(this.trackFile));
			oos.writeObject(humanPlace);
			oos.flush();
			oos.close();
			mp.log.info("MineProfession: Placement tracking data saved.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mp.log.info("MineProfession: Cannot save placement tracking data to "+trackFile.toString());
		} catch (IOException e) {
			e.printStackTrace();
			mp.log.info("MineProfession: Cannot save placement tracking data to "+trackFile.toString());
		}
		if(backup){
			Calendar currentTime = Calendar.getInstance();
			StringBuilder sb = new StringBuilder();
			sb.append(currentTime.get(Calendar.YEAR));
			sb.append("_");
			sb.append(currentTime.get(Calendar.MONTH+1));
			sb.append("_");
			sb.append(currentTime.get(Calendar.DATE));
			sb.append("_");
			sb.append(currentTime.get(Calendar.HOUR_OF_DAY));
			sb.append("_");
			sb.append(currentTime.get(Calendar.MINUTE));
			sb.append("_");
			sb.append(currentTime.get(Calendar.SECOND));
			sb.append(".trackPlacement");
			File backDir = new File(trackFile.getParentFile(),"backup");
			backDir.mkdir();
			File backFile = new File(backDir, sb.toString() );
			
			try {
				oos = new ObjectOutputStream(new FileOutputStream(backFile));
				oos.writeObject(this.humanPlace);
				oos.flush();
				oos.close();
				mp.log.info("MineProfession: Placement tracking data backup saved.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				mp.log.info("MineProfession: Backup Placement tracking data to "+backFile.toString()+" failed.");
			} catch (IOException e) {
				e.printStackTrace();
				mp.log.info("MineProfession: Backup Placement tracking data to "+backFile.toString()+" failed.");
			}
		}
	}
	
	public synchronized boolean isHumanPlaced(int x,int y,int z){
		HashMap<Integer,HashSet<Integer>> xMap;
		HashSet<Integer> zMap;
		return (xMap=humanPlace.get(x))!=null && (zMap=xMap.get(z))!=null && zMap.contains(y);
	}
	
	public boolean isHumanPlaced(Block block){
		return isHumanPlaced(block.getX(),block.getY(),block.getZ());
	}
	
	private synchronized void setHumanPlaced(int x,int y,int z){
		HashMap<Integer,HashSet<Integer>> xMap = humanPlace.get(x);
		if(xMap==null){
			xMap = new HashMap<Integer,HashSet<Integer>>();
			humanPlace.put(x, xMap);
		}
		HashSet<Integer> zMap = xMap.get(z);
		if(zMap==null){
			zMap = new HashSet<Integer>();
			xMap.put(z, zMap);
		}
		zMap.add(y);
	}
	
	private synchronized void unsetHumanPlaced(int x,int y,int z){
		humanPlace.get(x).get(z).remove(y);
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
