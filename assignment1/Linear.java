package assignment1;

public class Linear {
	HeysCipher heys= new HeysCipher(Helper.randomKeys(5));
	
	double[] exhaust(int[] data, int o) {
		// Array to store the bias of all 256 keys exhausted
		double[] bias = new double[256]; // change to 4096 for more exhaustive search	
		int subKey,c,p;
		
		for (int i = 0; i < bias.length; i++) {
			// Use bit mask to reconstruct corresponding key bits 
			// of K_5. The resulting key will have the form of:
			if (o == 0) 
				subKey = (0x000F&i) ^ ((0x00F0&i)<<4);        // 0000.XXXX.0000.XXXX
			else if (o == 1) 
				subKey = ((0x000F&i) ^ ((0x00F0&i)<<4)) << 4; // XXXX.0000.XXXX.0000
			else 
				subKey = i << 4;            				  // XXXX.XXXX.XXXX.0000
			
//			subKey = i << 4;                              // 0000.XXXX.XXXX.0000
//			subKey = ((0x00F0&i) << 8) ^ (0x000F&i);      // XXXX.0000.0000.XXXX
			
			
			for (p = 0; p < data.length; p++) {
				c = data[p];	// obtain ciphertext from the given data
				
				// XOR backward with Key k_5
				c = c^subKey;
				// Substitute backward to the S-box
				c = heys.sBoxEncrypt(c, false);
				
				// Increment count for each key
				bias[i] += calculate(p,c,o);		
			}
			bias[i] = Math.abs(Math.abs(bias[i] - 5000)/10000);
		}

		return bias;
	}
	
	int calculate(int p, int u, int o) {
		if (o == 0) 	 // recover 2 and 4 (Hey's approximation) (> 75% accuracy)
			return Helper.bitSum(p & 0xb00) ^ Helper.bitSum(u & 0x505);
		else if (o == 1) // recover 1 and 3	70% accuracy) 
			return Helper.bitSum(p & 0x9009) ^ Helper.bitSum(u & 0x8080);
		else 			 //  recover 1,2 and 3 
			return Helper.bitSum(p & 0xcc00) ^ Helper.bitSum(u & 0x2220);
	}
		
	public static void main(String[] args) {
		int[] data;
		// Choose between generating random ciphertext/plaintext pairs
		// or reading pairs from file
		if (args.length != 0) {
			System.out.println("Reading pairs from " + args[0]);
			data = Helper.readFile(args[0]); // file/FourRounds.txt
		} else {
			// Generating random plaintext/ciphertext pairs
			int[] keys = Helper.randomKeys(5);
			System.out.println("Generating random round keys, key K_5: " + Helper.intf(keys[4], 16));
			
			System.out.println("Generating random plaintext/ciphertext pairs");
			HeysCipher hc = new HeysCipher(keys);
			data = new int[10000];
			for (int j = 0; j < 10000; j++) {
				data[j] = hc.encrypt(j);
			}
		}
		
		// Cryptanalysis using collected pairs
		Linear c = new Linear();
		
		double[] ac = c.exhaust(data,0);	// recovering K_5 to K_8 and K_13 to K_16
		double[] bd = c.exhaust(data,1);	// recovering K_1 to K_4 and K_9  to K_12

		// There might be more than 1 results with the 
		// same highest value. Let's print out every 
		// possible combination possible
		String ans = "";
		for (String i: Helper.best(ac).split("\\.")) {
			for (String j: Helper.best(bd).split("\\.")) {
				i = String.format("%2s", i).replace(" ", "0");	// padding
				j = String.format("%2s", j).replace(" ", "0");	// padding
				ans  += j.substring(0,1) + i.substring(0,1) + 
						j.substring(1,2) + i.substring(1,2) + ", ";
			}
		}
		
		System.out.println("Potential results for K_5: " + ans);
	}
}
 
