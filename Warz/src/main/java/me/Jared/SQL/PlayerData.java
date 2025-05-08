package me.Jared.SQL;

public class PlayerData
{
	int id;
	String uuid;
	int x;
	int y;
	int z;
	float yaw;
	float pitch;
	double health;
	String inventory;


	@Override
	public String toString()
	{
		return "PlayerData [uuid=" + uuid + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw
				+ ", pitch=" + pitch + ", health=" + health + ", inventory=" + inventory + "]";
	}
	
	public String getInventory()
	{
		return inventory;
	}
	public void setInventory(String inventory)
	{
		this.inventory = inventory;
	}
	
	public String getUuid()
	{
		return uuid;
	}
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
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
