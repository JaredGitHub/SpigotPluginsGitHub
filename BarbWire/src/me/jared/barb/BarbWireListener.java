package me.jared.barb;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.jared.BoostPad;
import net.md_5.bungee.api.ChatColor;

public class BarbWireListener implements Listener
{
	BarbWire plugin;

	public BarbWireListener(BarbWire plugin)
	{
		this.plugin = plugin;
	}

	Material web = Material.COBWEB;
	private static int uses;

	public static ItemStack getBarb()
	{
		ItemStack barb = new ItemStack(Material.COBWEB);
		ItemMeta im = barb.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Barbed Wire");
		barb.setItemMeta(im);
		return barb;
	}


	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		ItemStack wireCutters = getShears(p);

		for(ItemStack item : p.getInventory().getContents())
		{
			if(item == null)
			{
				continue;
			}
			if(item.getType().equals(Material.SHEARS))
			{
				if(!(item.hasItemMeta()))
				{
					item.setItemMeta(wireCutters.getItemMeta());
					break;
				} else if(item.getItemMeta().getLore() == null)
				{
					item.setItemMeta(wireCutters.getItemMeta());
				}
			}
			else if(item.getType().equals(Material.COBWEB))
			{
				if(!item.hasItemMeta())
				{
					item.setItemMeta(getBarb().getItemMeta());
					break;
				} else if(item.getItemMeta().getLore() == null)
				{
					item.setItemMeta(getBarb().getItemMeta());
				}
			}
		}
	}


	public static ItemStack getShears(Player p)
	{
		if(p.hasPermission("ranks.default") || !(p.isOp()))
		{
			uses = 6;
		}
		if(p.hasPermission("ranks.vip"))
		{
			uses = 10;
		}
		if(p.hasPermission("ranks.vipplus"))
		{
			uses = 12;
		}
		if(p.hasPermission("ranks.mvp"))
		{
			uses = 15;
		}
		if(p.hasPermission("ranks.mvpplus"))
		{
			uses = 20;
		}
		if(p.hasPermission("ranks.owner"))
		{
			uses = 25;
		}

		ItemStack grapple = new ItemStack(Material.SHEARS);
		ItemMeta im = grapple.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Wire Cutters");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Uses Left - " + ChatColor.GREEN + uses);
		im.setLore(lore);
		grapple.setItemMeta(im);

		return grapple;
	}

	private String getIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ',')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}

		return stringBuilder.toString();
	}

	@EventHandler
	public void onAction(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();

		ArrayList<String> webs = new ArrayList<String>(((Plugin) BoostPad.getInstance()).getConfig().getStringList("webs"));

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals(web))
		{
			if(p.getInventory().getItemInMainHand().getType() == getShears(p).getType())
			{
				ArrayList<Location> locations = new ArrayList<Location>();
				for(String webLoc : webs)
				{
					double x = Double.parseDouble(getIndex(webLoc, 0));
					double y = Double.parseDouble(getIndex(webLoc, 1));
					double z = Double.parseDouble(getIndex(webLoc, 2));
					String world = getIndex(webLoc, 3);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);
					locations.add(loc);
				}

				if(locations.contains(e.getClickedBlock().getLocation()))
				{
					e.getClickedBlock().setType(Material.AIR);
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 2);

					ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());
					Damageable meta = (Damageable) item.getItemMeta();
					meta.setDamage(meta.getDamage() + 1);

					String strLore = meta.getLore().get(0);
					String loreUses = strLore.substring(16);
					Integer number = Integer.valueOf(loreUses);

					if(number <= 1)
					{
						p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					}

					if(p.getWorld().equals(Bukkit.getWorld("warz")))
					{
						number--;
					}
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY + "Uses Left - " + ChatColor.GREEN + number);
					meta.setLore(lore);
					p.getInventory().getItemInMainHand().setItemMeta(meta);
				}
			}
		}
	}
}
