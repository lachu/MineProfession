package tw.lachu.MineProfession;

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
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		String replace=null;
		String time=null;
		{
			String temp = mp.getConfig().getString("drop-replace."+event.getBlock().getType().name());
			if(temp!=null){
				String[] tempArray = temp.split("\\*");
				if(tempArray.length==0){
					replace = "AIR";
					time = "1";
				}else if(tempArray.length==1){
					replace = tempArray[0];
					time = "1";
				}else{
					replace = tempArray[0];
					time = tempArray[1];
				}
			}
		}
		if(replace!=null){
			if(event.getBlock().getWorld()!=null && !event.getPlayer().getItemInHand().containsEnchantment(Enchantment.getById(33))){
				event.getBlock().setType(Material.getMaterial("AIR"));
				ItemStack item = new ItemStack(Material.getMaterial(replace),1);
				Location loc = event.getBlock().getLocation();
				for(int i = Integer.valueOf(time);i>0;--i){
					loc.getWorld().dropItem(loc, item);
				}
			}
		}
	}
}
