package me.Jared.VaultCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Jared.PersonalVault;
import me.Jared.Menu.PVMenu;

public class VaultCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("UR NOT A PLAYER SO I CAN'T OPEN A MENU FOR YOU");
		}
		else
		{
			Player player = (Player) sender;
			PVMenu pv = new PVMenu(PersonalVault.getInstance().getPlayerMenuUtility(player));
			pv.open();
		}

		return true;
	}

}
