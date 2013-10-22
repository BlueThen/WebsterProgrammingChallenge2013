package jcounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Problem A: Multiply
 * @Author Jared Counts
 * 
 */


//  Tips for speedy programming:
//  ctrl+SPACE -> display options for auto completion
//  type "sysout", press ctrl+SPACE -> auto completes to System.out.println();
//  ctrl+D -> delete line
//  highlight lines, ctrl+/, auto-comments/uncomments all of those lines

// multiply.in (and the following .in files) will include the given inputs as well as special cases for testing
 

public class Multiply {
	
	static Scanner scanner;
	public static void main(String args[]) {
		try {
			scanner = new Scanner(new File("data/multiply.in"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int a = scanner.nextInt(), b = scanner.nextInt();
		int problem = 1;
		
		while (a != 0 && b != 0) {
			// find answer and number of digits
			// use long so our program can handle huge numbers like 200001 * 90040
			long answer = (long)a*(long)b; 

			// hacky way of finding number of digits in an integer
			int digits = new Long(answer).toString().length(); 

			System.out.println("Problem " + problem);
			// print number with spaces using printf(%#d %#d ..., var1, var2, ...) # = number of spaces
			System.out.printf("%" + digits + "d\n%" + digits + "d\n", a, b); 
			
			for (int i = 0; i < digits; i++)
				System.out.print("-");
			System.out.println();
			
			/* Example Process:
			 * 34 * 965
			 *     34
			 *    965
			 *  ----- <- 5 digits in answer, 5 columns to fill
			 *    170 <- 5 * 34
			 *   204  <- 6 * 34
			 *  306   <- 9 * 34
			 *  -----
			 *  32810 <- 965 * 34
			 */
			String bAsString = new Integer(b).toString();
			int bDigits = bAsString.length();
			int bFirstDigit = new Integer(bAsString.substring(0,1));
			// if the second number is a digit followed only be zeroes, we'll only be displaying the answer (ie. for the 246 * 70 case)
			// or if it's just one digit (ie. 234567 * 8)
			if (bDigits != 1 && b != bFirstDigit * (int)Math.pow(10, bDigits-1)) {
				int bTemp = b;
				int line = 0;
				// for the 200001 * 90040, special case, we count the zeroes to append to the next displayed number
				int zeroesToAppend = 0;
				// loop through each digit on bTemp
				while (bTemp > 0) {
					int digit = bTemp % 10;
					// cut off the last digit
					bTemp /= 10; 
					// number to be displayed
					int lineNum = digit * a; 
					
					// make sure the number isn't 0, for the special case 200001 * 90040 (we count the zeroes to append in the else)
					if (lineNum != 0) { 
						int spacesLeft = digits-line;
						int spacesRight = line-1;
						
						System.out.printf("%" + spacesLeft + "d", lineNum);
						
						while (zeroesToAppend > 0) {
							zeroesToAppend--;
							System.out.print("0");
						}
						for (int i = 0; i < spacesRight; i++)
							System.out.print(" ");
						
						System.out.println();
					}
					else
						zeroesToAppend++;
					
					line++;
				}
			
				for (int i = 0; i < digits; i++)
					System.out.print("-");
			}
			
			
			System.out.println(answer);
			System.out.println();
			
			// read the next numbers
			a = scanner.nextInt();
			b = scanner.nextInt();
			problem++;
		}
	}
	
}
