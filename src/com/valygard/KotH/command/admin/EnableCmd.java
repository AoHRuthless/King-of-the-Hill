/**
 * EnableCmd.java is part of King of the Hill.
 * (c) 2014 Anand, All Rights Reserved.
 */
package com.valygard.KotH.command.admin;

import org.bukkit.command.CommandSender;

import com.valygard.KotH.Messenger;
import com.valygard.KotH.Msg;
import com.valygard.KotH.command.Command;
import com.valygard.KotH.command.util.CommandInfo;
import com.valygard.KotH.command.util.CommandPermission;
import com.valygard.KotH.command.util.CommandUsage;
import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.framework.ArenaManager;

/**
 * @author Anand
 *
 */
public class EnableCmd implements Command {

	@CommandInfo(
			name = "enable", 
			pattern = "enable|on",
			desc = "Enable specific arenas or all of KotH.",
			argsRequired = 0
	)
	@CommandPermission("koth.admin.enable")
	@CommandUsage("/koth enable [arena|all]")
	@Override
	public boolean execute(ArenaManager am, CommandSender sender, String[] args) {
		if (args.length == 0) {
			am.setEnabled(true);
		    Messenger.tell(sender, "KotH enabled.");
		    Messenger.info("KotH has been enabled. Note: This is overriden by per-arena settings.");
		}
		
		if (args[0].equalsIgnoreCase("all")) {
			am.setEnabled(true);
			for (Arena arena : am.getArenas())
				arena.setEnabled(true);
			Messenger.tell(sender, "You have enabled all arenas.");
		}
		else {
			Arena arena = am.getArenaWithName(args[0]);
			if (arena == null) {
				Messenger.tell(sender, Msg.ARENA_NULL);
				return false;
			}
			
			am.setEnabled(true);
			arena.setEnabled(true);
			Messenger.tell(sender, "You have enabled '" + arena.getName() + "'.");
		}
		return true;
	}

}