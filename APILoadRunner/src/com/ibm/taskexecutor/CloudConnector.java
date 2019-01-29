package com.ibm.taskexecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.util.Constants;
import com.ibm.util.LRException;
import com.ibm.util.Utils;

public class CloudConnector {

	/**
	 * @param apptype
	 * @param tran_no
	 * @throws LRException 
	 */
	public void invokeCloudAPI(String apptype, String tran_no) throws LRException {
		String endPoint = null;
		String dataType = null;

		switch (apptype) {
		case Constants.CSA:
			endPoint = Utils.getValue(Constants.CSA_ENDPOINT);
			dataType = Constants.XML;
			break;
		case Constants.YP:
			endPoint = Utils.getValue(Constants.YP_ENDPOINT);
			dataType = Constants.XML;
			break;
		case Constants.ON:
			endPoint = Utils.getValue(Constants.ON_ENDPOINT);
			dataType = Constants.JSON;
			break;

		default:
			break;
		}

		// submission
		invoke(getAuthorizationCode(apptype), endPoint, dataType, tran_no);

	}

	private String getAuthorizationCode(String appType) {
		String authorizationdetails = null;
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(signHost());
			URL url = new URL(Utils.getValue(Constants.AUTH_ENDPOINT));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);

			JSONObject json = new JSONObject();

			if (Constants.ON.equalsIgnoreCase(appType)) {
				json.put("username", Constants.ON);
				json.put("password", "12345");
			} else if (Constants.CSA.equalsIgnoreCase(appType)) {
				json.put("username", Constants.CSA);
				json.put("password", "12345");
			} else if (Constants.YP.equalsIgnoreCase(appType)){
				json.put("username", "yourproc");
				json.put("password", "12345");
			}


			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}

			authorizationdetails = conn.getHeaderField("Authorization");

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return authorizationdetails;
	}

	private String invoke(String authCode, String serviceID, String type,String tran_no) throws LRException {
		String authorizationdetails = null;
		try {
			if (authCode == null)
				return null;

			URL url = new URL(Utils.getValue(Constants.TEST_SERVER) + serviceID);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", authCode);
			conn.setDoOutput(true);

			if (type.equalsIgnoreCase(Constants.XML)) {
				String xml = getXml(tran_no);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(xml.toString());
				wr.flush();
			} else {

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(getJson(tran_no));
				wr.flush();
			}

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output = null;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			throw new LRException(e);

		} catch (IOException e) {
			throw new LRException(e);

		} 

		return authorizationdetails;
	}

	/**
	 * Set xml request data
	 * @param tran_no
	 * @return
	 */
	private String getXml(String tran_no) {
		String requestData = null;

		requestData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<UniqueTransactionNumber>"
				+ tran_no
				+ "</UniqueTransactionNumber>"
				+ "<RequisitionName>China Request _Ariba Testing1(F239LY)</RequisitionName>"
				+ "<CompanyCode>0684</CompanyCode>"
				+ "<RequesterName>OneCN Requester</RequesterName>"
				+ "<CommentToSupplier>Comments to suppliers * Contracts: None</CommentToSupplier>"
				+ "<PreparerWebId>chinareq1@c25a0161.toronto.ca.ibm.com</PreparerWebId>"
				+ "<RequesterWebId>chinareq1@c25a0161.toronto.ca.ibm.com</RequesterWebId>"
				+ "<LineItems>"
				+ "<ItemDetails>"
				+ "<unspsc_cd>81111600</unspsc_cd>"
				+ "<lineitemDescription>Milestone name1~Description of milestone1</lineitemDescription>"
				+ "<lineItemAmount>20.000</lineItemAmount>"
				+ "<lineItemCurrCd>CNY</lineItemCurrCd>"
				+ "<supplierpartNumber>F239LY000001</supplierpartNumber>"
				+ "<lineItemUom>DAY</lineItemUom>"
				+ "<splitPercentage>100</splitPercentage>"
				+ "<lineItemQty>3</lineItemQty> <!-- accepts 3.000  -->"
				+ "<lineItemSeqNo>1</lineItemSeqNo>"
				+ "<!-- <lineItemComment></lineItemComment> --> <!-- optional -->"
				+ "<lineitemNeedByDate>2021-01-02T00:00:00</lineitemNeedByDate>"
				+ "<originatingSystemLineItemNumber>001</originatingSystemLineItemNumber>"
				+ "<lineitemSupplierId>1000301665</lineitemSupplierId> <!--  Please note your supplier Id exist in ierp otherwise transaction will fail -->"
				+ "<lineitemContractNo></lineitemContractNo>"
				+ "<lineitemByPassFlag>B</lineitemByPassFlag>"
				+ "<lineitemSourceCode>E</lineitemSourceCode>"
				+ "<valueOrder>false</valueOrder> <!-- optional : accepts true and false based on UOM value -->"
				+ "<startDate>2021-01-02T00:00:00</startDate>"
				+ "<endDate>2022-01-02T00:00:00</endDate>"
				+ "</ItemDetails>"
				+ "<ItemDetails>"
				+ "<unspsc_cd>90000000TS</unspsc_cd>"
				+ "<lineitemDescription>Milestone name1~Description of milestone1</lineitemDescription>"
				+ "<lineItemAmount>80.000</lineItemAmount>"
				+ "<lineItemCurrCd>CNY</lineItemCurrCd>"
				+ "<supplierpartNumber>F239LY000002</supplierpartNumber>"
				+ "<lineItemUom>DAY</lineItemUom>"
				+ "<splitPercentage>100</splitPercentage>"
				+ "<lineItemQty>1</lineItemQty> <!-- accepts 3.000  -->"
				+ "<lineItemSeqNo>2</lineItemSeqNo>"
				+ "<!-- <lineItemComment></lineItemComment> --> <!-- optional -->"
				+ "<lineitemNeedByDate>2021-01-02T00:00:00</lineitemNeedByDate>"
				+ "<originatingSystemLineItemNumber>002</originatingSystemLineItemNumber>"
				+ "<lineitemSupplierId>1000301665</lineitemSupplierId> <!--  Please note your supplier Id exist in ierp otherwise transaction will fail -->"
				+ "<lineitemContractNo></lineitemContractNo>"
				+ "<lineitemByPassFlag>B</lineitemByPassFlag>"
				+ "<lineitemSourceCode>E</lineitemSourceCode>"
				+ "<valueOrder>true</valueOrder> <!-- optional : accepts true and false based on UOM value -->"
				+ "<startDate>2021-01-02T00:00:00</startDate>"
				+ "<endDate>2022-01-02T00:00:00</endDate>"
				+ "	</ItemDetails>"
				+ "</LineItems>" + "</Request>";

		return requestData;
	}

	/**
	 * set JSon request data
	 * @param tran_no
	 * @return
	 */
	private String getJson(String tran_no) {
		String requestData = null;
		// JSONObject requestData = new JSONObject();
		requestData = "{" + "\"UniqueTransactionNumber\": \""
				+ tran_no
				+ "\","
				+ "\"RequisitionName\": \"China Request _Ariba Testing1(F239LY)\","
				+ "\"CompanyCode\": \"0684\","
				+ "\"RequesterName\": \"OneCN Requester\","
				+ "\"CommentToSupplier\": \"Comments to suppliers * Contracts: None\","
				+ "\"PreparerWebId\": \"chinareq1@c25a0161.toronto.ca.ibm.com\","
				+ "\"RequesterWebId\": \"chinareq1@c25a0161.toronto.ca.ibm.com\","
				+ "\"LineItems\": ["
				+ "{"
				+ "\"unspsc_cd\": \"81111600\","
				+ "\"lineitemDescription\": \"Milestone name1~Description of milestone1\","
				+ "\"lineItemAmount\": \"20.000\","
				+ "\"lineItemCurrCd\": \"CNY\","
				+ "\"supplierpartNumber\": \"F239LY000001\","
				+ "\"lineItemUom\": \"DAY\","
				+ "\"splitPercentage\": \"100\","
				+ "\"lineItemQty\": \"3\","
				+ "\"lineItemSeqNo\": \"1\","
				+ "\"lineitemNeedByDate\": \"2021-01-02T00:00:00\","
				+ "\"originatingSystemLineItemNumber\": \"001\","
				+ "\"lineitemSupplierId\": \"1000301665\","
				+ "\"lineitemContractNo\": \"null\","
				+ "\"lineitemByPassFlag\": \"B\","
				+ "\"lineitemSourceCode\": \"E\","
				+ "\"valueOrder\": \"false\","
				+ "\"startDate\": \"2021-01-02T00:00:00\","
				+ "\"endDate\": \"2022-01-02T00:00:00\""
				+ "},"
				+ "{"
				+ "\"unspsc_cd\": \"81111601\","
				+ "\"lineitemDescription\": \"Milestone name1~Description of milestone2\","
				+ "\"lineItemAmount\": \"80.000\","
				+ "\"lineItemCurrCd\": \"CNY\","
				+ "\"supplierpartNumber\": \"F239LY000002\","
				+ "\"lineItemUom\": \"DAY\"," + "\"splitPercentage\": \"101\","
				+ "\"lineItemQty\": \"3\"," + "\"lineItemSeqNo\": \"1\","
				+ "\"lineitemNeedByDate\": \"2021-01-02T00:00:00\","
				+ "\"originatingSystemLineItemNumber\": \"001\","
				+ "\"lineitemSupplierId\": \"1000301665\","
				+ "\"lineitemContractNo\": \"null\","
				+ "\"lineitemByPassFlag\": \"B\","
				+ "\"lineitemSourceCode\": \"E\","
				+ "\"valueOrder\": \"false\","
				+ "\"startDate\": \"2021-01-02T00:00:00\","
				+ "\"endDate\": \"2022-01-02T00:00:00\"" + "}" + "]" + "}";
		return requestData;
	}

	/**
	 * Accepts all SSL by default
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private HostnameVerifier signHost() throws NoSuchAlgorithmException,
			KeyManagementException {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		return allHostsValid;
	}

}
