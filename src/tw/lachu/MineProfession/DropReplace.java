package tw.lachu.MineProfession;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DropReplace implements Listener {
	private MineProfession mp;
	public DropReplace(MineProfession mp){
		this.mp = mp;
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getWorld()==null || event.getPlayer().getItemInHand().containsEnchantment(Enchantment.getById(33))){
			return;
		}
		if(mp.getConfig().getString("drop-replace."+event.getBlock().getType().name())==null || mp.getConfig().getString("drop-replace."+event.getBlock().getType().name()).length()==0){
			return;
		}
		
		String[] items = mp.getConfig().getString("drop-replace."+event.getBlock().getType().name()).split("&");
		for(String item:items){
			item = item.trim();
			String[] temp = item.split("\\*");
			String replace = temp[0].trim();
			String time = temp[1].trim();
			ItemStack drop = new ItemStack(Material.getMaterial(replace), Integer.valueOf(time));
			event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), drop);
		}
		
	}
}
