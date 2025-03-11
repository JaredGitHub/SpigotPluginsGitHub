
package me.Jared;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.WorldEdit;

import me.Jared.Guns.Bullet;
import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;
import me.Jared.Guns.TriggerSwap;
import me.Jared.Guns.WeaponReader;
import me.Jared.Guns.constant.GunValue;
import me.Jared.Listeners.EntityListener;
import me.Jared.Listeners.PlayerListener;
import me.Jared.Utils.RemoveBullets;
import me.Jared.Utils.UtilServer;

public class GunsPlugin extends JavaPlugin
{
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<Gun> loadedGuns = new ArrayList<Gun>();
	private ArrayList<GunPlayer> players = new ArrayList<GunPlayer>();
	public static Random random;
	public static GunsPlugin getPlugin;

	public void onDisable()
	{
		this.clearMemory(true);
	}

	public void onEnable()
	{

		Bukkit.getScheduler().runTaskLater(this, () -> {
			if (WorldGuard.getInstance() != null) {
				Bukkit.getLogger().info("WorldGuard is now initialized!");
			}
		}, 20L); // Delays by 1 second


		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The guns plugin is here!!! Best believe it! SIIIIMPLE!!!");

		new RemoveBullets("world").runTaskTimer(this, 0, 20);
		new RemoveBullets("warz").runTaskTimer(this, 0, 20);

		//Register trigger swap command
		Bukkit.getPluginCommand("triggerswap").setExecutor(new TriggerSwap(this));

		try
		{
			getPlugin = this;
			PluginManager pm = Bukkit.getServer().getPluginManager();
			pm.registerEvents((Listener) new PlayerListener(), (Plugin) this);
			pm.registerEvents((Listener) new EntityListener(), (Plugin) getPlugin);
			this.startup(true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void clearMemory(boolean init)
	{
		Bukkit.getScheduler().cancelTasks((Plugin) getPlugin);
		for (Bullet b : this.bullets)
		{
			b.remove();
		}
		for (GunPlayer gp : this.players)
		{
			gp.unload();
		}
		if (init)
		{
			this.loadedGuns.clear();
		}
		this.bullets.clear();
		this.players.clear();
	}

	public void startup(boolean init)
	{
		random = new Random();
		UtilServer.runTaskTimerAsync(this::UpdateTimer, 20L, 0L);
		if (!new File(this.getPluginFolder()).exists())
		{
			new File(this.getPluginFolder()).mkdir();
		}
		if (!new File(String.valueOf(this.getPluginFolder()) + "/guns").exists())
		{
			new File(String.valueOf(this.getPluginFolder()) + "/guns").mkdir();
		}
		if (!new File(String.valueOf(this.getPluginFolder()) + "/projectile").exists())
		{
			new File(String.valueOf(this.getPluginFolder()) + "/projectile").mkdir();
		}
		if (init)
		{
			this.loadGuns();
			this.loadWarzGuns();
			this.loadProjectile();
		}
		this.getOnlinePlayers();
	}

	public WorldEdit getWorldEdit()
	{
		if (WorldEdit.getInstance() == null)
		{
			throw new NullPointerException();
		}
		return WorldEdit.getInstance();
	}

	private String getPluginFolder()
	{
		return this.getDataFolder().getAbsolutePath();
	}

	public static boolean isProtect(Player pl)
	{
		if (pl.hasPotionEffect(PotionEffectType.INVISIBILITY))
		{
			return true;
		}
		return pl.getLocation().distance(new Location((World) Bukkit.getWorlds().get(0), -731.0, 27.0, -188.0)) < 140.0;
	}

	private void loadProjectile()
	{
		String path = String.valueOf(this.getPluginFolder()) + "/projectile";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null)
		{
			String[] stringArray = children;
			int n = children.length;
			int n2 = 0;
			while (n2 < n)
			{
				String fName = stringArray[n2];
				WeaponReader f = new WeaponReader(this, new File(String.valueOf(path) + "/" + fName), "gun");
				if (f.loaded)
				{
					f.ret.node = "guns." + fName.toLowerCase();
					this.loadedGuns.add(f.ret);
					f.ret.setIsThrowable(true);
				} else
				{
					System.out.println("FAILED TO PROJECTILE PROJECTILE: " + f.ret.getName());
				}
				++n2;
			}
		}
	}

	private void loadGuns()
	{
		String path = String.valueOf(this.getPluginFolder()) + "/guns";
		File dir = new File(path);
		String[] children = dir.list();
		this.loadedGuns.clear();
		if (children != null)
		{
			String[] stringArray = children;
			int n = children.length;
			int n2 = 0;
			while (n2 < n)
			{
				String fName = stringArray[n2];
				WeaponReader f = new WeaponReader(this, new File(String.valueOf(path) + "/" + fName), "gun");
				if (f.loaded)
				{
					f.ret.node = "guns." + fName.toLowerCase();
					this.loadedGuns.add(f.ret);
				} else
				{
					System.out.println("FAILED TO LOAD GUN: " + f.ret.getName());
				}
				++n2;
			}
		}
	}

	private void loadWarzGuns()
	{
		String path = String.valueOf(this.getPluginFolder()) + "/warzguns";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null)
		{
			String[] stringArray = children;
			int n = children.length;
			int n2 = 0;
			while (n2 < n)
			{
				String fName = stringArray[n2];
				WeaponReader f = new WeaponReader(this, new File(String.valueOf(path) + "/" + fName), "gun");
				if (f.loaded)
				{
					f.ret.node = "guns." + fName.toLowerCase();
				} else
				{
					System.out.println("FAILED TO LOAD GUN: " + f.ret.getName());
				}
				++n2;
			}
		}
	}

	public void reload(boolean b)
	{
		this.clearMemory(b);
		this.startup(b);
	}

	public void reload()
	{
		this.reload(false);
	}

	public void getOnlinePlayers()
	{
		for (Player pl : Bukkit.getOnlinePlayers())
		{
			GunPlayer g = new GunPlayer(this, pl);
			this.players.add(g);
		}
	}

	public void UpdateTimer()
	{
		int i = this.players.size() - 1;
		while (i >= 0)
		{
			GunPlayer gp = this.players.get(i);
			if (gp != null)
			{
				UtilServer.runTask(gp::tick);
			}
			--i;
		}
		i = this.bullets.size() - 1;
		while (i >= 0)
		{
			Bullet t = this.bullets.get(i);
			if (t != null)
			{
				UtilServer.runTask(t::tick);
			}
			--i;
		}
	}

	public GunPlayer getGunPlayer(Player player)
	{
		for (GunPlayer gp : this.players)
		{
			if (gp.getPlayer() != player)
				continue;
			return gp;
		}
		return null;
	}

	public Gun getGun(Material mat, GunValue gunValue)
	{
		if (mat == null)
		{
			return null;
		}
		for (Gun g : this.loadedGuns)
		{
			if (g.getGunType() != mat)
				continue;
			return g.copy();
		}
		return null;
	}

	public Gun getGun(String gunName)
	{
		String lowerCased = gunName.toLowerCase();
		for (Gun gun : this.loadedGuns)
		{
			if (!gun.getName().toLowerCase().equals(lowerCased) && !gun.getFilename().toLowerCase().equals(lowerCased))
				continue;
			return gun;
		}
		return null;
	}

	public void onJoin(Player player)
	{
		if (this.getGunPlayer(player) == null)
		{
			GunPlayer gp = new GunPlayer(this, player);
			this.players.add(gp);
		}
	}

	public void onQuit(Player pl)
	{
		for (GunPlayer d : this.players)
		{
			if (d.getPlayer().getEntityId() != pl.getEntityId())
				continue;
			this.players.remove(d);
			return;
		}
	}

	public ArrayList<Gun> getLoadedGuns()
	{
		ArrayList<Gun> ret = new ArrayList<Gun>();
		for (Gun g : this.loadedGuns)
		{
			ret.add(g.copy());
		}
		return ret;
	}

	public void removeBullet(Bullet bullet)
	{
		this.bullets.remove(bullet);
	}

	public void addBullet(Bullet bullet)
	{
		this.bullets.add(bullet);
	}

	public Bullet getBullet(Entity proj)
	{
		int id = proj.getEntityId();
		for (Bullet b : this.bullets)
		{
			if (b.getProjectile().getEntityId() != id)
				continue;
			return b;
		}
		return null;
	}

	public Bullet getBullet(int id)
	{
		for (Bullet b : this.bullets)
		{
			if (b.getProjectile().getEntityId() - id != 0)
				continue;
			return b;
		}
		return null;
	}

	public static Sound getSound(String gunSound)
	{
		String snd = gunSound.toUpperCase().replace(" ", "_");
		return Sound.valueOf(snd);
	}

	public Gun getGunByType(ItemStack item)
	{
		Material mat = item.getType();
		for (Gun g : this.loadedGuns)
		{
			if (g.getGunType() != mat)
				continue;
			return g;
		}
		return null;
	}

	public static String getReloadingTime(Gun gun)
	{
		if (gun.reloading)
		{
			StringBuilder reload = new StringBuilder();
			int scale = 40;
			int bars = Math.round(scale - (float) (gun.gunReloadTimer * scale) / gun.getReloadTime());
			int i = 0;
			while (i < bars)
			{
				reload.append("\u00a7a\u00a7l|");
				++i;
			}
			int left = scale - bars;
			int ii = 0;
			while (ii < left)
			{
				reload.append("\u00a7c\u00a7l|");
				++ii;
			}
			if (!reload.toString().contains("\u00a7c"))
			{
				return "\u00a78\u00a7l\u00a7aReloaded!";
			}
			return reload.toString();
		}
		return "";
	}
}
