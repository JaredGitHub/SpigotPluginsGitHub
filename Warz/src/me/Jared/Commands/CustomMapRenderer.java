package me.Jared.Commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
			URL url = new URL("https://i.imgur.com/xaWUSlI.png");
			BufferedImage image = ImageIO.read(url);
			canvas.drawImage(0, 0, MapPalette.resizeImage(image));
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
