package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import tw.lachu.util.Chance;

public class ExtraWoolAbility extends AbilityTrigger {
	@Override
	public void onEvent(PlayerShearEntityEvent event){
		if(event.getEntity() instanceof Sheep){
			int result = Chance.contribute(getPower(event.getPlayer().getName()), Chance.ContributeType.LOWER_AMOUNT_HIGHER_CHANCE);
			if(result>0){
				ItemStack bonus = new Wool(((Sheep)event.getEntity()).getColor()).toItemStack(result);
				event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), bonus);
			}
		}
	}
}
