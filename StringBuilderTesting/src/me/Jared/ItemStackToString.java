package me.Jared;

import java.util.ArrayList;

public class ItemStackToString
{
	static ArrayList<Integer> numbers = new ArrayList<Integer>();
	public static void main(String[] args)
	{
		ArrayList<Integer> teams = new ArrayList<Integer>();
		teams.add(0);
		teams.add(1);
		teams.add(2);
		teams.add(3);
		teams.add(4);
		teams.add(5);

		if(teams.get(teams.size()-1) % 2 ==0)
		{
			teams.remove(teams.size()-1);
		}

		for(int i = 0; i < teams.size(); i++)
		{
			if(teams.contains(i+1))
			{
				int team1 = i;
				int team2 = i+1;
				System.out.println("Team " + team1 + " vs Team " + team2);
			}
		}

	}
}