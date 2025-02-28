package me.Jared.Commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CustomMapRenderer extends MapRenderer
{
	@Override
	public void render(MapView map, MapCanvas canvas, Player player)
	{
		try
		{
			URI uri = new URI("https://i.imgur.com/xaWUSlI.png");
			try(InputStream inputStream = uri.toURL().openStream())
			{
				BufferedImage image = ImageIO.read(inputStream);
				if(image != null)
				{
					canvas.drawImage(0, 0, MapPalette.resizeImage(image));
				} else
				{
					System.err.println("Failed to load image: ImageIO.read returned null");
				}
			}
		} catch(IOException e)
		{
			System.err.println("I/O error while loading image: " + e.getMessage());
			e.printStackTrace();
		} catch(Exception e)
		{
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
