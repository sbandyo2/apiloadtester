package com.ibm.taskexecutor;

import com.ibm.util.LRException;

/**
 * @author ibm
 *
 */
public class WorkerRunnable implements Runnable {

	private String tran_no = null;
	private  String appType = null;
	private CloudConnector cloudConnector = null;

	public WorkerRunnable(String applicationType, String reqNo) {
		tran_no = reqNo;
		appType = applicationType;
		cloudConnector = new CloudConnector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			cloudConnector.invokeCloudAPI(appType, tran_no);
		} catch (LRException e) {
			e.printStackTrace();
		}
	}

}
