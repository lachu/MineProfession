package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;

import tw.lachu.util.Chance;

public class IndomitablePetAbility extends AbilityTrigger {
	@Override
	public void onEvent(EntityDamageEvent event, Player player){
		if(event.getEntity() instanceof Tameable){
			Tameable animal = (Tameable)event.getEntity();
			if(animal.isTamed()){
				double expect = event.getDamage()*getPower(player.getName());
				int result = Chance.contribute(expect, Chance.ContributeType.CLOSET_TO_EXPECT);
				if(result>0){
					event.setDamage(event.getDamage()-result);
				}
				pro.mp.debug(this, player.getName(), event.getDamage()+result, result);
			}
		}
	}
}
