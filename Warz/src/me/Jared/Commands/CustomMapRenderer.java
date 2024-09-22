package me.Jared.Commands;

import org.bukkit.Material;
import org.bukkit.World;
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
		
//		try
//		{
//			URL url = new URL("https://i.imgur.com/xaWUSlI.png");
//			BufferedImage image = ImageIO.read(url);
//			canvas.drawImage(0, 0, MapPalette.resizeImage(image));
//		} catch(IOException e)
//		{
//			e.printStackTrace();
//		}
		
		
		World world = player.getWorld();
        int centerX = map.getCenterX();
        int centerZ = map.getCenterZ();
        int scale = 128; // Number of pixels for a full map (at scale 1:1)

        // Loop through every pixel on the map and set the color based on the world data
        for (int x = 0; x < scale; x++) {
            for (int z = 0; z < scale; z++) {
                // Convert map pixel coordinates to world block coordinates
                int worldX = centerX + (x - scale / 2) * map.getScale().getValue();
                int worldZ = centerZ + (z - scale / 2) * map.getScale().getValue();

                // Get the highest block at the given coordinates
                int highestY = world.getHighestBlockYAt(worldX, worldZ);

                // Get the material at that block
                Material material = world.getBlockAt(worldX, highestY - 1, worldZ).getType();

                // Determine map color based on the block material
                MapPalette.Color color = getColorForMaterial(material);

                // Set the pixel on the map
                canvas.setPixel(x, z, color.getId());
            }
        }
	}
}
