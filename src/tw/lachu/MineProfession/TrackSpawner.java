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
	public void set(UUID id){
		spawnerEntity.add(id);
	}
	
	public void unset(UUID id){
		spawnerEntity.remove(id);
	}
	
	public boolean isSet(UUID id){
		return spawnerEntity.contains(id);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
		if("SPAWNER".equals(event.getSpawnReason().name())){
			set(event.getEntity().getUniqueId());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDeath(EntityDeathEvent event){
		unset(event.getEntity().getUniqueId());
	}
}
