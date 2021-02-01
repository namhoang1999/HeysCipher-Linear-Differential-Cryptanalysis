package assignment1;

public class Differential {
	HeysCipher heys = new HeysCipher(Helper.randomKeys(5));
	
	double[] exhaust(int[] input, int n) {
		double[] prob = new double[256];	
		int delta_p, delta_c;
		int p1,p2,c1,c2,subKey,y1,y2;
		
		if (n == 0) {	// 100% accuracy
			delta_p = 0x0B00;
			delta_c = 0x606;
		} else {	// 99% accuracy
			delta_p = 0x0500;	
			delta_c = 0x6060;
		}
				
		for (int i = 0; i < prob.length; i++) {
			for (p1 = 0; p1 < input.length; p1++) {
				p2 = p1 ^ delta_p;
				c1 = input[p1];
				
				// Since we might don't have results for c2
				// this is to prevent index out of bound
				if (p2 < input.length) {	
					c2 = input[p2];
		
					// Use bit mask to reconstruct corresponding key bits 
					// of K_5. The resulting key will have the form of:
					if (n == 0) subKey = (0x000F&i) ^ ((0x00F0&i)<<4);	// 0000.XXXX.0000.XXXX
					else subKey = ((0x000F&i) ^ ((0x00F0&i)<<4)) << 4;	// XXXX.0000.XXXX.0000
					
					// partial decryption of c1
					y1 = c1 ^ subKey;
					y1 = heys.sBoxEncrypt(y1, false);
					
					// partial decryption of c2
					y2 = c2 ^ subKey;
					y2 = heys.sBoxEncrypt(y2, false);
	
					// Increment when input difference to the final round
					// is the same as the estimation
					if ((y1^y2) == delta_c) prob[i]++;
				}
			}
			// Since we don't know exactly how many pairs tested,
			// dividing to get actual probability doesn't make sense
			// A count of tested pairs can be kept but this is fine :)
			//prob[i] = prob[i] / 5000; 
		}
		return prob;
	}
	
	
	public static void main(String[] args) {
		int[] data;
		// Choose between generating random ciphertext/plaintext pairs
		// or reading pairs from file
		if (args.length != 0) {
			System.out.println("Reading pairs from " + args[0]);
			data = Helper.readFile(args[0]); // file/input.txt
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
		Differential d = new Differential();
		
		double[] ac = d.exhaust(data,0);	// recovering K_5 to K_8 and K_13 to K_16
		double[] bd = d.exhaust(data,1);	// recovering K_1 to K_4 and K_9  to K_12

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
		
		System.out.println("Potential results for K_5 are: " + ans);	}
}
