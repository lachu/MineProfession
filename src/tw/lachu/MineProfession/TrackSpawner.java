package tw.lachu.MineProfession;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class TrackSpawner implements Listener{
	private HashSet<UUID> spawnerEntity;
	private MineProfession mp;
	public TrackSpawner(MineProfession mp){
		spawnerEntity = new HashSet<UUID>();
		this.mp = mp;
	}
	
	public synchronized void set(UUID id){
		spawnerEntity.add(id);
	}
	
	public synchronized void unset(UUID id){
		spawnerEntity.remove(id);
	}
	
	public synchronized boolean isSet(UUID id){
		return spawnerEntity.contains(id);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSpawn(CreatureSpawnEvent event){
		if("SPAWNER".equals(event.getSpawnReason().name())){
			set(event.getEntity().getUniqueId());
			mp.debug(this, "spawner spawn: "+event.getEntity().getUniqueId().toString());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDeath(EntityDeathEvent event){
		if(isSet(event.getEntity().getUniqueId())){
			mp.debug(this, "spawner spawned killed: "+event.getEntity().getUniqueId().toString());
			unset(event.getEntity().getUniqueId());
		}
	}
}
