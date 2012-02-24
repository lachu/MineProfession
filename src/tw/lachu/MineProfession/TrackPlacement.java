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
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import tw.lachu.MineProfession.util.SerialData;

public class TrackPlacement extends SerialData< HashMap<Integer,HashMap<Integer,HashSet<Integer>>> > implements Listener{
	private HashMap<Integer,HashMap<Integer,HashSet<Integer>>> data;
	private MineProfession mp;
	private File trackFile;
	
	public TrackPlacement(MineProfession mp, File file){
		this.mp = mp;
		this.trackFile = file;
		data = super.load(trackFile);
		if(data!=null){
			mp.log.info("MineProfession: Finish reading track placement data.");
		}else{
			data = new HashMap<Integer,HashMap<Integer,HashSet<Integer>>>();
			mp.log.info("MineProfession: Cannot read track placement data from "+trackFile.toString()+". Create an empty one.");
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
		
		if(super.save(data, trackFile,backup)){
			mp.log.info("MineProfession: Placement tracking data saved.");
		}else{
			mp.log.info("MineProfession: Cannot save placement tracking data to "+trackFile.toString());
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
	
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event){
		List<Block> blocks = event.getBlocks();
		BlockFace dir = event.getDirection();
		for(Block block:blocks){
			if(isHumanPlaced(block)){
				unsetHumanPlaced( block.getX()-dir.getModX(), block.getY()-dir.getModY(), block.getZ()-dir.getModZ() );
				setHumanPlaced(block.getX(), block.getY(), block.getZ());
			}
		}
	}
	
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event){
		Block block = event.getBlock();
		BlockFace dir = event.getDirection();
		if(isHumanPlaced(block)){
			unsetHumanPlaced( block.getX()-dir.getModX(), block.getY()-dir.getModY(), block.getZ()-dir.getModZ() );
			setHumanPlaced(block.getX(), block.getY(), block.getZ());
		}
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
