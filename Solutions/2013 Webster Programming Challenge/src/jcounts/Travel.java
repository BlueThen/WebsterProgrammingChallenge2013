package jcounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Problem B: Travel
 * @author Jared Counts
 *
 */

//I threw in their 3 example inputs in the beginning of travel.in for an actual case to test against


public class Travel {
	static Scanner scanner;
	public static void main(String args[]) {
		try {
			scanner = new Scanner(new File("data/travel.in"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*
		 * Given: Number of plans, then number of options for the first plan
		 * 	after we loop through each option
		 * then the number of options for second plan
		 * etc.
		 * 
		 * We just need to find the plan with the lowest cost/(city*distance) and return its city count
		 */
		int plans = scanner.nextInt();
		// loop through each plan
		for (int p = 0; p < plans; p++) {
			int options = scanner.nextInt();
			int bestOption = 0;
			// it's safe to assume that none of the costs will be more than Float.MAX_VALUE, I think.
			float lowestCost = Float.MAX_VALUE;
			
			for (int o = 0; o < options; o++) {
				int cities = scanner.nextInt();
				float distance = scanner.nextFloat();
				float cost = scanner.nextFloat();
				
				float costPerCityMile = cost / (distance * (float)cities);
				
				if (costPerCityMile < lowestCost) {
					lowestCost = costPerCityMile;
					bestOption = cities;
				}
			}
			
			System.out.println("Plan " + (p+1) + ": " + bestOption);
		}
	}
}
