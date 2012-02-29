package tw.lachu.MineProfession.profession;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import tw.lachu.util.Chance;

public class LootingAbility extends AbilityTrigger {
	@Override
	public void onEvent(EntityDeathEvent event, Player player){
		if(!pro.mp.ts.isSet(event.getEntity().getUniqueId()) && types!=null && types.contains(getName(event.getEntity()))){
			List<ItemStack> drops = event.getDrops();
			for(ItemStack drop : drops){
				double expect = drop.getAmount()*getPower(player.getName());
				int result = Chance.contribute(expect, Chance.ContributeType.LOWER_AMOUNT_HIGHER_CHANCE);
				if(result>0){
					ItemStack bonus = new ItemStack(drop.getType(), result);
					bonus.setData(drop.getData());
					event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), bonus);
				}
			}
		}
	}
}
