package tw.lachu.MineProfession.profession;

import org.bukkit.entity.Entity;

public abstract class Trigger {
	public String getName(Entity entity){
		return entity.toString().split("\\{")[0];
	}
}
