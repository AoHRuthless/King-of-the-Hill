/**
 * ScoreboardManager.java is part of King of the Hill.
 */
package com.valygard.KotH;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.valygard.KotH.framework.Arena;

/**
 * @author Anand
 * 
 */
public class ScoreboardManager {
	// Arena stuff
	private Arena arena;
	private boolean enabled;

	// The scoreboard itself
	private Scoreboard scoreboard;

	// sidebar objective
	private Objective sidebar;

	// two teams (Red and Blue)
	private Team redteam, blueteam;

	// Red score, blue score, and time remaining
	private Score red, blue, timeLeft;

	// Amount of red players and blue players in the hill
	private Score redStr, blueStr;

	/**
	 * Create a new scoreboard for the given arena.
	 * 
	 * @param arena
	 *            an arena
	 */
	public ScoreboardManager(Arena arena) {
		this.arena = arena;
		this.enabled = arena.getSettings().getBoolean("use-scoreboard");
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		// sidebar
		sidebar = scoreboard.registerNewObjective(ChatColor.YELLOW
				+ "Arena Stats", "dummy");

		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

		red = sidebar.getScore(ChatColor.DARK_RED + "[Red Team]");
		blue = sidebar.getScore(ChatColor.DARK_BLUE + "[Blue Team]");
		timeLeft = sidebar.getScore(ChatColor.YELLOW + "Time left -");

		redStr = sidebar.getScore(ChatColor.RED + "[Red Control]");
		blueStr = sidebar.getScore(ChatColor.BLUE + "[Blue Control]");

		// teams
		redteam = scoreboard.registerNewTeam("red");
		blueteam = scoreboard.registerNewTeam("blue");

		redteam.setPrefix("" + ChatColor.RED);
		blueteam.setPrefix("" + ChatColor.BLUE);
	}

	/**
	 * Unregister the scoreboard for a player by removing it from the sidebar
	 * and setting them to the server scoreboard (null).
	 * 
	 * @param p
	 *            the player
	 */
	public void removePlayer(Player p) {
		String name = p.getName();

		if (redteam.getEntries().contains(name))
			redteam.removeEntry(name);
		if (blueteam.getEntries().contains(name))
			blueteam.removeEntry(name);
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	/**
	 * Add a point to either the red or blue team. If the parameter is true, it
	 * will give the point to the red team. If false, it will give the point to
	 * the blue team.
	 * 
	 * @param red
	 *            whether or not the team is red.
	 */
	public void addPoint(boolean red) {
		if (red)
			this.red.setScore(this.red.getScore() + 1);
		else
			this.blue.setScore(this.blue.getScore() + 1);
	}

	/**
	 * Initialize the scoreboard by resetting the objective and setting all
	 * scores to 0.
	 * 
	 * @param p
	 *            the player
	 */
	public void initialize(Player p, Team team) {
		if (!enabled)
			return;

		p.setScoreboard(scoreboard);
		team.addEntry(p.getName());

		// minecraft or spigot have trouble initializing zero values sometimes.
		red.setScore(8);
		blue.setScore(8);
		timeLeft.setScore(8);

		red.setScore(0);
		blue.setScore(0);
		timeLeft.setScore(arena.getSettings().getInt("arena-time"));
	}

	/**
	 * Removes the scoreboard by deleting it for all players.
	 */
	public void unregister() {
		for (Player p : arena.getPlayersInArena()) {
			if (p.getScoreboard().equals(scoreboard))
				removePlayer(p);
		}
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Get the time left of the game in seconds.
	 * 
	 * @return
	 */
	public Score getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Set the time remaining
	 * 
	 * @param time
	 *            in seconds.
	 */
	public void setTimeleft(int time) {
		timeLeft.setScore(time);
	}

	/**
	 * Get the amount of red players in the hill.
	 * 
	 * @return
	 */
	public Score getRedStrength() {
		return redStr;
	}

	/**
	 * Update the amount of players from each team in the hill.
	 */
	public void updateStrengths() {
		redStr.setScore(arena.getHillManager().getRedStrength());
		blueStr.setScore(arena.getHillManager().getBlueStrength());
	}

	/**
	 * Get the amount of blue players in the hill.
	 * 
	 * @return
	 */
	public Score getBlueStrength() {
		return blueStr;
	}

	/**
	 * Get the red team.
	 * 
	 * @return scoreboard team
	 */
	public Team getRedTeam() {
		return redteam;
	}

	/**
	 * Get the blue team.
	 * 
	 * @return scoreboard team
	 */
	public Team getBlueTeam() {
		return blueteam;
	}
}
