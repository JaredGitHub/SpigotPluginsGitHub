package me.Jared.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerManager
{
	private ArrayList<Player> players;
	private GameManager gameManager;

	private int maxPlayers;

	private ArrayList<ItemStack> guns;
	private HashMap<UUID, Integer> kills;

	public PlayerManager(GameManager gameManager)
	{
		this.setGameManager(gameManager);
		this.players = new ArrayList<Player>();
		this.kills = new HashMap<UUID, Integer>();
		for(Player p : players)
		{
			kills.put(p.getUniqueId(), 0);
		}
		
		this.maxPlayers = ConfigManager.getPlayersNeeded();

		this.guns = new ArrayList<ItemStack>();

		//Pistols
		guns.add(new ItemStack(Material.WOODEN_SHOVEL));
		guns.add(new ItemStack(Material.STONE_SHOVEL));
		guns.add(new ItemStack(Material.IRON_SHOVEL));
		guns.add(new ItemStack(Material.DIAMOND_SHOVEL));
		//Shotguns
		guns.add(new ItemStack(Material.WOODEN_PICKAXE));
		guns.add(new ItemStack(Material.STONE_PICKAXE));
		guns.add(new ItemStack(Material.IRON_PICKAXE));
		guns.add(new ItemStack(Material.DIAMOND_PICKAXE));
		guns.add(new ItemStack(Material.GOLDEN_AXE));
		//Snipers
		guns.add(new ItemStack(Material.WOODEN_AXE));
		guns.add(new ItemStack(Material.STONE_AXE));
		guns.add(new ItemStack(Material.IRON_AXE));
		guns.add(new ItemStack(Material.DIAMOND_AXE));
		guns.add(new ItemStack(Material.NETHER_STAR));
		//Automatics
		guns.add(new ItemStack(Material.WOODEN_HOE));
		guns.add(new ItemStack(Material.STONE_HOE));
		guns.add(new ItemStack(Material.IRON_HOE));
		guns.add(new ItemStack(Material.DIAMOND_HOE));
		guns.add(new ItemStack(Material.STICK));
		//SMGS
		guns.add(new ItemStack(Material.GOLDEN_HOE));
		guns.add(new ItemStack(Material.GOLDEN_PICKAXE));
		guns.add(new ItemStack(Material.GOLDEN_SHOVEL));
		guns.add(new ItemStack(Material.DIAMOND));
		
		//The golden knife!!
		ItemStack goldenKnife = new ItemStack(Material.GOLDEN_SWORD);
		ItemMeta meta = goldenKnife.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Golden Knife");
		goldenKnife.setItemMeta(meta);
		goldenKnife.addUnsafeEnchantment(Enchantment.SHARPNESS, 100);
		
		guns.add(goldenKnife);

	}

	public ArrayList<ItemStack> getGuns()
	{
		return guns;
	}
	public HashMap<UUID, Integer> getKills()
	{
		return kills;
	}

	public void giveGuns(Player player, int kills)
	{
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		player.getInventory().setItem(4, new ItemStack(Material.COMPASS));
		
		player.getInventory().addItem(gameManager.getPlayerManager().getGuns().get(kills));
		if(kills >= 0 && kills < 4)
		{
			//Pistols

			ItemStack item = new ItemStack(Material.ENDER_PEARL,128);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "Pistol Ammo");
			item.setItemMeta(meta);
			player.getInventory().addItem(item);


		}
		else if(kills >= 4 && kills < 9)
		{
			//Shotguns

			ItemStack item = new ItemStack(Material.WHEAT_SEEDS,128);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "Shotgun Shells");
			item.setItemMeta(meta);

			player.getInventory().addItem(item);


		}
		else if(kills >= 9 && kills < 14)
		{
			//Snipers

			ItemStack item = new ItemStack(Material.CLAY_BALL,128);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "Sniper Bullets");
			item.setItemMeta(meta);

			player.getInventory().addItem(item);


		}
		else if(kills >= 14 && kills < 19)
		{
			//Automatics

			ItemStack item = new ItemStack(Material.FLINT,256);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "Automatic Ammo");
			item.setItemMeta(meta);

			player.getInventory().addItem(item);


		}
		else if(kills >= 19 && kills <= 23)
		{
			//SMGs

			ItemStack item = new ItemStack(Material.FLINT,256);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "Automatic Ammo");
			item.setItemMeta(meta);

			player.getInventory().addItem(item);

		}
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public int getPlayerCount()
	{
		return players.size();
	}

	public void setPlayerInGame(Player player)
	{
		players.add(player);
	}

	public void removePlayer(Player player)
	{
		players.remove(player);
	}
	
	public void removePlayers()
	{
		this.getPlayers().clear();
	}

	public ArrayList<Player> getPlayers()
	{
		return players;
	}

	public void sendMessage(String string)
	{
		for (Player player : getPlayers())
		{
			player.sendMessage(string);
		}
	}

	public void teleportPlayerInGame()
	{
		int i = 0;
		for (Player player : getPlayers())
		{
			i++;
			player.teleport(ConfigManager.getGameSlotLocation(i));
		}
	}

	public GameManager getGameManager()
	{
		return gameManager;
	}

	public void setGameManager(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

}
