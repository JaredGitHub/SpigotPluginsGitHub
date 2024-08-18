package me.Jared;

public class ItemStackToString
{
	public static void main(String[] args)
	{
		int initialKillsToRankup = 10;
		int accumulatedKillsToRankup = 0;
		int rank = 0;
		int kills = 4090;

		for(int i = 1; i < kills; i++)
		{
			accumulatedKillsToRankup *= 1.15;
			accumulatedKillsToRankup += initialKillsToRankup;
			
			if(accumulatedKillsToRankup % i == 0)
			{
				System.out.println(accumulatedKillsToRankup);
				if(accumulatedKillsToRankup >= kills)
				{
					break;
				}
				rank++;
			}
		}
		System.out.println();
		System.out.println(rank);
	}
}
