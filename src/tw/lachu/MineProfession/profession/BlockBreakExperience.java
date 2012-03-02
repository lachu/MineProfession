package tw.lachu.MineProfession.profession;

import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakExperience extends ExperienceTrigger {
	@Override
	public void onEvent(BlockBreakEvent event){
		pro.toString();
		pro.mp.toString();
		pro.mp.getConfig().toString();
		pro.mp.getConfig().getStringList("track-placement");
		if(!(pro.mp.getConfig().getStringList("track-placement").contains(event.getBlock().getType().name()))
		|| !pro.mp.tp.isHumanPlaced(event.getBlock())){
			pro.mp.data.gainExperience(event.getPlayer().getName(), pro.profession, amounts.get(event.getBlock().getType().name()));
			pro.mp.debug(this, event.getPlayer().getName(), pro.profession, event.getBlock().getType().name(), amounts.get(event.getBlock().getType().name()));
		}
	}
}
