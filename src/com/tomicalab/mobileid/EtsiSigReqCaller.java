/* ==========================================
 * Laverca Project
 * https://sourceforge.net/projects/laverca/
 * ==========================================
 * Copyright 2013 Laverca Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomicalab.mobileid;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etsi.uri.TS102204.v1_1_2.CertificateResponse;
import org.etsi.uri.TS102204.v1_1_2.MSS_ProfileReqType;
import org.etsi.uri.TS102204.v1_1_2.MSS_ProfileRespType;
import org.etsi.uri.TS102204.v1_1_2.MSS_ReceiptReqType;
import org.etsi.uri.TS102204.v1_1_2.MSS_ReceiptRespType;
import org.etsi.uri.TS102204.v1_1_2.MSS_RegistrationReqType;
import org.etsi.uri.TS102204.v1_1_2.MSS_RegistrationRespType;
import org.etsi.uri.TS102204.v1_1_2.MSS_SignatureReqType;
import org.etsi.uri.TS102204.v1_1_2.MSS_SignatureRespType;
import org.etsi.uri.TS102204.v1_1_2.MSS_StatusReqType;
import org.etsi.uri.TS102204.v1_1_2.MSS_StatusRespType;
import org.etsi.uri.TS102204.v1_1_2.MessagingModeType;
import org.etsi.uri.TS102204.v1_1_2.MssURIType;
import org.etsi.uri.TS102204.v1_1_2.StatusCodeType;
import org.etsi.uri.TS102204.v1_1_2.StatusType;

import com.tomicalab.mobileid.datatype.AP_Info;
import com.tomicalab.mobileid.datatype.AdditionalServices;
import com.tomicalab.mobileid.datatype.DTBS;
import com.tomicalab.mobileid.datatype.Description;
import com.tomicalab.mobileid.datatype.MSSP_ID;
import com.tomicalab.mobileid.datatype.MSSP_Info;
import com.tomicalab.mobileid.datatype.Message;
import com.tomicalab.mobileid.datatype.Service;

import fi.ficom.mss.TS102204.v1_0_0.ServiceResponsesServiceResponse;

public class EtsiSigReqCaller {

	private static final Log log = LogFactory.getLog(EtsiSigReqCaller.class);

	public static void main(String[] args) throws Exception {
//		createSignature();
		//statusQuery();
		profileQuery();
		//receiptQuery();
		//registeration();
	}

	private static void receiptQuery() throws Exception {
		// TODO Auto-generated method stub
		Properties properties = ExampleConf.getProperties();
		log.info("setting up ssl");
		JvmSsl.setSSL(properties.getProperty(ExampleConf.TRUSTSTORE_FILE),
				properties.getProperty(ExampleConf.TRUSTSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_FILE),
				properties.getProperty(ExampleConf.KEYSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_TYPE));

		String apId = properties.getProperty(ExampleConf.AP_ID);
		String apPwd = properties.getProperty(ExampleConf.AP_PASSWORD);
		
		String msspId = properties.getProperty(ExampleConf.MSSP_ID);

		String msspSignatureUrl = properties
				.getProperty(ExampleConf.SIGNATURE_URL);
		String msspStatusUrl = properties.getProperty(ExampleConf.STATUS_URL);
		String msspReceiptUrl = properties.getProperty(ExampleConf.RECEIPT_URL);
		String msspRegistrationUrl = properties.getProperty(ExampleConf.REGISTERATION_URL);
		String msspProfileUrl = properties.getProperty(ExampleConf.PROFILE_URL);
		String msspHandshakeUrl = "http://nevermind";

		EtsiClient etsiClient = new EtsiClient(apId, apPwd, msspId, msspSignatureUrl,
				msspStatusUrl, msspReceiptUrl, msspRegistrationUrl,
				msspProfileUrl, msspHandshakeUrl);

		String apTransId = "A" + System.currentTimeMillis();
		String msisdn = "84940000363";

		AP_Info apInfo = new AP_Info();
		apInfo.setAP_ID(new URI(apId));
		apInfo.setAP_PWD(apPwd);

		MSSP_ID idMssp = new MSSP_ID();
		idMssp.setURI(new URI(msspId));
		MSSP_Info msspInfo = new MSSP_Info();
		msspInfo.setMSSP_ID(idMssp);

		MSS_SignatureRespType sigResp = new MSS_SignatureRespType();

		sigResp.setAP_Info(apInfo);
		sigResp.setMSSP_Info(msspInfo);
		sigResp.setMSSP_TransID(new NCName("F4440969942209458"));

		StatusType status = new StatusType();
		status.setStatusMessage("VALID SIGNATURE");
		StatusCodeType statusCode = new StatusCodeType();
		statusCode.setValue(BigInteger.valueOf(502L));
		status.setStatusCode(statusCode);

		sigResp.setStatus(status);

		byte[] hashedstr = DatatypeConverter
				.parseHexBinary("3021300906052B0E03021A0500041492902E74284AC4060C83C5EC6805C9C4BD89FA83");
		Message message = new Message();
		message.set_value(Base64.encode(hashedstr));
		message.setEncoding(DTBS.ENCODING_BASE64);
		message.setMimeType(DTBS.MIME_STREAM);

		MSS_ReceiptReqType receiptReq = etsiClient.createReceiptRequest(
				sigResp, apTransId, msisdn, message);

		MSS_ReceiptRespType receiptResp = null;
		
		System.out.println(receiptReq.getMSSP_Info().getMSSP_ID().getURI().toString());

		try {
			receiptResp = etsiClient.send(receiptReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}

		log.info("got resp");
		long statusCodeResp = receiptResp.getStatus().getStatusCode()
				.getValue().longValue();
		log.info("statuscode " + statusCodeResp);
		System.out.println(receiptResp.getMSSP_Info().getMSSP_ID().getURI()
				.toString());
		System.out.println(receiptResp.getAP_Info().getAP_ID().toString());

	}

	private static void profileQuery() throws Exception {
		// TODO Auto-generated method stub
		Properties properties = ExampleConf.getProperties();
		log.info("setting up ssl");
		JvmSsl.setSSL(properties.getProperty(ExampleConf.TRUSTSTORE_FILE),
				properties.getProperty(ExampleConf.TRUSTSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_FILE),
				properties.getProperty(ExampleConf.KEYSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_TYPE));

		String apId = properties.getProperty(ExampleConf.AP_ID);
		String apPwd = properties.getProperty(ExampleConf.AP_PASSWORD);
		
		String msspId = properties.getProperty(ExampleConf.MSSP_ID);

		String msspSignatureUrl = properties
				.getProperty(ExampleConf.SIGNATURE_URL);
		String msspStatusUrl = properties.getProperty(ExampleConf.STATUS_URL);
		String msspReceiptUrl = properties.getProperty(ExampleConf.RECEIPT_URL);
		String msspRegistrationUrl = properties.getProperty(ExampleConf.REGISTERATION_URL);
		String msspProfileUrl = properties.getProperty(ExampleConf.PROFILE_URL);
		String msspHandshakeUrl = "http://nevermind";

		EtsiClient etsiClient = new EtsiClient(apId, apPwd, msspId, msspSignatureUrl,
				msspStatusUrl, msspReceiptUrl, msspRegistrationUrl,
				msspProfileUrl, msspHandshakeUrl);

		String apTransId = "A" + System.currentTimeMillis();
		String msisdn = "84705392218";

		MSS_ProfileReqType proReq = etsiClient.createProfileRequest(msisdn,
				apTransId);

		MSS_ProfileRespType proResp = null;
		try {
			proResp = etsiClient.send(proReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}

		log.info("got resp");
		long statusCode = proResp.getStatus().getStatusCode().getValue()
				.longValue();
		log.info("statuscode " + statusCode);
		MssURIType[] signatureProfiles;
		signatureProfiles = proResp.getSignatureProfile();
		for (int i = 0; i < signatureProfiles.length; i++) {
			System.out.println(signatureProfiles[i].getMssURI().toString());
		}
	}

	private static void createSignature() throws Exception {

		Properties properties = ExampleConf.getProperties();
		log.info("setting up ssl");
		JvmSsl.setSSL(properties.getProperty(ExampleConf.TRUSTSTORE_FILE),
				properties.getProperty(ExampleConf.TRUSTSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_FILE),
				properties.getProperty(ExampleConf.KEYSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_TYPE));

		String apId = properties.getProperty(ExampleConf.AP_ID);
		String apPwd = properties.getProperty(ExampleConf.AP_PASSWORD);
		
		String msspId = properties.getProperty(ExampleConf.MSSP_ID);

		String msspSignatureUrl = properties
				.getProperty(ExampleConf.SIGNATURE_URL);
		String msspStatusUrl = properties.getProperty(ExampleConf.STATUS_URL);
		String msspReceiptUrl = properties.getProperty(ExampleConf.RECEIPT_URL);
		String msspRegistrationUrl = properties.getProperty(ExampleConf.REGISTERATION_URL);
		String msspProfileUrl = properties.getProperty(ExampleConf.PROFILE_URL);
		String msspHandshakeUrl = "http://nevermind";

		EtsiClient etsiClient = new EtsiClient(apId, apPwd, msspId, msspSignatureUrl,
				msspStatusUrl, msspReceiptUrl, msspRegistrationUrl,
				msspProfileUrl, msspHandshakeUrl);

		String apTransId = "A" + System.currentTimeMillis();
		String msisdn = "841684237467";
		// DTBS dtbs = new DTBS("hello", DTBS.ENCODING_UTF8); //NONEwithRSA

		byte[] hashData = DatatypeConverter.parseHexBinary("3021300906052B0E03021A0500041492902E74284AC4060C83C5EC6805C9C4BD89FA83");
		
		String vc = VerificationCodeCalculator.calculate(hashData);
		
		/*
		 * 
		 * VC to make sure that hash isn't be modified during transfer to SIM
		 * 
		 * */
		log.info("VC: "+vc);
		
		DTBS dtbs = new DTBS(hashData, DTBS.ENCODING_BASE64, DTBS.MIME_STREAM);
		/*
		 * Applet will re-calculate the VC based on received HASH and replace it to %%VC%%
		 * 
		 * */
		String dataToBeDisplayed = "Ma giao dich %%VC%%. Vui long xac nhan giao dich";
		
		String signatureProfile = SignatureProfile.SIGNATURE_PROFILE_DIGITALSIGN;
		String mss_format = FiComMSS_Formats.PKCS1_PSS_SHA1;
		MessagingModeType messagingMode = MessagingModeType.synch;

		MSS_SignatureReqType sigReq = etsiClient.createSignatureRequest(
				apTransId, msisdn, dtbs, dataToBeDisplayed, signatureProfile,
				mss_format, messagingMode);

		AdditionalServices services = new AdditionalServices();

		Description d1 = new Description();
		d1.setMssURI(new URI(
				"http://mobile-id.vn/MSSP/v1.0.0#signingCertificate"));

		Service s1 = new Service();
		s1.setDescription(d1);
		services.addService(s1);

		Description d2 = new Description();
		d2.setMssURI(new URI(
				"http://mobile-id.vn/MSSP/v1.0.0#signatureValidation"));

		Service s2 = new Service();
		s2.setDescription(d2);
		services.addService(s2);

		sigReq.setAdditionalServices(services.getService());

		MSS_SignatureRespType sigResp = null;
		try {
			sigResp = etsiClient.send(sigReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			String msspErrorCode = af.getFaultSubCodes()[0].getLocalPart();
			System.out.println("ErrorCode: "+msspErrorCode);
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}

		log.info("got resp");
		long statusCode = sigResp.getStatus().getStatusCode().getValue()
				.longValue();
		log.info("statuscode " + statusCode);
		System.out.println("MSSP TransId: "+sigResp.getMSSP_TransID().toString());
		log.info("signature  "
				+ DatatypeConverter.printBase64Binary(sigResp
						.getMSS_Signature().getBase64Signature()));
		if(sigResp.getStatus()
				.getStatusDetail()!=null) {
			ServiceResponsesServiceResponse[] sResponse = sigResp.getStatus()
					.getStatusDetail().getServiceResponses();
			System.out.println("Service response length: " + sResponse.length);
			System.out.println("Any length: "
					+ sResponse[0].getDescription().getAny().length);
			System.out.println("object: "
					+ (String) sResponse[0].getDescription().getAny()[0]);
		}
		/*
		if (statusCode == 500) {
			log.info("signature  "
					+ DatatypeConverter.printBase64Binary(sigResp
							.getMSS_Signature().getBase64Signature()));
			if(sigResp.getStatus()
					.getStatusDetail()!=null) {
				ServiceResponsesServiceResponse[] sResponse = sigResp.getStatus()
						.getStatusDetail().getServiceResponses();
				System.out.println("Service response length: " + sResponse.length);
				System.out.println("Any length: "
						+ sResponse[0].getDescription().getAny().length);
				System.out.println("object: "
						+ (String) sResponse[0].getDescription().getAny()[0]);
			}
			
		} else if (statusCode == 100) {
			String msspTransId = sigResp.getMSSP_TransID().toString();
			System.out.println("MSSP TransID: " + msspTransId);
		}
		*/
/*
		System.out.println("Get receipt");
		System.out.println(sigResp.getMSSP_Info().getMSSP_ID().getURI().toString());
		System.out.println(sigResp.getMSSP_TransID());
		apTransId = "A" + System.currentTimeMillis();
		
		Message message = new Message();
		message.set_value(Base64.encode(hashedstr));
		message.setEncoding(DTBS.ENCODING_BASE64);
		message.setMimeType(DTBS.MIME_STREAM);
		MSS_ReceiptReqType receiptReq = etsiClient.createReceiptRequest(
				sigResp, apTransId, msisdn, message);
	
		MSS_ReceiptRespType receiptResp = null;

		try {
			receiptResp = etsiClient.send(receiptReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}

		log.info("got resp");
		long statusCodeResp = receiptResp.getStatus().getStatusCode()
				.getValue().longValue();
		log.info("statuscode " + statusCodeResp);
		System.out.println(receiptResp.getMSSP_Info().getMSSP_ID().getURI()
				.toString());
		System.out.println(receiptResp.getAP_Info().getAP_ID().toString());
		*/
	}

	private static void statusQuery() throws Exception {
		Properties properties = ExampleConf.getProperties();
		log.info("setting up ssl");
		JvmSsl.setSSL(properties.getProperty(ExampleConf.TRUSTSTORE_FILE),
				properties.getProperty(ExampleConf.TRUSTSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_FILE),
				properties.getProperty(ExampleConf.KEYSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_TYPE));

		String apId = properties.getProperty(ExampleConf.AP_ID);
		String apPwd = properties.getProperty(ExampleConf.AP_PASSWORD);
		
		String msspId = properties.getProperty(ExampleConf.MSSP_ID);

		String msspSignatureUrl = properties
				.getProperty(ExampleConf.SIGNATURE_URL);
		String msspStatusUrl = properties.getProperty(ExampleConf.STATUS_URL);
		String msspReceiptUrl = properties.getProperty(ExampleConf.RECEIPT_URL);
		String msspRegistrationUrl = properties.getProperty(ExampleConf.REGISTERATION_URL);
		String msspProfileUrl = "http://nevermind";
		String msspHandshakeUrl = "http://nevermind";

		EtsiClient etsiClient = new EtsiClient(apId, apPwd, msspId, msspSignatureUrl,
				msspStatusUrl, msspReceiptUrl, msspRegistrationUrl,
				msspProfileUrl, msspHandshakeUrl);

		String apTransId = "A" + System.currentTimeMillis();

		AP_Info apInfo = new AP_Info();
		apInfo.setAP_ID(new URI(apId));
		apInfo.setAP_PWD(apPwd);

		MSSP_ID idMssp = new MSSP_ID();
		idMssp.setURI(new URI(msspId));
		MSSP_Info msspInfo = new MSSP_Info();
		msspInfo.setMSSP_ID(idMssp);

		MSS_SignatureRespType sigResp = new MSS_SignatureRespType();

		sigResp.setAP_Info(apInfo);
		sigResp.setMSSP_Info(msspInfo);
		sigResp.setMSSP_TransID(new NCName("C131864910655457"));

		MSS_StatusReqType statusReq = etsiClient.createStatusRequest(sigResp,
				apTransId);

		MSS_StatusRespType statusResp = null;
		try {
			statusResp = etsiClient.send(statusReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			log.error("error code " + af.getFaultSubCodes()[0].getLocalPart());
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}
		log.info("got resp");
		long statusCode = statusResp.getStatus().getStatusCode().getValue()
				.longValue();
		log.info("statuscode " + statusCode);

		if (statusCode == 500) {
			log.info("signature  "
					+ DatatypeConverter.printBase64Binary(statusResp
							.getMSS_Signature().getBase64Signature()));
		} else if(statusCode == 502) {
			log.info("signature  "
					+ DatatypeConverter.printBase64Binary(statusResp
							.getMSS_Signature().getBase64Signature()));
		}

	}
	
	private static void registeration() throws Exception {
		// TODO Auto-generated method stub
		Properties properties = ExampleConf.getProperties();
		log.info("setting up ssl");
		JvmSsl.setSSL(properties.getProperty(ExampleConf.TRUSTSTORE_FILE),
				properties.getProperty(ExampleConf.TRUSTSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_FILE),
				properties.getProperty(ExampleConf.KEYSTORE_PASSWORD),
				properties.getProperty(ExampleConf.KEYSTORE_TYPE));

		String apId = properties.getProperty(ExampleConf.AP_ID);
		String apPwd = properties.getProperty(ExampleConf.AP_PASSWORD);
		
		String msspId = properties.getProperty(ExampleConf.MSSP_ID);

		String msspSignatureUrl = properties
				.getProperty(ExampleConf.SIGNATURE_URL);
		String msspStatusUrl = properties.getProperty(ExampleConf.STATUS_URL);
		String msspReceiptUrl = properties.getProperty(ExampleConf.RECEIPT_URL);
		String msspRegistrationUrl = properties.getProperty(ExampleConf.REGISTERATION_URL);
		String msspProfileUrl = properties.getProperty(ExampleConf.PROFILE_URL);
		String msspHandshakeUrl = "http://nevermind";

		EtsiClient etsiClient = new EtsiClient(apId, apPwd, msspId, msspSignatureUrl,
				msspStatusUrl, msspReceiptUrl, msspRegistrationUrl,
				msspProfileUrl, msspHandshakeUrl);

		String apTransId = "A" + System.currentTimeMillis();
		String msisdn = "841684237467";

		AP_Info apInfo = new AP_Info();
		apInfo.setAP_ID(new URI(apId));
		apInfo.setAP_PWD(apPwd);

		MSSP_ID idMssp = new MSSP_ID();
		idMssp.setURI(new URI(msspId));
		MSSP_Info msspInfo = new MSSP_Info();
		msspInfo.setMSSP_ID(idMssp);
		
		String signatureProfile = SignatureProfile.SIGNATURE_PROFILE_DIGITALSIGN;
		
		MSS_RegistrationReqType registerationReq = etsiClient.createRegistrationRequest(apTransId
				, msisdn, signatureProfile);

		MSS_RegistrationRespType registrationResp = new MSS_RegistrationRespType();
		
		try {
			registrationResp = etsiClient.send(registerationReq);
		} catch (AxisFault af) {
			log.error("got soap fault", af);
			return;
		} catch (IOException ioe) {
			log.error("got IOException ", ioe);
			return;
		}

		log.info("got resp");
		long statusCodeResp = registrationResp.getStatus().getStatusCode()
				.getValue().longValue();
		log.info("statuscode " + statusCodeResp);
		
		CertificateResponse[] certificates = registrationResp.getCertificateResponse();
		for(CertificateResponse certificate : certificates) {
			log.info(certificate.getCertificateURI());
			log.info(new String(certificate.getPublicKey()));
			log.info(new String(certificate.getX509Certificate()));
		}

	}
}
