package me.Jared.Menus;

import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.KitManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;

	public ArmorMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);

	}

	@Override
	public String getMenuName()
	{
		return "Choose your armor!";
	}

	@Override
	public int getSlots()
	{
		return 9;
	}

	private static int armorNumber;
	public static int getArmorNumber()
	{
		return armorNumber;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{

		Player player = (Player) e.getWhoClicked();
		String mapName = ConfigManager.getMaps().get(MapMenu.getMapNumber());
		String kit = "Chainmail";
		switch(e.getSlot())
		{
		case 3:
			//Chainmail armor
			armorNumber = 1;
			kit = "Chainmail";
			break;
		case 4:
			//Iron armor
			armorNumber = 2;
			kit = "Iron";
			break;
		case 5:
			//Diamond armor
			armorNumber = 3;
			kit = "Diamond";
			break;

		default:
			break;
		}



		TextComponent text = new TextComponent(ChatColor.GREEN + player.getName() + " is inviting you to a duel in map " + ChatColor.WHITE + "'" + mapName + "'" + ChatColor.GREEN + " with a kit of " + kit + " and a bet of " + DuelCommands.betAmount.get(player));

		TextComponent accept = new TextComponent(ChatColor.GREEN + "ACCEPT");
		TextComponent deny = new TextComponent(ChatColor.RED + "DENY");

		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + player.getName()));
		deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel deny " + player.getName()));
		accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept duel!")));
		deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to deny duel!")));

		text.addExtra(ChatColor.GRAY + " [");
		text.addExtra(accept);
		text.addExtra(ChatColor.GRAY + "/");
		text.addExtra(deny);
		text.addExtra(ChatColor.GRAY + "]");

		DuelCommands.duelPlayer.spigot().sendMessage(text);
		player.sendMessage(ChatColor.GREEN + "Invitation sent to " + DuelCommands.duelPlayer.getName() + " with a kit of " + kit + " and the map " + mapName + "!");
		player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		DuelCommands.duelPlayer.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

		player.closeInventory();
		DuelCommands.duelPlayer.closeInventory();
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.CHAINMAIL_CHESTPLATE), ChatColor.GRAY + "Chain", inventory, 3);
		ConfigManager.iconCreate(new ItemStack(Material.IRON_CHESTPLATE), ChatColor.WHITE + "Iron", inventory, 4);
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND_CHESTPLATE), ChatColor.BLUE + "Diamond", inventory, 5);
	}
}
