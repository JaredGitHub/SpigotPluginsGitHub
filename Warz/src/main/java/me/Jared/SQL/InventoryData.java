package me.Jared.SQL;

public class InventoryData
{
	 String[] items;
	 
	 public void setItem(int slot, String itemData)
	 {
		 items[slot] = itemData;
	 }
	 
	 public String[] getItems()
	 {
		 return items;
	 }
}
