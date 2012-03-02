package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTameEvent;

public class TameEntityExperience extends ExperienceTrigger {
	@Override
	public void onEvent(EntityTameEvent event){
		if(event.getOwner() instanceof Player){
			pro.mp.data.gainExperience(((Player)event.getOwner()).getName(), pro.profession, amounts.get(getName(event.getEntity())));
			pro.mp.debug(this, ((Player)event.getOwner()).getName(), event.getEntity().toString(), amounts.get(getName(event.getEntity())));
		}
	}
}
