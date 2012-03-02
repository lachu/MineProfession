package tw.lachu.MineProfession.profession;

import org.bukkit.event.player.PlayerShearEntityEvent;

public class PlayerShearEntityExperience extends ExperienceTrigger {
	@Override
	public void onEvent(PlayerShearEntityEvent event){
		pro.mp.data.gainExperience(event.getPlayer().getName(), pro.profession, amounts.get(getName(event.getEntity())));
		pro.mp.debug(this, event.getPlayer().getName(), pro.profession, amounts.get(getName(event.getEntity())));
	}
}
