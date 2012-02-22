package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TraceWood implements Listener{
	/*public static class Position implements Serializable{
		public Position(int x,int y,int z){
			this.x=x;
			this.y=y;
			this.z=z;
		}
		private static final long serialVersionUID = 1L;
		int x;
		int y;
		int z;
		public boolean equals(Position pos){
			return this.x==pos.x && this.y==pos.y && this.z==pos.z;
		}
		
		public String toString(){
			return "tw.lachu.MineProfession.TraceWood.Position: "+this.x+", "+this.y+", "+this.z;
		}
	}*/
	private HashMap<Integer,HashMap<Integer,HashSet<Integer>>> humanPlace;
	@SuppressWarnings("unused")
	private MineProfession mp;
	private File woodFile;
	
	@SuppressWarnings("unchecked")
	public TraceWood(MineProfession mp, File woodFile){
		this.mp = mp;
		this.woodFile = woodFile;
		if(woodFile.exists()){
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.woodFile));
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
	
	public void save(){
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(this.woodFile));
			oos.writeObject(humanPlace);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isHumanPlaced(int x,int y,int z){
		HashMap<Integer,HashSet<Integer>> xMap;
		HashSet<Integer> zMap;
		return (xMap=humanPlace.get(x))!=null && (zMap=xMap.get(z))!=null && zMap.contains(y);
	}
	
	public boolean isHumanPlaced(Block block){
		return isHumanPlaced(block.getX(),block.getY(),block.getZ());
	}
	
	public void setHumanPlaced(int x,int y,int z){
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
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if("LOG".equals(event.getBlock().getType().name())){
			//mp.log.info("place "+event.getBlock().getX()+", "+event.getBlock().getY()+", "+event.getBlock().getZ());
			setHumanPlaced(event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ());
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event){
		//if("LOG".equals(event.getBlock().getType().name())){
			//mp.log.info("break "+event.getBlock().getX()+", "+event.getBlock().getY()+", "+event.getBlock().getZ());
		//}
		
		//if(event.getBlock().getWorld()!=null) mp.log.info("a");
		//if("LOG".equals(event.getBlock().getType().name())) mp.log.info("b");
		//if(isHumanPlaced(event.getBlock())) mp.log.info("c");
		//else mp.log.info("... "+event.getBlock().getX()+", "+event.getBlock().getY()+", "+event.getBlock().getZ());
		
		if(event.getBlock().getWorld()!=null && "LOG".equals(event.getBlock().getType().name()) && isHumanPlaced(event.getBlock())){
			//mp.log.info("lalala "+event.getBlock().getX()+", "+event.getBlock().getY()+", "+event.getBlock().getZ());
			Collection<ItemStack> drops = event.getBlock().getDrops();
			Location loc = event.getBlock().getLocation();
			event.getBlock().setType(Material.getMaterial("AIR"));
			for(ItemStack drop:drops){
				loc.getWorld().dropItem(loc, drop);
			}
		}
	}
}
