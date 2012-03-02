package tw.lachu.MineProfession.profession;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tw.lachu.util.Chance;

public class StrongAttackAbility extends AbilityTrigger {
	@Override
	public void onEvent(EntityDamageByEntityEvent event){
		if(types!=null && types.contains(getName(event.getEntity())) && event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
			Player player = (Player)event.getDamager();
			LivingEntity entity = (LivingEntity)event.getEntity();
			double expect = getPower(player.getName())*event.getDamage();
			int result = Chance.contribute(expect, Chance.ContributeType.CLOSET_TO_EXPECT);
			if(result>entity.getHealth()){
				result = entity.getHealth();
			}
			if(result>0){
				entity.damage(result, player);
			}
			pro.mp.debug(this, player.getName(), event.getDamage(), result);
		}
	}
}
