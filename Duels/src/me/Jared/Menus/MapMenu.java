package me.Jared.Menus;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.Duels;
import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MapMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;

	public MapMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.GREEN + "Pick a map!";
	}

	@Override
	public int getSlots()
	{
		return 27;
	}
	
	private static int mapNumber;
	public static int getMapNumber()
	{
		return mapNumber;
	}

	FileConfiguration config = Duels.getInstance().getConfig();
	
	public static ArrayList<Player> playersInDuel = new ArrayList<Player>();
	public static ArrayList<Player> playersToDuel = new ArrayList<Player>();
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		playersToDuel.remove(DuelCommands.duelPlayer);
		playersInDuel.remove(p);
		p.closeInventory();

		String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

		if(config.getStringList("maps").size() == 0)
		{
			p.sendMessage(ChatColor.RED + "There are no maps yet, add one with /duel set [mapName] [1 or 2]");
		}
		
		for(String mapName : config.getStringList("maps"))
		{
			if(itemName.equals(mapName))
			{
				TextComponent text = new TextComponent(ChatColor.GREEN + p.getName() + " is inviting you to a duel in map " + ChatColor.WHITE + "'" + mapName + "'" + ChatColor.GREEN + " with a bet of " + DuelCommands.betAmount.get(p));
				
				TextComponent accept = new TextComponent(ChatColor.GREEN + "ACCEPT");
				TextComponent deny = new TextComponent(ChatColor.RED + "DENY");
				
				accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + p.getName()));
				deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel deny " + p.getName()));
				accept.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("Click to accept duel!")));
				deny.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("Click to deny duel!")));

				text.addExtra(ChatColor.GRAY + " [");
				text.addExtra(accept);
				text.addExtra(ChatColor.GRAY + "/");
				text.addExtra(deny);
				text.addExtra(ChatColor.GRAY + "]");

				DuelCommands.duelPlayer.spigot().sendMessage(text);
				p.sendMessage(ChatColor.GREEN + "Invitation sent to " + DuelCommands.duelPlayer.getName() + " for map " + mapName + "!");
				p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				DuelCommands.duelPlayer.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				
				playersToDuel.add(DuelCommands.duelPlayer);
				playersInDuel.add(p);
				
				mapNumber = config.getStringList("maps").indexOf(mapName);
			}
		}
	}

	@Override
	public void setMenuItems()
	{
		ArrayList<String> mapNames = new ArrayList<String>(config.getStringList("maps"));

		int i = 0;
		for(String mapName : mapNames)
		{
			iconCreate(new ItemStack(Material.SPAWNER), mapName, inventory, i);
			i++;
		}
	}

	//Icon creator
	private void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);

	}

}
