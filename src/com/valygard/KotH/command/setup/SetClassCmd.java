/**
 * SetClassCmd.java is part of King of the Hill.
 * (c) 2014 Anand, All Rights Reserved.
 */
package com.valygard.KotH.command.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.valygard.KotH.command.Command;
import com.valygard.KotH.command.util.CommandInfo;
import com.valygard.KotH.command.util.CommandPermission;
import com.valygard.KotH.command.util.CommandUsage;
import com.valygard.KotH.framework.ArenaManager;

@CommandInfo(
		name = "addclass", 
		pattern = "(add.*|set.*)class.*",
		desc = "Add a new class with your inventory.",
		playerOnly = true
)
@CommandPermission("koth.setup.addclass")
@CommandUsage("/koth addclass <class-name> [-o]")
/**
 * @author Anand
 *
 */
public class SetClassCmd implements Command {

	@Override
	public boolean execute(ArenaManager am, CommandSender sender, String[] args) {
		String newClass = args[0];
		Player p = (Player) sender;
		
		String override = (args.length > 1 ? args[1] : "");
		boolean overwrite = override.startsWith("-o");
		
		am.createClassNode(newClass, p.getInventory(), overwrite);
		return true;
	}


}
