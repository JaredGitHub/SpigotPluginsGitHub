package me.Jared.Loot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class InventorySaver
{
	/**
	 * A method to serialize an inventory to Base64 string.
	 *
	 * <p />
	 *
	 * Special thanks to Comphenix in the Bukkit forums or also known as aadnk on GitHub.
	 *
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 *
	 * @param inventory to serialize
	 * @return Base64 string of the provided inventory
	 * @throws IllegalStateException
	 */
	public static String toBase64(Inventory inventory) throws IllegalStateException
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());

			// Save every element in the list
			for(int i = 0; i < inventory.getSize(); i++)
			{
				dataOutput.writeObject(inventory.getItem(i));
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch(Exception e)
		{
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * Gets an array of ItemStacks from Base64 string.
	 *
	 * <p />
	 *
	 * Base off of {@link #fromBase64(String)}.
	 *
	 * @param data Base64 string to convert to ItemStack array.
	 * @return ItemStack array created from the Base64 string.
	 * @throws IOException
	 */
	public static ItemStack[] fromBase64(String data) throws IOException
	{
		if(data == null)
		{
			return null;
		} else
		{
			try
			{
				ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
				BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
				ItemStack[] items = new ItemStack[dataInput.readInt()];

				// Read the serialized inventory
				for(int i = 0; i < items.length; i++)
				{
					items[i] = (ItemStack) dataInput.readObject();
				}

				dataInput.close();
				return items;
			} catch(ClassNotFoundException e)
			{
				throw new IOException("Unable to decode class type.", e);
			}
		}
	}

}
