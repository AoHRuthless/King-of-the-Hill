/**
 * HillManager.java is part of King of the Hill.
 * (c) 2014 Anand, All Rights Reserved.
 */
package com.valygard.KotH.hill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import com.valygard.KotH.Messenger;
import com.valygard.KotH.Msg;
import com.valygard.KotH.event.HillChangeEvent;
import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.util.LocationUtil;

/**
 * @author Anand
 * 
 */
public class HillManager {
	// Important classes
	private Arena arena;
	private HillUtils utils;

	// Type of block the hill is, and what it used to be.
	private Material hillType, oldType;

	private HashMap<Location, Material> hillBoundary;

	// Status, varies upon how many hills there are.
	private int status;

	public HillManager(Arena arena) {
		this.arena = arena;
		this.utils = arena.getHillUtils();

		this.hillType = Material.matchMaterial(arena.getSettings().getString(
				"hill-block").toUpperCase());
		
		this.hillBoundary = new HashMap<Location, Material>();

		this.status = utils.getHillRotations() - utils.getRotationsLeft();
	}

	public void begin() {
//		Location hill = utils.getCurrentHill();
		
//		oldType = getBlockType(hill.getBlock());
//		hill.getBlock().setType(hillType);

		status = 1;
		
//		setHillBoundary();
	}

	public void changeHills() {
		// We aren't going to change anymore if this is the last hill.
		if (utils.isLastHill() || !arena.isRunning()) {
			arena.forceEnd();
			return;
		}
		
		HillChangeEvent event = new HillChangeEvent(arena);
		arena.getPlugin().getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			Messenger.info("The hill change was cancelled by an external force.");
			return;
		}
		
		if (utils.isFirstHill() && !utils.isSwitchTime()) {
			begin();
			return;
		}
		
		if (!utils.isSwitchTime()) {
			return;
		}
		
//		revertBlock(utils.getCurrentHill());
//		utils.getNextHill().getBlock().setType(hillType);
		Messenger.info("3");
//		resetHillBoundary();

		Messenger.announce(arena, Msg.HILLS_SWITCHED);

		if (utils.getRotationsLeft() == 1) {
			Messenger.announce(arena, Msg.HILLS_ONE_LEFT);
		}

		// Now, finally, change the status.
		status++;
		// We do this last so it sets boundary of the new hill.
//		setHillBoundary();
	}

	// Hills have a radius, marked by colored wool. The wool color matches the
	// dominant team, and in case of draw or nobody, mark it arbitrary colors.
	public void setHillBoundary() {
		for (Block b : getHillBoundary()) {
			hillBoundary.put(b.getLocation(), b.getType());
			setBlockColor(b);
		}
	}
	
	// Resets the previous hill back to it's original block type.
	public void resetHillBoundary() {
		for (Block b : getHillBoundary()) {
			// If for some odd reason the block isn't in the hashmap
			if (!hillBoundary.containsKey(b.getLocation()))
				continue;
			
			b.setType(hillBoundary.get(b.getLocation()));
			hillBoundary.remove(b.getLocation());
		}
	}
	
	private void setBlockColor(Block b) {
		b.setType(Material.WOOL);
		// Although this is normally dangerous, we just set it's type to wool.
		Wool wool = (Wool) b.getState().getData();
		if (getDominantTeam().equals(arena.getRedTeam()))
			wool.setColor(DyeColor.RED);
		else if (getDominantTeam().equals(arena.getBlueTeam()))
			wool.setColor(DyeColor.BLUE);
		else
			wool.setColor(DyeColor.YELLOW);
	}
	
	public Set<Player> getDominantTeam() {
		if (getRedStrength() > getBlueStrength())
			return arena.getRedTeam();
		else if (getRedStrength() < getBlueStrength())
			return arena.getBlueTeam();
		// By the Trichotomy Property, the only option remaining if the strengths are equal.
		else
			return null;
	}
	
	// Check if a certain player is in the hill.
	public boolean containsPlayer(Player p) {
		Location pLoc = p.getLocation();
		Location l = utils.getCurrentHill();
		
		int radius = arena.getSettings().getInt("hill-radius");
		
		// We don't care about y values.
		if (pLoc.getBlockX() < l.getBlockX() - radius || pLoc.getBlockX() > l.getBlockX() + radius)
			return false;
		
		if (pLoc.getBlockZ() < l.getBlockZ() - radius || pLoc.getBlockZ() > l.getBlockZ() + radius)
			return false;
		
		return true;
	}
	
	public boolean containsLoc(Location loc) {
		Location l = utils.getCurrentHill();
		
		int radius = arena.getSettings().getInt("hill-radius");
		
		// We don't care about y values.
		if (loc.getBlockX() < l.getBlockX() - radius || loc.getBlockX() > l.getBlockX() + radius)
			return false;
		
		if (loc.getBlockZ() < l.getBlockZ() - radius || loc.getBlockZ() > l.getBlockZ() + radius)
			return false;
		
		return true;
	}
	
	public int getPlayerCount() {
		int count = 0;
		for (Player p : arena.getPlayersInArena()) {
			if (containsPlayer(p))
				count++;
		}
		return count;
	}
	
	public int getRedStrength() {
		int count = 0;
		for (Player p : arena.getRedTeam()) {
			if (containsPlayer(p))
				count++;
		}
		return count;
	}
	
	public int getBlueStrength() {
		int count = 0;
		for (Player p : arena.getBlueTeam()) {
			if (containsPlayer(p))
				count++;
		}
		return count;
	}

	public Material getHillType() {
		return hillType;
	}

	public int getHillStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public Map<Location, Material> getBoundary() {
		return hillBoundary;
	}
	
	public Set<Block> getHillBoundary() {
		Set<Block> block = new HashSet<Block>();
		
		Location l = utils.getCurrentHill();
		int radius = arena.getSettings().getInt("hill-radius");
		
		block.clear();
		for (Block b : arena.getSettings().getBoolean("circular-hill") ? LocationUtil.getCircularBoundary(l, radius) : LocationUtil.getSquareBoundary(l, radius)) {
			block.add(b);
		}
        return block;
	}
 	
	public Material getBlockType(Location loc) {
		return getBlockType(loc.getWorld().getBlockAt(loc));
	}
	
	public Material getBlockType(Block block) {
		return (block == null ? Material.AIR : block.getType());
	}
	
	public void revertBlock(Location loc) {
		Material m = oldType;
		Block b = loc.getWorld().getBlockAt(loc);
		if (m.equals(null) || m.equals(Material.AIR)) {
			b.setType(Material.AIR);
		} else {
			b.setType(oldType);
		}
	}
}
