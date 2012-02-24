package tw.lachu.MineProfession;

import java.util.Collection;

import org.bukkit.Location;
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
		Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());
		Location loc = event.getBlock().getLocation();
		for(ItemStack drop:drops){
			String replace;
			String time;
			replace = mp.getConfig().getString("drop-replace."+drop.getType().name());
			ItemStack item;
			if(replace!=null){
				String[] tempArray = replace.split("\\*");
				replace = tempArray[0];
				time = tempArray[1];
				item = new ItemStack(Material.getMaterial(replace), drop.getAmount()*Integer.valueOf(time));
			}else{
				item = drop;
			}
			loc.getWorld().dropItem(loc, item);
		}
		event.getBlock().setType(Material.getMaterial("AIR"));
	}
}
