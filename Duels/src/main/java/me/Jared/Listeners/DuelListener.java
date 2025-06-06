package me.Jared.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

import me.Jared.Duels;
import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.Menus.MapMenu;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class DuelListener implements Listener
{

	//Handle deaths
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		if(DuelCommands.playersInDuel.contains(player) || DuelCommands.betAmount.containsKey(player))
		{
			if(player.getKiller() == null)
			{
				int indexMinusOne = (DuelCommands.playersInDuel.indexOf(player) - 1);
				int indexPlusOne = (DuelCommands.playersInDuel.indexOf(player) + 1);

				boolean inBoundsMinusOne = (indexMinusOne >= 0) 
						&& (indexMinusOne < DuelCommands.playersInDuel.size());

				boolean inBoundsPlusOne = (indexPlusOne >= 0) 
						&& (indexPlusOne < DuelCommands.playersInDuel.size());

				if(inBoundsPlusOne)
				{
					Player dueler = DuelCommands.playersInDuel.get(indexPlusOne);
					dueler.teleport(dueler.getWorld().getSpawnLocation());
					dueler.getInventory().clear();
					DuelCommands.playersInDuel.remove(dueler);
					DuelCommands.betAmount.remove(dueler);
					DuelCommands.playersInDuel.remove(player);
					DuelCommands.betAmount.remove(player);
					return;
				}
				else if(inBoundsMinusOne)
				{
					Player dueler = DuelCommands.playersInDuel.get(indexMinusOne);
					dueler.teleport(dueler.getWorld().getSpawnLocation());
					dueler.getInventory().clear();
					DuelCommands.playersInDuel.remove(dueler);
					DuelCommands.betAmount.remove(dueler);
					DuelCommands.playersInDuel.remove(player);
					DuelCommands.betAmount.remove(player);
					return;
				}
				return;
			}

			String healthLeft = ChatColor.DARK_RED + player.getKiller().getName() 
					+ ChatColor.GRAY + " - " + ChatColor.GREEN + player.getKiller().getHealth() + "/" + "20";

			for(Player online : Bukkit.getOnlinePlayers())
			{
				online.spigot().sendMessage(clickableText(ChatColor.DARK_GREEN + "" + player.getKiller().getName() 
						+ " has won a duel against " + player.getName() + "!", healthLeft));
			}


			//Adds wins to the player
			int wins = Duels.getInstance().getConfig().getInt(player.getKiller().getUniqueId() + ".wins");
			Duels.getInstance().getConfig().set(player.getKiller().getUniqueId() + ".wins", wins + 1);
			Duels.getInstance().saveConfig();
			
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), 
					"givegems " + player.getKiller().getName() + " " + DuelCommands.betAmount.get(player.getKiller()));

			//clear any chickens on their head
			removeChickenOnHead(player.getKiller());

			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), 
					"removegems " + player.getName() + " " + DuelCommands.betAmount.get(player.getKiller()));

			player.getKiller().teleport(player.getWorld().getSpawnLocation());
		}
		DuelCommands.playersInDuel.remove(player.getKiller());
		DuelCommands.playersInDuel.remove(player);
		DuelCommands.betAmount.remove(player);
		DuelCommands.betAmount.remove(player.getKiller());
		DuelCommands.mapNumber.remove(player);
		DuelCommands.mapNumber.remove(player.getKiller());
	}

	private void removeChickenOnHead(Player killer)
	{
		if (killer != null) {
			for (Entity passenger : killer.getPassengers()) {
				killer.removePassenger(passenger); // Detach passenger
				passenger.remove(); // Optional: remove from world if you want to kill the chicken
			}
		}
	}

	private TextComponent clickableText(String chatText, String hoverText)
	{
		TextComponent message = new TextComponent(chatText);
		Text hoverMessage = new Text(hoverText);

		message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverMessage));

		return message;
	}

	//Handle quitting
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if((DuelCommands.playersInDuel.contains(player)) || DuelCommands.betAmount.containsKey(player))
		{
			int indexMinusOne = (DuelCommands.playersInDuel.indexOf(player) -1);
			int indexPlusOne = (DuelCommands.playersInDuel.indexOf(player) +1);

			boolean inBoundsMinusOne = (indexMinusOne >= 0) 
					&& (indexMinusOne < DuelCommands.playersInDuel.size());

			boolean inBoundsPlusOne = (indexPlusOne >= 0) 
					&& (indexPlusOne < DuelCommands.playersInDuel.size());

			if(inBoundsPlusOne)
			{
				Player dueler = DuelCommands.playersInDuel.get(indexPlusOne);
				dueler.teleport(dueler.getWorld().getSpawnLocation());
				dueler.getInventory().clear();
				DuelCommands.playersInDuel.remove(dueler);
				DuelCommands.betAmount.remove(dueler);
				DuelCommands.playersInDuel.remove(player);
				DuelCommands.betAmount.remove(player);
				return;
			}
			else if(inBoundsMinusOne)
			{
				Player dueler = DuelCommands.playersInDuel.get(indexMinusOne);
				dueler.teleport(dueler.getWorld().getSpawnLocation());
				dueler.getInventory().clear();
				DuelCommands.playersInDuel.remove(dueler);
				DuelCommands.betAmount.remove(dueler);
				DuelCommands.playersInDuel.remove(player);
				DuelCommands.betAmount.remove(player);
				return;
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() == null) return;

		InventoryHolder holder = e.getInventory().getHolder();

		if(holder instanceof DuelsMenu)
		{
			if(e.getCurrentItem() == null) return;

			DuelsMenu menu = (DuelsMenu) holder;
			menu.handleMenu(e);

			if(e.getWhoClicked().getOpenInventory().getBottomInventory().getType().equals(InventoryType.PLAYER))
			{
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onCommandEvent(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();
		if(DuelCommands.playersInDuel.contains(player) 
				|| DuelCommands.betAmount.containsKey(player))
		{
			if(!player.hasPermission("duels"))
			{
				if((e.getMessage().equalsIgnoreCase("/duel leave")))
				{
					return;
				}
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot do commands while in a duel!");
			}
		}
	}
}
