package assignment1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Helper {
	/**
	 * Generate random round keys
	 * @param round: number of rounds
	 * @return an array of round keys
	 */
	public static int[] randomKeys(int round) {
		// Using random library is not secure at all!
		Random r = new Random();
		
		int[] keys = new int[round];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = r.nextInt(65535);
		}
		
		return keys;
	}

	/**
	 * Print a number in a nice binary or hex format
	 * @param n: Number to print
	 * @param base: the base of the format (2-binary, 16-hex)
	 */
	public static String intf(int n, int base) {
		String a = "";
		if (base == 16) 
			a = String.format("%4s", Integer.toHexString(n)).replace(" ","0");
		else if (base == 2) 
			a = String.format("%4s.%4s.%4s.%4s", Integer.toBinaryString((n& 0xf000)>>12), 
				Integer.toBinaryString((n& 0x0f00)>>8), 
				Integer.toBinaryString((n& 0x00f0)>>4), 
				Integer.toBinaryString(n& 0x000f)).replace(" ","0");
		return a;
	}
	
	/**
	 * Return the i-th bit of the binary n 
	 * @param n: Input number
	 * @param i: index of the bit to extract (0 is the most right)
	 * @return i-th bit of n
	 */
	public static int getBit(int n, int i) {
        return (n >> i) & 1;
    }
	
	/**
	 * Set bit of a binary
	 * @param n: Input number
	 * @param i: Index of the bit to set (0 is the most right)
	 * @param v: Value of the bit to set
	 * @return New binary with i-th bit set
	 */
	public static int setBit(int n, int i, int v) {
		if (v == 1) return n |= (1 << i);
		else		return n &= ~(1 << i);
	}
	
	/**
	 * Calculate the XOR sum of set bits in a binary (eg. 0110 -> 0 ^ 1 ^ 1 ^ 0 = 0)
	 * @param n: input number
	 * @return sum of bits in the number
	 */
	static int bitSum(int n) {
		int s = 0;
		while (n > 0) {
			s ^= (n&1);
			n = n >> 1;
		}
		return s;
	}
	
	/**
	 * Read plaintext-ciphertext from a file into an array
	 * @param path: Path to file
	 * @return AArray of plaintext-ciphertext pairs 
	 */
	public static int[] readFile(String path) {
		int[] pairs = new int[10000];
		try {
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\\s+");
				pairs[Integer.parseInt(line[0])] = Integer.parseInt(line[1]);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return pairs;
	}
	
	/**
	 * Write an array to a file
	 * @param path: Path to file
	 * @param data: Array to write
	 */
	public static void writeFile(String path, String a) {
		try {
		      FileWriter myWriter = new FileWriter(path);
		      myWriter.write(a);
		      
		      myWriter.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}

	/**
	 * Return all keys with the best value
	 * @param data: data to search from
	 * @return A string contain all best keys 
	 */
	public static String best(double[] data) {
		double best = 0;
		String ans = "";
		for (int i = 0; i < data.length; i++) {
			if (data[i] > best) {
				best = data[i];
				ans = Integer.toHexString(i);
			} else if (data[i] == best) {
				ans += "." + Integer.toHexString(i);
			} 
		}
		return ans;
	}
	
	/**
	 * Print a nice table to present the collected statistic
	 * @param data: Input data
	 */
	public static void printTable(double[] data) {
		System.out.println(" HEX  value | HEX  value  | HEX  value  | HEX  value  | HEX  value  | HEX  value  | HEX  value  | HEX  value  |"); 
		System.out.println("---------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 8; j++) {
				int index = j*32+i;
				String forms = String.format("%2s - %.04f | ",Integer.toHexString(index),data[index]);
				System.out.print(forms);
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------------------------------------------------------------");
	}
	
}
