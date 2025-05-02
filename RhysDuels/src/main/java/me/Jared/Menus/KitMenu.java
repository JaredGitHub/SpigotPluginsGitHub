package me.Jared.Menus;

import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Duels;
import me.Jared.Manager.ConfigManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static me.Jared.Manager.ConfigManager.iconCreate;

public class KitMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;

	public KitMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);

	}

	@Override
	public String getMenuName()
	{
		return "Choose your kit!";
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

	FileConfiguration config = Duels.getInstance().getConfig();

	@Override
	public void handleMenu(InventoryClickEvent e)
	{

		Player player = (Player) e.getWhoClicked();
		String mapName = ConfigManager.getMaps().get(MapMenu.getMapNumber());
		String kit = e.getCurrentItem().getItemMeta().getDisplayName();
		armorNumber = e.getSlot();


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
		ArrayList<String> kitNames = new ArrayList<String>(config.getStringList("kits"));

		int i = 0;
		for(String kitName : kitNames)
		{
			if(kitName.contains("-"))
			{
				UUID uuid = UUID.fromString(kitName);
				Player player = Bukkit.getPlayer(uuid);
				assert player != null;
				iconCreate(new ItemStack(Material.DIAMOND_SWORD), player.getName() + "'s kit", inventory, i);
			}
			else
			{
				iconCreate(new ItemStack(Material.DIAMOND_SWORD), kitName, inventory, i);
			}

			i++;
		}
	}
}
