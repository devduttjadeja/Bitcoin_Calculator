package com.blockchain.CryptocurrenciesCalculator;

import java.util.Date;
import java.util.List;

import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.entity.Address;
import info.blockchain.api.blockexplorer.entity.Block;
import info.blockchain.api.blockexplorer.entity.Transaction;

public class App {
	
	public static void main(String[] args) throws Exception {
		// Miner's Public Address

		String strPublicAddress = "1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cE";

		String strBlockHash = "000000000000000000065a2bfed06cb0df1f584e48be7bbce931873ca3b2b3be";

		// Specify time frame

		int startYear = 2020;
		int startMonth = 1;
		int startDay = 15;

		int endYear = 2020;
		int endMonth = 1;
		int endDay = 19;

		BlockExplorer bc = new BlockExplorer();

		Address address = bc.getAddress(strPublicAddress);

		// Get List of last 50 transactions by miner
		List<Transaction> listTransactions = address.getTransactions();

		System.out.println("Total Num Transacs: " + listTransactions.size() + "\n");

		// This is our A value
		// It represents total assets miner has earned due to mining blocks from
		// startDate to endDate.
		double sumFees = 0;

		// Year is Offset by 1900 (Gregorian Calendar)
		// Month Index starts with 0

		int gregorianYearOffset = 1900;

		int gregorianMonthOffset = 1;

		Date startDate = new Date(startYear - gregorianYearOffset, startMonth - gregorianMonthOffset, startDay);

		// Value of 1 added to endDay because we want endDay to be inclusive
		// If we want endDay to be exclusive later, we remove the additional 1
		Date endDate = new Date(endYear - gregorianYearOffset, endMonth - gregorianMonthOffset, endDay + 1);

		// Starting Finding Mined Blocks and Retrieving their details by Looping through
		// transactions.
		// Goal is to find Total Assets earned by Miner during the specified period
		// **Please note that API allows only latest 50 transactions

		int numBlocksMined = 0;

		for (int i = 0; i < listTransactions.size(); i++) {

			Transaction t = listTransactions.get(i);

			long time = t.getTime();

			Date date = convertTime(time); // Convert time to actual date & time

			System.out.println((i + 1) + ". " + t.getHash() + " mined at: " + date);

			if (date.after(startDate) && date.before(endDate)) {

				// Loop through Output List to calculate total transaction value
				// However Not all Transactions are mined blocks
				// Mined Blocks have no inputs
				// Mined Blocks also have no Spent sub-transactions
				// Hence we check for expenditure while looping through output list
				// If none is detected, it is a mined blocked
				// Total Fee received for mining a block = List of output fees of that
				// transactions

				double totalTransactionFee = 0;

				// boolean spendDetected=false;

				boolean returnFlagDetected = false;

				for (int j = 0; j < t.getOutputs().size(); j++) {

					if (t.getOutputs().size() == 3 && t.getOutputs().get(1).getValue() == 0
							&& t.getOutputs().get(2).getValue() == 0) {

						returnFlagDetected = true;

						// Value is in Satoshi...So We need to multiply by 10^-8 to convert into BTC

						totalTransactionFee += (double) (t.getOutputs().get(j).getValue()) * Math.pow(10, -8); // Need
																												// to
																												// convert
																												// from
																												// Satoshi
																												// to
																												// BTC
																												// **

					} // End of If Condition

				} // End of Inner For Loop: transaction-fee-calculation for 1 transaction only

				// if( (!spendDetected)) {

				if (returnFlagDetected) {

					sumFees += totalTransactionFee;
					numBlocksMined++;
					System.out.println("Transaction represents mined block.");
					System.out.println("Fee for mined block: " + totalTransactionFee + "\n");

				}

				else {
					System.out.println("Transaction does not represent a mined block\n");
				}

			} // End of Date-checking If Function

			else {

				System.out.println("Date condition not met.\n");

			}

		} // End of Outer for Loop (Through All Transactions)

//--------------------------------------------------------------------------------------------------------------		

		// Start Finding Hash Rate:

		// Expected Asset
		double A = sumFees;
		// double A=numBlocksMined;

		// Mining Difficulty
		double D = getDifficulty(strBlockHash);

		// Reward
		double R = 12.5 * 24 * 6 * 3;

		// Global Hash Rate
		double G = D * (Math.pow(2, 32)) / 600;

		// Expected HashRate
		double H = (A * G) / R;

		System.out.println("Total Fee for Miner, A: " + sumFees + "BTC\n");
		System.out.println("Difficulty, D: " + D + "\n");
		System.out.println("Global Hash Rate, G: " + G + " hashes/second\n");
		System.out.println("Expected Hash Rate, H: " + H + " hashes/second\n");
		System.out.println("Num Blocks Mined within Timeframe: " + numBlocksMined + "\n");

		System.out.println("H/G in terms of HashRate: " + H / G + "\n");

		System.out.println("H/G in terms of blocks mined: " + (float) ((float) numBlocksMined / 432) + "\n");

		// -------------------Given H, Find Expected Assets of a Miner with respect to
		// Global Computer Power-------------

		System.out.println("Given HashRate of Miner, We find how much Bitcoins He/She can expect to earn:");

		double[] arrA = new double[10];
		double[] arrX = new double[10];
		
		for (int i = 10; i < 20; i++) {

			double variableHashRate = Math.pow(10, i);
			arrX[i-10] = i;
			arrA[i - 10] = (variableHashRate / G) * R;
			
		}

		GraphDisplay.displayGraph(arrX, arrA);
		
		System.out.println("Given that Bitcoin's throughput (R) increases over a timeframe, We"
				+ " investigate how the Mining difficulty adjusts itself.");

		double[] arrD = new double[20];

		for (int i = 1; i < 20; i++) {

			// double variableR=()
			arrD[i - 1] = (G * 600) / (i * 10 * 24 * 6 * 3 * Math.pow(2, 32));
			// System.out.println(arrD[i - 1]);
		}

	} // End of main

	// -------------------------------------------------------------------------------------------------------------

	// Calculate Mining Difficulty
	// Convert bits retrieved to decimal value
	private static Double getDifficulty(String hash) throws Exception {

		// Get Block using Hash

		BlockExplorer blockExplorer = new BlockExplorer();
		Block block = blockExplorer.getBlock(hash);

		// Hexa of Constant Target:
		// 00000000FFFF0000000000000000000000000000000000000000000000000000

		Double constantTarget = new Double("26959535291011309493156476344723991336010898738574164086137773096960");

		// Retrieve bits and convert to Hexa
		long bitsLong = block.getBits();
		String hexString = Long.toHexString(bitsLong);

		// Split hexa bits into 2 parts:
		// 1. First 2 digits
		// 2. Remaining digits
		String firstTwoDigits = hexString.substring(0, 2);
		String lastDigits = hexString.substring(2);

		// Apply Formula: D = constantTarget/ (remainingDigits * 2^ (8*
		// (firstTwoDigit-3) ))
		Long longFirstTwoDigits = 8 * (Long.parseLong(firstTwoDigits, 16) - 3);
		Double power = Math.pow(2, longFirstTwoDigits);
		Double pow = new Double(String.valueOf(power));

		Long longLastDigits = Long.parseLong(lastDigits, 16);
		Double lastdigitsString = new Double(String.valueOf(longLastDigits));

		Double multi = lastdigitsString * pow;

		Double diffculty = constantTarget / multi;

		return diffculty;
	}

	// --------------------------------------------------------------------------------------------------------------------------

	// Convert time from long to proper Date Format
	public static Date convertTime(long time) {
		return new Date(time * 1000);
	}
}
