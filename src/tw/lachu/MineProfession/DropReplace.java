package tw.lachu.MineProfession;

import org.bukkit.Location;
import org.bukkit.Material;
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
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		String replace = mp.getConfig().getString("drop-replace."+event.getBlock().getType().name());
		if(replace!=null){
			if(event.getBlock().getWorld()!=null){
				event.getBlock().setType(Material.getMaterial("AIR"));
				ItemStack item = new ItemStack(Material.getMaterial(replace),1);
				Location loc = event.getBlock().getLocation();
				loc.getWorld().dropItem(loc, item);
			}
		}
	}
}
