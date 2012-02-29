package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureBreedExperience extends ExperienceTrigger{
	@Override
	public void onEvent(CreatureSpawnEvent event, Player player){
		if("CUSTOM".equals(event.getSpawnReason().name())){
			pro.mp.data.gainExperience(player.getName(), pro.profession, amounts.get(event.getCreatureType().name()));
		}
	}
}
