/**
 * AutoEndTimer.java is part of King of the Hill.
 */
package com.valygard.KotH.time;

import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.messenger.Messenger;
import com.valygard.KotH.messenger.Msg;

/**
 * Self-contained countdown timer which automatically ends the arena on
 * completion and runs for the course of the game.
 * <p>
 * If the timer is manually halted, the arena will end immediately and the
 * winner will be determined.
 * 
 * @author Anand
 * 
 */
public class AutoEndTimer extends CountdownTimer implements TimerCallback {

	private Arena arena;
	private int seconds;
	
	private TimerCallback callback;

	/**
	 * Default constructor for the end timer initialises by arena and duration.
	 * 
	 * @param arena
	 *            the arena for the timer
	 * @param seconds
	 *            the duration of the timer in seconds
	 */
	public AutoEndTimer(Arena arena, int seconds) {
		super(arena.getPlugin(), Conversion.toTicks(seconds));
		super.setCallback(this);
		
		this.arena = arena;
		this.seconds = seconds;
		
		this.callback = new IntervalCallback(arena, this, Msg.ARENA_AUTO_END, new int[] { 1, 2,
				3, 5, 10, 20, 30, 45, 60, 120, 180, 300, 600, 900, 1200 });
	}

	/**
	 * Returns the amount of seconds left in the game.
	 * 
	 * @return an int
	 */
	public int getRemaining() {
		return seconds;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Update duration to proper timing
	 */
	@Override
	public synchronized void onStart() {
		this.seconds = arena.getSettings().getInt("arena-time");
		setDuration(Conversion.toTicks(seconds));
		Messenger.announce(arena, Msg.ARENA_START);
		callback.onStart();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The arena is ended when the timer is completed.
	 */
	@Override
	public synchronized void onFinish() {
		callback.onFinish();
		this.seconds = arena.getSettings().getInt("arena-time");
		setDuration(Conversion.toTicks(seconds));
		arena.endArena();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Series of checks to ensure the arena should stay running. If the score
	 * cap is reached, the arena is finished and the timer is terminated.
	 */
	@Override
	public synchronized void onTick() {
		// Abort if the arena isn't running.
		if (!arena.isRunning()) {
			return;
		}

		/*
		 * End the arena and halt the timer if the score to win is reached or
		 * there are no more players in the arena.
		 */
		if (arena.getPlayersInArena().isEmpty()
				|| arena.getBlueTeam().isEmpty()
				|| arena.getRedTeam().isEmpty()) {
			super.stop();
			return;
		}

		if (arena.scoreReached()) {
			super.stop();
			return;
		}
		callback.onTick();
		seconds--;
	}
}