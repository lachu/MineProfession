package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackEntityExperience extends ExperienceTrigger {
	@Override
	public void onEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player && !pro.mp.ts.isSet(event.getEntity().getUniqueId())){
			pro.mp.data.gainExperience(((Player)event.getDamager()).getName(), pro.profession, event.getDamage()*amounts.get(getName(event.getEntity())));
		}
	}
}
