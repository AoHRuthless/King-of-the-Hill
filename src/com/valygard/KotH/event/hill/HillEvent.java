/**
 * HillEvent.java is part of King Of The Hill.
 */
package com.valygard.KotH.event.hill;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Cancellable;

import com.valygard.KotH.event.KotHEvent;
import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.hill.Hill;
import com.valygard.KotH.hill.HillManager;
import com.valygard.KotH.hill.HillTask;

/**
 * @author Anand
 * 
 */
public abstract class HillEvent extends KotHEvent implements Cancellable {
	protected HillManager hm;
	protected HillTask timer;

	protected ConfigurationSection hills;

	protected boolean cancelled;

	public HillEvent(final Arena arena) {
		super(arena);

		hm = arena.getHillManager();
		timer = arena.getHillTimer();

		hills = arena.getWarps().getConfigurationSection("hills");

		cancelled = false;
	}

	/**
	 * Gets the current hill in the arena.
	 * 
	 * @return the current hill.
	 * @since v1.2.5
	 */
	public Hill getCurrentHill() {
		return hm.getCurrentHill();
	}

	/**
	 * Gets the next hill in an arena.
	 * 
	 * @return the no next hill.
	 * @since v1.2.5
	 */
	public Hill getNextHill() {
		return hm.getNextHill();
	}

	/**
	 * Grabs all the Hills in the Arena.
	 * 
	 * @return a list of hills.
	 * @since v1.2.12
	 */
	public List<Hill> getAllHills() {
		return hm.getHills();
	}

	/**
	 * Retrieves an already existing instance of the HillManager.
	 * 
	 * @return HillManager
	 * @since v1.2.5
	 */
	public HillManager getHillManager() {
		return hm;
	}

	/**
	 * Obtains an existing instance of HillTask for modification and registry.
	 * 
	 * @return an instance of HillTask
	 * @since v1.2.5
	 */
	public HillTask getTimer() {
		return timer;
	}

	/**
	 * Obtains where all the arena hills are stored.
	 * 
	 * @return a ConfigurationSection
	 * @since 1.2.5
	 */
	public ConfigurationSection getHills() {
		return hills;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}

}
