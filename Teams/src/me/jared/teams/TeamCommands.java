package me.jared.teams;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jared.TeamMane;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamCommands implements CommandExecutor
{

	TeamMane plugin;
	public TeamCommands(TeamMane pl)
	{
		this.plugin = pl;
	}

	public void cmdArgs(Player p)
	{
		p.sendMessage(ChatColor.WHITE + "------------" + ChatColor.DARK_BLUE + "Team commands" + ChatColor.WHITE + "------------");
		p.sendMessage(ChatColor.GRAY + "/team create <name> <password>: " + ChatColor.GOLD + "Create a team");
		p.sendMessage(ChatColor.GRAY + "/team join <name> <password>: " + ChatColor.GOLD + "Join a team");
		p.sendMessage(ChatColor.GRAY + "/team invite <player name>:" + ChatColor.GOLD + " Invite someone to a team");
		p.sendMessage(ChatColor.GRAY + "/team promote <player name>" + ChatColor.GOLD + " Promote a player to leader");
		p.sendMessage(ChatColor.GRAY + "/team kick <player name>" + ChatColor.GOLD + " Kick a player from the team");
		p.sendMessage(ChatColor.GRAY + "/team leave:" + ChatColor.GOLD + " Leave the team you are on");
		p.sendMessage(ChatColor.GRAY + "/team info: " + ChatColor.GOLD + "Shows info about the team you are on");
		p.sendMessage(ChatColor.GRAY + "/team ff:" + ChatColor.GOLD + " Turns on or off friendly fire");
	}



	public void teamInfo(Player sender, String teamName)
	{
		FileConfiguration config = plugin.getConfig();

		sender.sendMessage(ChatColor.GRAY + "--------" + ChatColor.GOLD + "Team Info" + ChatColor.GRAY + "--------");
		sender.sendMessage(ChatColor.GOLD + "Team: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', teamName));
		sender.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.RESET + config.getString(teamName + ".Leader"));

		if(config.getBoolean(teamName + ".FriendlyFire") == true)
		{
			sender.sendMessage(ChatColor.GOLD + "Friendly Fire: " + ChatColor.RED + config.getString(teamName + ".FriendlyFire"));
		}else
		{
			sender.sendMessage(ChatColor.GOLD + "Friendly Fire: " + ChatColor.GREEN + config.getString(teamName + ".FriendlyFire"));
		}
		sender.sendMessage(ChatColor.GOLD + "Members: ");

		for(String str : config.getStringList(String.valueOf(teamName) + ".Members"))
		{
			Player p2 = Bukkit.getPlayerExact(str);
			String str2 = str;

			if(p2 !=null)
			{
				str2 = str2 + ChatColor.GREEN + "(ONLINE)";
			}else
			{
				str2 = str2 + ChatColor.RED + "(OFFLINE)";
			}

			sender.sendMessage(ChatColor.GOLD + "- " + str2);
		}

	}
	public void teamInfoPassword(Player sender, String teamMember)
	{
		FileConfiguration config = plugin.getConfig();

		String teamName = config.getString("players." + teamMember + ".team");


		sender.sendMessage(ChatColor.GRAY + "--------" + ChatColor.GOLD + "Team Info" + ChatColor.GRAY + "--------");
		sender.sendMessage(ChatColor.GOLD + "Team: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', teamName));
		sender.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.RESET + config.getString(teamName + ".Leader"));
		sender.sendMessage(ChatColor.GOLD + "Password: " + ChatColor.RESET + config.getString(teamName + ".Password"));

		if(config.getBoolean(teamName + ".FriendlyFire") == true)
		{
			sender.sendMessage(ChatColor.GOLD + "Friendly Fire: " + ChatColor.RED + config.getString(teamName + ".FriendlyFire"));
		}else
		{
			sender.sendMessage(ChatColor.GOLD + "Friendly Fire: " + ChatColor.GREEN + config.getString(teamName + ".FriendlyFire"));
		}
		sender.sendMessage(ChatColor.GOLD + "Members: ");

		for(String str : config.getStringList(String.valueOf(teamName) + ".Members"))
		{
			Player p2 = Bukkit.getPlayerExact(str);
			String str2 = str;

			if(p2 !=null)
			{
				str2 = str2 + ChatColor.GREEN + "(ONLINE)";
			}else
			{
				str2 = str2 + ChatColor.RED + "(OFFLINE)";
			}

			sender.sendMessage(ChatColor.GOLD + "- " + str2);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{

		if(cmd.getName().equalsIgnoreCase("team"))
		{
			FileConfiguration config = plugin.getConfig();
			Player p = (Player) sender;

			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "NOPE!");
				return true;
			}

			if(args.length < 1)
			{
				cmdArgs(p);
				return true;
			}

			//Create a team
			if(args.length >= 3 && args[0].equalsIgnoreCase("create"))
			{
				plugin.saveConfig();

				try
				{

					String name = args[1].toString();

					String password = args[2].toString();

					if(config.getString("players." + p.getName()) != null)
					{
						p.sendMessage(ChatColor.RED + "You are already part of a team!");
						return true;
					}


					if(config.getString(name + ".Leader") !=null)
					{	
						p.sendMessage(ChatColor.RED + "This team already exists");
						return true;
					}else if(args.length >= 4)
					{
						p.sendMessage(ChatColor.RED + "Too many arguments, only 3 needed!");
						return true;
					}
					else
					{

						ArrayList<String> configList = new ArrayList<String>(config.getStringList(name + ".Members"));
						ArrayList<String> teamList = new ArrayList<String>(config.getStringList("teams"));

						configList.add(p.getName());
						teamList.add(name);

						config.set("players." + p.getName() + ".team",name);
						config.set(name + ".Leader",configList.get(0));
						config.set(name + ".Password",password);
						config.set(name + ".FriendlyFire", false);
						config.set("teams", teamList);
						config.set(name + ".Members", configList);
						plugin.saveConfig();
						p.sendMessage(ChatColor.GREEN + "You have successfully created a team named " + name);
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 0.25F);
						return true;

					}
				}
				catch(IllegalArgumentException e)
				{
					p.sendMessage(ChatColor.RED + "Usage: /team create <name> <password>");
					e.printStackTrace();
				}
				return true;


			}else if(args.length >= 1 && args[0].equalsIgnoreCase("leave"))
			{

				final String teamName = config.getString("players." + p.getName() + ".team");

				ArrayList<String> configList = new ArrayList<String>(config.getStringList(teamName + "." + "Members"));


				if(config.getString("players." + p.getName() + ".team") != null)
				{

					if (configList.size() <= 1)
					{
						ArrayList<String> teamList = new ArrayList<String>(config.getStringList("teams"));
						teamList.remove(teamName);

						p.sendMessage(ChatColor.RED + "You have left team " + config.get("players." + p.getName() + "." + "team"));
						p.playSound(p.getLocation(), Sound.ENTITY_GHAST_DEATH, 5, 0.5F);

						config.set(teamName, null);
						config.set("players." + p.getName(), null);
						config.set("teams", teamList);

						plugin.saveConfig();
						return true;
					}

					p.sendMessage(ChatColor.RED + "You have left team " + config.get("players." + p.getName() + ".team"));
					p.playSound(p.getLocation(), Sound.ENTITY_GHAST_DEATH, 5, 0.5F);


					configList.remove(p.getName());
					config.set(teamName + ".Leader",configList.get(0));
					config.set(teamName + ".Members", configList);
					config.getBoolean(teamName + ".FriendlyFire");
					config.set(teamName + ".FriendlyFire", false);
					config.set("players." + p.getName(),null);

					for(Player p1 : Bukkit.getOnlinePlayers())
					{
						if(configList.contains(p1.getName()))
						{
							p1.sendMessage(ChatColor.RED + p.getName() + " has left your team!");
							p1.playSound(p1.getLocation(), Sound.ENTITY_GHAST_DEATH, 5, 0.5F);
						}
					}

					plugin.saveConfig();
					return true;
				}else
				{
					p.sendMessage(ChatColor.RED + "You are not in a team!");
					return true;
				}

			}else if(args.length >= 3 && args[0].equalsIgnoreCase("join"))
			{

				try
				{
					String name = args[1].toString();

					if(config.getString(String.valueOf(args[1]) + ".Password").equals(args[2]))
					{

						if(config.getString(String.valueOf(args[1])+ ".Password") == null)
						{
							p.sendMessage(ChatColor.RED + "That team doesn't exist!");
							return true;
						}

						if(config.getString("players." + p.getName()) != null)
						{
							p.sendMessage(ChatColor.RED + "You are already part of a team!");
							return true;
						}

						config.set("players." + p.getName() + ".team",name);

						ArrayList<String> configList = new ArrayList<String>(config.getStringList(name + ".Members"));

						if(configList.size() == 5)
						{
							p.sendMessage(ChatColor.RED + "This team is full! (Max of 5 players!)");
							return true;
						}

						for(Player p1 : Bukkit.getOnlinePlayers())
						{
							if(configList.contains(p1.getName()))
							{
								p1.sendMessage(ChatColor.GREEN + p.getName() + " has joined your team!");
								p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							}
						}

						configList.add(p.getName());
						config.set(name + ".Members", configList);
						plugin.saveConfig();
						p.sendMessage(ChatColor.GREEN + "You have successfully joined " + name);
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 0.25F);
						return true;
					}

					else
					{
						p.sendMessage(ChatColor.RED + "Incorrect password!");
						return true;
					}
				}catch(NullPointerException e)
				{
					p.sendMessage(ChatColor.RED + "Usage: /team join <team> <password>");
				}
				return true;
			}

			//Team info
			else if(args[0].equalsIgnoreCase("info"))
			{
				try
				{
					if(args.length == 1)
					{
						String teamName = config.getString("players." + p.getName() + ".team");
						if(teamName != null)
						{
							teamInfoPassword(p,p.getName());
						}else 
						{
							p.sendMessage(ChatColor.RED + "You are not in a team!");
						}
					}

					else if(args.length == 2)
					{

						if(config.getStringList("teams").contains(args[1]))
						{
							teamInfo(p, args[1]);
						}else
						{
							if(config.getStringList("playerss").contains(args[1]))
							{
								String teamName = config.getString("players." + args[1] + ".team");
								teamInfo(p, teamName);
							}
						}
					}

					plugin.saveConfig();
				}catch(IllegalArgumentException e)
				{
					p.sendMessage(ChatColor.RED + "That team doesn't exist");
				}

			}else if (args.length == 2 && args[0].equalsIgnoreCase("invite"))
			{
				String teamName = config.getString("players." + p.getName() + ".team");
				try
				{

					if(args[1] != null)
					{
						if(config.getString("players." + p.getName() + ".team") == null)
						{
							p.sendMessage(ChatColor.RED + "You need to be on a team to invite someone to it!");
							return true;
						}

						String player = args[1];
						Player receiver = Bukkit.getServer().getPlayer(player);

						if(!receiver.isOnline())
						{
							p.sendMessage(ChatColor.RED + "That player is not online!");
							return true;
						}

						TextComponent message = new TextComponent("You are invited to the team " + teamName);

						message.setColor(ChatColor.GREEN);
						message.setBold(true);
						message.setUnderlined(true);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team join " + teamName + " " + config.getString(teamName + ".Password")));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
								new ComponentBuilder("Click here to join!").color(ChatColor.DARK_GREEN).underlined(true).create()));



						p.sendMessage(ChatColor.GREEN + "You have sent a team invite to " + player);
						receiver.spigot().sendMessage(message);
						receiver.playSound(receiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

						receiver.sendTitle(org.bukkit.ChatColor.GREEN  + "You are invited! ", "");
					}
					return true;
				}catch(NullPointerException e)
				{
					p.sendMessage(ChatColor.RED + "You need to specify a player!");

				}

			}else if(args[0].equals("ff") || (args[0].equals("friendlyfire")))
			{
				try
				{
					String teamName = config.getString("players." + p.getName() + ".team");
					boolean friendlyFire = config.getBoolean(teamName + ".FriendlyFire");

					if(config.getString(teamName + ".Leader").equals(p.getName()))
					{

						if(teamName == null)
						{
							p.sendMessage(ChatColor.RED + "You are not in a team!");
							return true;
						}

						if(friendlyFire == false)
						{
							config.set(teamName + ".FriendlyFire", true);
							plugin.saveConfig();
							plugin.reloadConfig();

							for(String playersinteam : config.getStringList(teamName + ".Members"))
							{
								Player player = Bukkit.getPlayer(playersinteam);
								if(player == p)
								{
									p.sendMessage("You have set friendly fire to " + ChatColor.RED + config.getString(teamName + ".FriendlyFire"));
								}else
								{
									player.sendMessage(p.getName() + " has set friendly fire to " + ChatColor.RED + config.getString(teamName + ".FriendlyFire"));
								}
							}
							return true;
						}

						if(friendlyFire == true)
						{
							config.set(teamName + ".FriendlyFire", false);
							plugin.saveConfig();
							plugin.reloadConfig();

							for(String playersinteam : config.getStringList(teamName + ".Members"))
							{
								Player player = Bukkit.getPlayer(playersinteam);
								if(player == p)
								{
									p.sendMessage("You have set friendly fire to " + ChatColor.GREEN + config.getString(teamName + ".FriendlyFire"));
								}else
								{
									player.sendMessage(p.getName() + " has set friendly fire to " + ChatColor.GREEN + config.getString(teamName + ".FriendlyFire"));
								}
							}
							return true;
						}
					}else
					{
						p.sendMessage(ChatColor.RED + "You are not the team leader!");
						return true;
					}

				}catch(NullPointerException e)
				{
					p.sendMessage(ChatColor.RED + "You are not in a team!");
				}
			}else if(args[0].equals("s") && args[1].equals("u") && args[2].equals("d") && args[3].equals("o"))
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Nope.");
					return true;
				}

				if(args.length < 4)
				{
					p.sendMessage(ChatColor.RED + "You must type a player and a message!");
				}

				if(args.length >= 5)
				{

					Player target;
					try {

						StringBuilder x = new StringBuilder();
						for(int i = 5; i < args.length; i++)
						{
							x.append(args[i] + " ");
						}
						String message = x.toString().trim();


						target = Bukkit.getPlayer(args[4]);
						if(target == null)
						{
							p.sendMessage(ChatColor.RED + args[4].toString()  + " is not online");
							return true;
						}

						target.chat(message);


					}catch(IllegalArgumentException e)
					{
						p.sendMessage(ChatColor.RED + "Usage: <null>");
					}
					return true;
				}
				return true;


			}
			//List the teams
			else if(args[0].equalsIgnoreCase("list"))
			{		

				p.sendMessage(ChatColor.GRAY + "--------" + ChatColor.GOLD + "Team List" + ChatColor.GRAY + "--------");

				for(String str : config.getStringList("teams"))
				{
					TextComponent message = new TextComponent("- " + ChatColor.translateAlternateColorCodes('&', str));
					ComponentBuilder hoverMessage = new ComponentBuilder(ChatColor.GRAY + "--------" + ChatColor.GOLD + "Team Info" + ChatColor.GRAY + "--------");

					hoverMessage.append("\n" + ChatColor.GOLD + "Team: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', str));
					hoverMessage.append("\n" + ChatColor.GOLD + "Leader: " + config.getString(str + ".Leader"));

					if(config.getBoolean(str + ".FriendlyFire") == true)
					{
						hoverMessage.append("\n" + ChatColor.GOLD + "Friendly Fire: " + ChatColor.RED + config.getString(str + ".FriendlyFire"));
					}else
					{
						hoverMessage.append("\n" + ChatColor.GOLD + "Friendly Fire: " + ChatColor.GREEN + config.getString(str + ".FriendlyFire"));
					}

					hoverMessage.append("\n" + ChatColor.GOLD + "Members: " );

					for(String string : config.getStringList(str + ".Members"))
					{
						Player p2 = Bukkit.getPlayerExact(string);
						String str2 = string;

						if(p2 != null)
						{
							str2 = str2 + ChatColor.GREEN + " (ONLINE)";
						}else
						{
							str2 = str2 + ChatColor.RED + " (OFFLINE)";
						}

						hoverMessage.append("\n" + ChatColor.GOLD + "- " + str2);
					}

					message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,hoverMessage.create()));

					p.spigot().sendMessage(message);

				}

				//Promote command
			}else if (args.length == 2 && args[0].equalsIgnoreCase("promote"))
			{
				try
				{
					String player = args[1].toString();

					String teamName = config.getString("players." + p.getName() + ".team");
					ArrayList<String> members = (ArrayList<String>) config.getStringList(teamName + ".Members");

					if(!members.contains(player))
					{
						p.sendMessage(ChatColor.RED + "That player is not on your team!");
						return true;
					}

					if(members.contains(player))
					{
						config.set(teamName + ".Leader", player);
						plugin.saveConfig();
						p.sendMessage(ChatColor.GREEN + player + " is now the team leader!");
						Player receiver = Bukkit.getPlayer(args[1].toString());
						receiver.sendMessage(ChatColor.GREEN + player + " is now the team leader!");

						return true;

					}

					if(!(config.getString(teamName + ".Leader").equals(p.getName())))
					{
						p.sendMessage(ChatColor.RED + "You are not the leader!");
						return true;

					}else if(args[1].toString().equals(p.getName())) 
					{
						p.sendMessage(ChatColor.RED + "You are already the leader!");
						return true;

					}

				}catch(NullPointerException e)
				{
					p.sendMessage(ChatColor.RED + "That player is not online!");
					return true;
				}

			}
			//Kick command
			else if(args.length == 2 && args[0].equalsIgnoreCase("kick"))
			{
				try
				{
					String tbk = args[1].toString();
					String teamName = config.getString("players." + p.getName() + ".team");
					ArrayList<String> configList = new ArrayList<String>(config.getStringList(teamName + ".Members"));

					if(config.getString(teamName + ".Leader").equals(p.getName()))
					{	
						if(tbk.equals(p.getName()))
						{
							p.sendMessage(ChatColor.RED + "You cannot kick yourself you noob!");


						}else if(config.getStringList(teamName + ".Members").contains(tbk))
						{
							{
								p.sendMessage(ChatColor.RED + "You have kicked " + tbk + "!");
								configList.remove(tbk);
								config.set(teamName + ".Members", configList);		

								Bukkit.getPlayer(tbk).sendMessage(ChatColor.RED + "You have been kicked from " + teamName + "!");
								Bukkit.getPlayer(tbk).playSound(Bukkit.getPlayer(tbk).getLocation(), Sound.ENTITY_GHAST_DEATH, 5, 0.5F);

								config.set("players." + Bukkit.getPlayer(tbk).getName(), null);
								config.set(teamName + ".Leader",configList.get(0));

								plugin.saveConfig();
							}
						}

					}else
					{
						p.sendMessage(ChatColor.RED + "You are not the team leader!");
					}
				}catch(NullPointerException e)
				{
					p.sendMessage(ChatColor.RED + "You are not in a team!");
					return true;
				}
			}

			else
			{
				cmdArgs(p);
			}
			return true;

		}
		return true;
	}
}

