package com.ibm.entry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.taskexecutor.WorkerRunnable;
import com.ibm.util.Constants;
import com.ibm.util.Utils;

public class EntryPoint {

	/**
	 * This is the entry point for the cloudant
	 * @param args
	 */
	public static void main(String[] args) {
		
		String tran_no = null;
		String requestNumber = null;
		int count = 0;
		int submissionCount = 0;
		Runnable worker = null;
		ExecutorService executor = null;
		
		// if application type is passed as run time argument then it is
		// considered else defaulted to csa
		// values csa, yp, on
		String appType = null;
		if (args.length > 0) {
			appType = args[0].toLowerCase();
		} else
			appType = Utils.getValue(Constants.DEFAULT_APP_TYPE).toLowerCase();

		
		// set the pool size and submission count based on configuration
		executor = Executors.newFixedThreadPool(Integer.valueOf(Utils.getValue(Constants.THREAD_POOL_SIZE)));
		submissionCount = Integer.valueOf(Utils.getValue(Constants.SUBMITTION_COUNT));
		
		// start execution
		for (int i = 0; i < submissionCount; i++) {

			// set the request number in cache for increment for the first time
			// based on configurations
			if (count == 0) {
				tran_no = Utils.getInitialTransaction(appType);
				requestNumber = String.valueOf(tran_no.split(Constants.UNDERSCORE)[0]);
				count = Integer.valueOf(tran_no.split(Constants.UNDERSCORE)[1]);
			} else
				tran_no = requestNumber + Constants.UNDERSCORE + count;

			worker = new WorkerRunnable(appType, tran_no);
			executor.execute(worker);
			
			count++;
		}
		
		// shutting down the pool
		executor.shutdown();

		// update the last transaction number 
		Utils.updateLastTransaction(appType, tran_no);

		System.out.println("Finished all threads");

	}

}
