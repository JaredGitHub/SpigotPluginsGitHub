package me.jared.teams;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.jared.TeamMane;
import net.md_5.bungee.api.ChatColor;

public class TeamListener implements Listener
{

	TeamMane plugin;
	public TeamListener(TeamMane pl)
	{
		this.plugin = pl;
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e)
	{
		FileConfiguration config = plugin.getConfig();

		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			Player pdamager = (Player) e.getDamager();

			String damagedTeam = config.getString("players." + p.getName() + ".team");
			String damagerTeam = config.getString("players." + pdamager.getName() + ".team");

			if(damagedTeam.equals(damagerTeam))
			{
				boolean friendlyFire = config.getBoolean(damagedTeam + ".FriendlyFire");
				if(friendlyFire == false)
				{
					pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
					e.setCancelled(true);
				}
			}
		}

		if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player)
		{
			Projectile projectile = (Projectile) e.getDamager();
			Player p = (Player) e.getEntity();

			String damagedTeam = config.getString("players." + p.getName() + ".team");
			Player pdamager = (Player) projectile.getShooter();
			String damagerTeam = config.getString("players." + pdamager.getName() + ".team");

			if(damagedTeam == null || damagerTeam == null) return;
			if(damagedTeam.equals(damagerTeam))
			{
				boolean friendlyFire = config.getBoolean(damagedTeam + ".FriendlyFire");
				if(friendlyFire == false)
				{
					pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
					
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		FileConfiguration config = plugin.getConfig();
		Player p = e.getPlayer();
		ArrayList<String> playerList = new ArrayList<String>(config.getStringList("playerss"));

		if(!playerList.contains(p.getName()))
		{
			playerList.add(p.getName());
			config.set("playerss", playerList);
			plugin.saveConfig();
		}
	}
}

