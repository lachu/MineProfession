package tw.lachu.MineProfession.profession;

import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakExperience extends ExperienceTrigger {
	@Override
	public void onEvent(BlockBreakEvent event){
		if(!pro.mp.getConfig().getList("track-placement").contains(event.getBlock().getType().name())
		|| !pro.mp.tp.isHumanPlaced(event.getBlock())){
			pro.mp.data.gainExperience(event.getPlayer().getName(), pro.profession, amounts.get(event.getBlock().getType().name()));
		}
	}
}
