package tw.lachu.MineProfession.profession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import tw.lachu.MineProfession.MineProfession;

public class Profession{
	
	protected final String profession;
	private final ConfigurationSection config;
	protected final MineProfession mp;
	
	private ArrayList<ExperienceTrigger> expList;
	private ArrayList<AbilityTrigger> abiList;
	
	public Profession(MineProfession mp, String name, ConfigurationSection conf){
		this.mp = mp;
		this.profession = name;
		this.config = conf;
		this.expList = new ArrayList<ExperienceTrigger>();
		this.abiList = new ArrayList<AbilityTrigger>();
	}
	
	public void load(){
		ConfigurationSection expSection = config.getConfigurationSection("experience");
		Set<String> expSources = expSection.getKeys(false);
		for(String source : expSources){
			try {
				Class<?> clazz = Class.forName("tw.lachu.MineProfession.profession."+source+"Experience");
				ExperienceTrigger trigger = (ExperienceTrigger)clazz.newInstance();
				trigger.load(this, expSection.getConfigurationSection(source));
				expList.add(trigger);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection abiSection = config.getConfigurationSection("abilities");
		Set<String> abilities = abiSection.getKeys(false);
		for(String ability : abilities){
			Class<?> clazz;
			try {
				clazz = Class.forName("tw.lachu.MineProfession.profession."+ability+"Ability");
				AbilityTrigger trigger = (AbilityTrigger)clazz.newInstance();
				trigger.load(this, abiSection.getConfigurationSection(ability));
				abiList.add(trigger);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void onEvent(Event event){
		for(ExperienceTrigger trigger : expList){
			try {
				Method method = trigger.getClass().getMethod("onEvent", event.getClass());
				method.invoke(trigger, event);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		for(AbilityTrigger trigger : abiList){
			try {
				Method method = trigger.getClass().getMethod("onEvent", event.getClass());
				method.invoke(trigger, event);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void onEvent(Event event, Player player){
		for(ExperienceTrigger trigger : expList){
			try {
				Method method = trigger.getClass().getMethod("onEvent", event.getClass(), Player.class);
				method.invoke(trigger, event, player);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		for(AbilityTrigger trigger : abiList){
			try {
				Method method = trigger.getClass().getMethod("onEvent", event.getClass(), Player.class);
				method.invoke(trigger, event, player);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}			
		}
	}
}