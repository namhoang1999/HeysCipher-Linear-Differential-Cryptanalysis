package assignment1;

public class HeysCipher {
	boolean verbose = false;
	// 4x4 S-Box for encryption and Inverse S-Box for decryption
	// 1 compact 2D array can be used instead but it would be inefficient	
	static int[] sBox = {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7};
	static int[] sBoxInverse = {14,3,4,8,1,12,10,15,7,13,9,6,11,2,0,5}; 
	
	// 4x4 P-Box
	static int[] pBox = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
	
	public int[] key;
	public int maxRound;
	
	/**
	 * Instantiate a Hey's Cipher object
	 * @param k: cipher keys (16-bit key)
	 */
	public HeysCipher(int[] k) {
		key = k;
		maxRound = k.length;
	}

	/**
	 * Encrytion/Decryption using S-Box
	 * @param inputBlock: 16 bit input blocks 
	 * @param encrypt: True - Encryption, False, Decryption
	 * @return 16-bit substituted output 
	 */
	int sBoxEncrypt(int input, boolean encrypt) {
		int block1,block2,block3,block4;
		
		// Use a bit mask and shift operator 
		// to split input data into 4 sub-blocks
		//
		//       input       bit
		//      to S-Box     mask 
		//          v         v
		block1 =  input & 0x000f;			// 0000.0000.0000.XXXX
		block2 = (input & 0x00f0) >> 4;		// 0000.0000.XXXX.0000
		block3 = (input & 0x0f00) >> 8;		// 0000.XXXX.0000.0000
		block4 = (input & 0xf000) >> 12;	// XXXX.0000.0000.0000
		
		// Substitute blocks using S-Box
		if (encrypt) {
			// In case of encryption, use encryption table
			block1 = sBox[block1];
			block2 = sBox[block2];
			block3 = sBox[block3];
			block4 = sBox[block4];
		} else {
			// In case of decryption, use decryption (inverse) table
			block1 = sBoxInverse[block1];
			block2 = sBoxInverse[block2];
			block3 = sBoxInverse[block3];
			block4 = sBoxInverse[block4];
		}
		
		// Return the concatenated 16-bit block
		return block1 | block2<<4 | block3<<8 | block4<<12;
	}

	/**
	 * Encryption/Decryption using P-Box
	 * @param inputBlock: 16-bit input block (4 4-bit blocks)
	 * @return substituted 16-bit output 
	 */
	int pBoxEncrypt(int input) {
		int output = 0;
		for (int i = 0; i < 16; i++) {
			// set the output bit to 1 if the 
			// correspoding input bit (from the P-box) is 1
			if (Helper.getBit(input, i) == 1) 
				output = Helper.setBit(output, pBox[i], 1);
		}
		
		return output;
	}

	/**
	 * Encryption function
	 * @param inputBlock: 16-bit plaintext input block
	 * @return 16-bit ciphertext output
	 */
	int encrypt(int inputBlock) {
		if (verbose) System.out.println("               " + Helper.intf(inputBlock,16));
		for (int round = 0; round < maxRound-1; round++) {
			if (verbose) System.out.println("Round " + round + " | key: " + Helper.intf(key[round],16));
			
			// Mixing the input block with the round key
			inputBlock = inputBlock ^ key[round];
			if (verbose) System.out.println("Key mixing:    " + Helper.intf(inputBlock,16));
			
			// S-Box Substitution
			inputBlock = sBoxEncrypt(inputBlock,true);
			if (verbose) System.out.println("S-box:         " + Helper.intf(inputBlock,16));
			
			// P-Box Permutation
			if (round != maxRound-2) {
				inputBlock = pBoxEncrypt(inputBlock);
				if (verbose) System.out.println("P-box:         " + Helper.intf(inputBlock,16) + "\n");
			}
		}
		// Apply subkey after the last round
		// to prevent attacker working backward
		// through the last round's substitution
		if (verbose) System.out.println("Final key      " + Helper.intf(key[key.length-1],16));
		inputBlock = inputBlock ^ key[key.length-1];

		if (verbose) System.out.println("               " + Helper.intf(inputBlock,16));
		
		return inputBlock;
	}

	/**
	 * Decryption function
	 * @param inputBlock: 16-bit ciphertext input block
	 * @return 16-bit plaintext output
	 */
	int decrypt(int inputBlock) {
		if (verbose) System.out.println("               " + Helper.intf(inputBlock,16));
	
		inputBlock = inputBlock ^ key[key.length-1];
		if (verbose) System.out.println("Final key      " + Helper.intf(key[key.length-1],16));
		
		for (int round = maxRound-1; round >= 0; round--) {
			if (verbose) System.out.println("Round " + round + " | key: " + Helper.intf(key[round],16));
			if (round != maxRound-1) {
				inputBlock = pBoxEncrypt(inputBlock);
				if (verbose) System.out.println("P-box:         " + Helper.intf(inputBlock,16));
			}
			
			inputBlock = sBoxEncrypt(inputBlock,false);
			if (verbose) System.out.println("S-box:         " + Helper.intf(inputBlock,16));
			
			inputBlock = inputBlock ^ key[round];
			if (verbose) System.out.println("Key mixing:    " + Helper.intf(inputBlock,16) + "\n");
		}
		
		return inputBlock;
	}
}
