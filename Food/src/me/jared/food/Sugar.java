package me.jared.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jared.food.listeners.Bandage;
import me.jared.food.listeners.Beans;
import me.jared.food.listeners.BleedRunnable;
import me.jared.food.listeners.BloodBag;
import me.jared.food.listeners.Bones;
import me.jared.food.listeners.CornedBeef;
import me.jared.food.listeners.InfectionCure;
import me.jared.food.listeners.MountainDew;
import me.jared.food.listeners.Pasta;
import me.jared.food.listeners.Pepsi;

public class Sugar extends JavaPlugin implements Listener
{
	public static Sugar plugin;

	public static ArrayList<ItemStack> food = new ArrayList<>();
	public static ArrayList<UUID> bleeders = new ArrayList<UUID>();

	private HashMap<UUID, Long> cooldown = new HashMap<>();

	private int cooldowntime = getConfig().getInt("sugarDelay");

	public void loadConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onEnable()
	{
		plugin = this;
		Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.GREEN) + "Food plugin is here!!");
		getCommand("foodparticles").setExecutor(new ParticleCommand());
		getConfig().addDefault("beanDelay", Integer.valueOf(20));
		getConfig().addDefault("pastaDelay", Integer.valueOf(20));
		getConfig().addDefault("pepsiDelay", Integer.valueOf(20));
		getConfig().addDefault("dewDelay", Integer.valueOf(20));
		getConfig().addDefault("sugarDelay", Integer.valueOf(60));
		getConfig().addDefault("cbDelay", Integer.valueOf(20));
		getConfig().addDefault("icDelay", Integer.valueOf(20));
		getConfig().addDefault("particleAmount", Integer.valueOf(20));
		getConfig().addDefault("sugarString", "&9Speed");
		getConfig().addDefault("pastaString", "&9Canned Pasta");
		getConfig().addDefault("beanString", "&9Canned Beans");
		getConfig().addDefault("pepsiString", "&9Can of Pepsi");
		getConfig().addDefault("dewString", "&9Mountain Dew");
		getConfig().addDefault("cbString", "&9Corned Beef");
		getConfig().addDefault("icString", "&aInfection Cure");
		getConfig().addDefault("bloodbagString", "&4Blood Bag");
		getConfig().addDefault("bonesString", "&bBones");
		getServer().getPluginManager().registerEvents(this, (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new Beans(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new Pasta(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new MountainDew(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new Pepsi(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new CornedBeef(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new InfectionCure(), (Plugin) this);
		
		getServer().getPluginManager().registerEvents((Listener) new BloodBag(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new Bandage(), (Plugin) this);
		getServer().getPluginManager().registerEvents((Listener) new Bones(), (Plugin) this);
		loadConfig();
		
		BleedRunnable bleedRunnable = new BleedRunnable();
		bleedRunnable.runTaskTimer(plugin, 0, 10);
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.RED) + "Food plugin is GONE!");
	}

	public ItemStack getSugar()
	{
		ItemStack sugar = new ItemStack(Material.SUGAR);
		ItemMeta im = sugar.getItemMeta();
		im.setDisplayName(getConfig().getString("sugarString").replaceAll("&", "§"));
		sugar.setItemMeta(im);
		return sugar;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getSugar().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for (i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if (item != null)
				if (item.getType().equals(Material.SUGAR))
				{
					item.setItemMeta(meta);
					break;
				}
			b++;
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() == getSugar().getType())
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemMeta meta = getSugar().getItemMeta();
				byte b;
				int i;
				ItemStack[] arrayOfItemStack;
				for (i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i;)
				{
					ItemStack item = arrayOfItemStack[b];
					if (item != null)
						if (item.getType().equals(Material.SUGAR))
						{
							item.setItemMeta(meta);
							break;
						}
					b++;
				}
				
				if (this.cooldown.containsKey(player.getUniqueId()))
				{
					long ticksleft = ((Long) this.cooldown.get(player.getUniqueId())).longValue() / 50L
							+ this.cooldowntime - System.currentTimeMillis() / 50L;
					if (ticksleft > 0L)
						return;
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0));
				for (Player online : Bukkit.getOnlinePlayers())
				{
					Location loc = player.getEyeLocation();
					putParticle(loc, getSugar());
					online.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
				}
				ItemStack is = player.getInventory().getItemInMainHand();
				if (is != null)
					if (is.getAmount() > 1)
					{
						is.setAmount(is.getAmount() - 1);
					} else
					{
						player.getInventory().setItemInMainHand(null);
					}
				this.cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
			}
	}

	public static void putParticle(Location location, ItemStack item)
	{
		location.getWorld().spawnParticle(Particle.ITEM, location, plugin.getConfig().getInt("particleAmount"),
				0.125D, 0.125D, 0.125D, 0.125D, item, true);
	}
}
