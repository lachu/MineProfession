package tw.lachu.MineProfession.profession;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;

import tw.lachu.util.Chance;

public class BetterEnchantmentAbility extends AbilityTrigger {
	@Override
	public void onEvent(EnchantItemEvent event){
		double power = getPower(event.getEnchanter().getName());
		Map<Enchantment, Integer> rankMap = event.getEnchantsToAdd();
		Set<Enchantment> enchants = rankMap.keySet();
		for(Enchantment ench: enchants){
			if(ench.getMaxLevel() > rankMap.get(ench) && rankMap.get(ench) > 0){
				if(Chance.happen(power)){
					rankMap.put(ench, rankMap.get(ench)+1);
					event.getEnchanter().sendMessage(ChatColor.AQUA+"Enchantment Table emits mysterious radiance.");
				}
				power /= 2;
			}
		}
		if(Chance.happen(power)){
			Enchantment[] allEnch = Enchantment.values();
			ArrayList<Enchantment> available = new ArrayList<Enchantment>();
			for(Enchantment ench : allEnch){
				if(ench.canEnchantItem(event.getItem()) && (rankMap.get(ench)==null || rankMap.get(ench)==0)){
					available.add(ench);
				}
			}
			Enchantment enchToAdd = available.get((new Random()).nextInt(available.size()));
			int maxLevel = enchToAdd.getMaxLevel();
			double expect = ((double)maxLevel-1.01)/3;
			int result = Chance.contribute(expect, Chance.ContributeType.LOWER_AMOUNT_HIGHER_CHANCE)+1;
			result = Math.min(result, enchToAdd.getMaxLevel());
			rankMap.put(enchToAdd, result);
			event.getEnchanter().sendMessage(ChatColor.AQUA+"Enchantment Table emits dazzling light.");
		}
	}
}