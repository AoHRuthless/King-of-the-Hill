/**
 * ArenaPlayerJoinEvent.java is part of King of the Hill.
 */
package com.valygard.KotH.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.valygard.KotH.framework.Arena;

/**
 * @author Anand
 * 
 */
public class ArenaPlayerJoinEvent extends ArenaPlayerEvent implements
		Cancellable {
	private boolean cancelled;

	public ArenaPlayerJoinEvent(Arena arena, Player player) {
		super(arena, player);

		this.cancelled = false;
	}

	/**
	 * Gets whether or not the player is to join the lobby or the spectator
	 * area.
	 * 
	 * @return a location
	 * @since v1.2.5
	 */
	public Location getPlayerJoinOrSpec() {
		if (arena.isRunning()) {
			return arena.getSpec();
		}
		return arena.getLobby();
	}

	/**
	 * Finds the distance the player is to either the lobby or spec area. This
	 * method is now deprecated, use {@link #getDistanceToArena()}
	 * 
	 * @return a double representing the distance
	 * @since v1.2.5, deprecated v1.2.11.3
	 */
	@Deprecated
	public double getDistance() {
		return getDistanceToArena();
	}

	/**
	 * Returns the player's distance to the arena.
	 * 
	 * @return a double distance
	 * @since v1.2.11.3
	 */
	public double getDistanceToArena() {
		return player.getLocation().distance(getPlayerJoinOrSpec());
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		cancelled = value;
	}

}
