package tw.lachu.MineProfession.profession;

import java.util.Collection;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import tw.lachu.util.Chance;

public class FortuneAbility extends AbilityTrigger {
	@Override
	public void onEvent(BlockBreakEvent event){
		if(!pro.mp.getConfig().getList("track-placement").contains(event.getBlock().getType().name())
		|| !pro.mp.tp.isHumanPlaced(event.getBlock())){
			if(types!=null && types.contains(event.getBlock().getType().name())){
				Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());
				for(ItemStack drop : drops){
					double expect = drop.getAmount()*getPower(event.getPlayer().getName());
					int result = Chance.contribute(expect, Chance.ContributeType.LOWER_AMOUNT_HIGHER_CHANCE);
					if(result>0){
						ItemStack bonus = drop.getData().toItemStack(result);
						event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), bonus);
					}
					pro.mp.debug(this, event.getPlayer().getName(), drop.getType().name(), result);
				}
			}
		}
	}
}
