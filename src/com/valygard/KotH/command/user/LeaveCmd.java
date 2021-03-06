/**
 * LeaveCmd.java is part of King of the Hill.
 */
package com.valygard.KotH.command.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.valygard.KotH.command.Command;
import com.valygard.KotH.command.CommandInfo;
import com.valygard.KotH.command.CommandPermission;
import com.valygard.KotH.command.CommandUsage;
import com.valygard.KotH.framework.ArenaManager;
import com.valygard.KotH.messenger.Messenger;
import com.valygard.KotH.messenger.Msg;

@CommandInfo(
		name = "leave", 
		pattern = "leave.*|quit.*",
		desc = "Leave an arena.",
		playerOnly = true,
		argsRequired = 0
)
@CommandPermission("koth.user.leave")
@CommandUsage("/koth leave")
/**
 * @author Anand
 *
 */
public class LeaveCmd implements Command {

	@Override
	public boolean execute(ArenaManager am, CommandSender sender, String[] args) {
		if (am.getArenaWithPlayer((Player) sender) == null) {
			Messenger.tell(sender, Msg.LEAVE_NOT_PLAYING);
			return true;
		}
		
		am.getArenaWithPlayer((Player) sender).removePlayer((Player) sender, false); 
		return true;
	}

}
