package me.jared.food.listeners;

import me.jared.food.Sugar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Pepsi implements Listener {
  FileConfiguration config = Sugar.plugin.getConfig();
  
  private int cooldowntime = this.config.getInt("pepsiDelay");
  
  public ItemStack getPepsi() {
    ItemStack is = new ItemStack(Material.ORANGE_DYE);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(this.config.getString("pepsiString").replaceAll("&", "§"));
    is.setItemMeta(im);
    return is;
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    ItemMeta meta = getPepsi().getItemMeta();
    Player p = (Player)e.getPlayer();
    byte b;
    int i;
    ItemStack[] arrayOfItemStack;
    for (i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i; ) {
      ItemStack item = arrayOfItemStack[b];
      if (item != null)
        if (item.getType().equals(getPepsi().getType()))
          if (!item.hasItemMeta()) {
            item.setItemMeta(meta);
          } else if (item.hasItemMeta() && !item.getItemMeta().equals(getPepsi().getItemMeta())) {
            item.setItemMeta(meta);
          }   
      b++;
    } 
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if (player.getInventory().getItemInMainHand().getType() == getPepsi().getType())
      if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        ItemMeta meta = getPepsi().getItemMeta();
        byte b;
        int i;
        ItemStack[] arrayOfItemStack;
        for (i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i; ) {
          ItemStack item = arrayOfItemStack[b];
          if (item != null)
            if (item.getType().equals(Material.ORANGE_DYE)) {
              item.setItemMeta(meta);
              break;
            }  
          b++;
        } 
        if (MountainDew.cooldown.containsKey(player.getUniqueId())) {
          long ticksleft = ((Long)MountainDew.cooldown.get(player.getUniqueId())).longValue() / 50L + this.cooldowntime - System.currentTimeMillis() / 50L;
          if (ticksleft > 0L)
            return; 
        } 
        for (Player p : Bukkit.getOnlinePlayers())
          p.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0F, 1.0F); 
        player.setFoodLevel(20);
        double health = player.getHealth();
        if (health == 20.0D) {
          player.setHealth(20.0D);
        } else if (health < 18.0D) {
          player.setHealth(health + 2.0D);
        } else if (health >= 18.0D) {
          player.setHealth(20.0D);
        } 
        int food = player.getFoodLevel();
        if (food == 20) {
          player.setFoodLevel(20);
        } else if (food < 12) {
          player.setFoodLevel(food + 6);
        } else if (food >= 12) {
          player.setFoodLevel(20);
        } 
        ItemStack is = player.getInventory().getItemInMainHand();
        if (is != null)
          if (is.getAmount() > 1) {
            is.setAmount(is.getAmount() - 1);
          } else {
            player.getInventory().setItemInMainHand(null);
          }  
        MountainDew.cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
      }  
  }
}
