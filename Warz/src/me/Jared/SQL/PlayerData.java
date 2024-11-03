package me.Jared.SQL;

public class PlayerData
{
	int id;
	String uuid;
	String world;
	int x;
	int y;
	int z;
	float yaw;
	float pitch;
	double health;
	
	
	
	@Override
	public String toString()
	{
		return "PlayerData [id=" + id + ", uuid=" + uuid + ", world=" + world + ", x=" + x + ", y=" + y + ", z=" + z
				+ ", yaw=" + yaw + ", pitch=" + pitch + ", health=" + health + "]";
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getUuid()
	{
		return uuid;
	}
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
	public String getWorld()
	{
		return world;
	}
	public void setWorld(String world)
	{
		this.world = world;
	}
	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}
	public int getZ()
	{
		return z;
	}
	public void setZ(int z)
	{
		this.z = z;
	}
	public float getYaw()
	{
		return yaw;
	}
	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}
	public float getPitch()
	{
		return pitch;
	}
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
	public double getHealth()
	{
		return health;
	}
	public void setHealth(double health)
	{
		this.health = health;
	}

	
	

}
