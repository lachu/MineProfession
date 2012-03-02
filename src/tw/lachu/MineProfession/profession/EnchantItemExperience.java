package tw.lachu.MineProfession.profession;

import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItemExperience extends ExperienceTrigger {
	
	private double getValue(String name){
		Double value = amounts.get(name);
		return value==null?1:value;
	}
	
	@Override
	public void onEvent(EnchantItemEvent event){
		Map<Enchantment, Integer> rankMap = event.getEnchantsToAdd();
		Set<Enchantment> enchants = rankMap.keySet();
		double expAmount = 0;
		for(Enchantment ench : enchants){
			expAmount += getValue(ench.getName())*rankMap.get(ench);
		}
		String[] itemMaterials = event.getItem().getType().name().split("_");
		for(String material : itemMaterials){
			expAmount *= getValue(material);
		}
		expAmount *= getValue("CONST");
		pro.mp.data.gainExperience(event.getEnchanter().getName(), pro.profession, expAmount);
		pro.mp.debug(this, event.getEnchanter().getName(), pro.profession, expAmount);
	}
}
