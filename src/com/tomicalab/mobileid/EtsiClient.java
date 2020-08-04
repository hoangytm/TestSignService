//
// Copyright Laverca project. 
// The contents of this file are subject to the Laverca License.
//

package com.tomicalab.mobileid;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Stub;
import org.apache.axis.types.NCName;
import org.apache.axis.types.PositiveInteger;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
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
import org.etsi.uri.TS102204.v1_1_2.MeshMemberType;
import org.etsi.uri.TS102204.v1_1_2.MessageAbstractType;
import org.etsi.uri.TS102204.v1_1_2.MessagingModeType;
import org.etsi.uri.TS102204.v1_1_2.StatusType;

import vn.mobile_id.ae.TS102204.etsi204.MSS_ProfileQueryBindingStub;
import vn.mobile_id.ae.TS102204.etsi204.MSS_ReceiptBindingStub;
import vn.mobile_id.ae.TS102204.etsi204.MSS_RegistrationBindingStub;
import vn.mobile_id.ae.TS102204.etsi204.MSS_SignatureBindingStub;
import vn.mobile_id.ae.TS102204.etsi204.MSS_SignatureServiceLocator;
import vn.mobile_id.ae.TS102204.etsi204.MSS_StatusQueryBindingStub;

import com.tomicalab.mobileid.datatype.AP_Info;
import com.tomicalab.mobileid.datatype.AdditionalServices;
import com.tomicalab.mobileid.datatype.DTBS;
import com.tomicalab.mobileid.datatype.DataToBeDisplayed;
import com.tomicalab.mobileid.datatype.DataToBeSigned;
import com.tomicalab.mobileid.datatype.MSSP_ID;
import com.tomicalab.mobileid.datatype.MSSP_Info;
import com.tomicalab.mobileid.datatype.MSS_Format;
import com.tomicalab.mobileid.datatype.Message;
import com.tomicalab.mobileid.datatype.MobileUser;
import com.tomicalab.mobileid.datatype.SignatureProfile;

/**
 * A raw ETSI TS 102 204 client object.
 */
public class EtsiClient {
	private static Log log = LogFactory.getLog(EtsiClient.class);

	// protected HttpConnectionManager cm = null;

	// AP settings
	String apId = null;
	String apPwd = null;
	
	// MSSP setting
	String msspId = null;

	// MSSP AE connection settings
	final MSS_SignatureServiceLocator mssSignatureService = new MSS_SignatureServiceLocator();

	URL MSSP_SI_URL = null;
	URL MSSP_RC_URL = null;
	URL MSSP_HS_URL = null;
	URL MSSP_ST_URL = null;
	URL MSSP_PR_URL = null;
	URL MSSP_RG_URL = null;

	/**
	 * NOTE that if any of the URLs require SSL, you must call JvmSsl.setSSL()
	 * before sending any requests. TODO: DO SOMETHING ABOUT IT
	 *
	 * @param apIdentifier
	 *            Your identifier; MessageAbstractType/AP_Info/AP_ID. Not null.
	 * @param apPassword
	 *            Your password; MessageAbstractType/AP_Info/AP_PWD. Not null.
	 * @exception IllegalArgumentException
	 *                if a parameter value is missing or invalid.
	 */
	public EtsiClient(
			String apId, // AP settings
			String apPwd,
			String msspId, // MSSP setting
			String msspSignatureUrl, // AE connection settings
			String msspStatusUrl, String msspReceiptUrl,
			String msspRegistrationUrl, String msspProfileUrl,
			String msspHandshakeUrl) throws IllegalArgumentException {
		if (apId != null)
			this.apId = apId;
		else
			throw new IllegalArgumentException("null apId not allowed.");
		if (apPwd != null)
			this.apPwd = apPwd;
		else
			throw new IllegalArgumentException("null apPwd not allowed.");
		
		if(msspId != null)
			this.msspId = msspId;

		this.setAeAddress(msspSignatureUrl, msspStatusUrl, msspReceiptUrl,
				msspRegistrationUrl, msspProfileUrl, msspHandshakeUrl);

	}

	/**
	 * @param msspSignatureUrl
	 *            Connection URL to the AE for signature requests.
	 * @param msspStatusUrl
	 *            Connection URL to the AE for status query requests.
	 * @param msspReceiptUrl
	 *            Connection URL to the AE for receipt requests.
	 * @param msspRegistrationUrl
	 *            Connection URL to the AE for registration requests.
	 * @param msspProfileUrl
	 *            Connection URL to the AE for profile query requests.
	 * @param msspHandshakeUrl
	 *            Connection URL to the AE for handshake requests.
	 */
	public void setAeAddress(String msspSignatureUrl, String msspStatusUrl,
			String msspReceiptUrl, String msspRegistrationUrl,
			String msspProfileUrl, String msspHandshakeUrl)
			throws IllegalArgumentException {
		try {
			if (msspSignatureUrl != null) {
				this.MSSP_SI_URL = new URL(msspSignatureUrl);
			}
			if (msspStatusUrl != null) {
				this.MSSP_ST_URL = new URL(msspStatusUrl);
			}
			if (msspReceiptUrl != null) {
				this.MSSP_RC_URL = new URL(msspReceiptUrl);
			}
			if (msspRegistrationUrl != null) {
				this.MSSP_RG_URL = new URL(msspRegistrationUrl);
			}
			if (msspProfileUrl != null) {
				this.MSSP_PR_URL = new URL(msspProfileUrl);
			}
			if (msspHandshakeUrl != null) {
				this.MSSP_HS_URL = new URL(msspHandshakeUrl);
			}
		} catch (MalformedURLException mue) {
			throw new IllegalArgumentException(mue.getMessage());
		}
	}

	void fillMatStuff(MessageAbstractType mat, String apTransId)
			throws MalformedURIException {
		if (mat == null)
			throw new IllegalArgumentException("can't fill a null mat");

		// Set the interface versions. 1 for both, as per ETSI TS 102 204.
		mat.setMajorVersion(BigInteger.valueOf(1L));
		mat.setMinorVersion(BigInteger.valueOf(1L));

		// Create the AP_Info.
		AP_Info aiObject = new AP_Info();
		aiObject.setAP_ID(new URI(this.apId));
		aiObject.setAP_PWD(this.apPwd);
		if (apTransId == null)
			throw new IllegalArgumentException("null apTransId not allowed.");
		aiObject.setAP_TransID(new NCName(apTransId));
		aiObject.setInstant(Calendar.getInstance());
		mat.setAP_Info(aiObject);

		MSSP_Info miObject = new MSSP_Info();
		MSSP_ID _msspId = new MSSP_ID();
		_msspId.setURI(new URI(this.msspId));
		miObject.setMSSP_ID(_msspId);
		mat.setMSSP_Info(miObject);
	}

	/**
	 * Creates a signature request.
	 * 
	 * @param msisdn
	 *            not null.
	 * @param messagingMode
	 *            not null.
	 * @param dataToBeSigned
	 *            not null.
	 * @param dataToBeDisplayed
	 * @param apTransId
	 *            not null.
	 * @param signatureProfile
	 *            not null.
	 * @throws MalformedURIException
	 */
	public MSS_SignatureReqType createSignatureRequest(String apTransId,
			String msisdn, DTBS dtbs, String dataToBeDisplayed,
			String signatureProfile, String mss_format,
			MessagingModeType messagingMode) throws MalformedURIException {
		MSS_SignatureReqType req = new MSS_SignatureReqType();

		fillMatStuff(req, apTransId);

		if (msisdn == null)
			throw new IllegalArgumentException("null msisdn is not allowed.");
		MobileUser muObject = new MobileUser();
		muObject.setMSISDN(msisdn);
		req.setMobileUser(muObject);

		if (dtbs == null)
			throw new IllegalArgumentException(
					"null dataToBeSigned is not allowed.");
		DataToBeSigned dsObject = dtbs.toDataToBeSigned();
		req.setDataToBeSigned(dsObject);

		if (dataToBeDisplayed != null) {
			DataToBeDisplayed ddObject = new DataToBeDisplayed();
			ddObject.set_value(dataToBeDisplayed);
			req.setDataToBeDisplayed(ddObject);
		}

		if (signatureProfile == null)
			throw new IllegalArgumentException(
					"null signatureProfile is not allowed.");
		SignatureProfile spObject = new SignatureProfile();
		spObject.setMssURI(new URI(signatureProfile));
		req.setSignatureProfile(spObject);

		if (mss_format != null) {
			MSS_Format mfObject = new MSS_Format();
			mfObject.setMssURI(new URI(mss_format));
			req.setMSS_Format(mfObject);
		}

		if (messagingMode == null)
			throw new IllegalArgumentException(
					"null messagingMode is not allowed.");
		req.setMessagingMode(messagingMode);

		AdditionalServices additionalServices = new AdditionalServices();
		req.setAdditionalServices(additionalServices.getService());
		req.setTimeOut(new PositiveInteger("300"));
		return req;
	}

	/**
	 * Create a MSS_ReceiptRequest based on received MSS_SignatureResponse
	 * 
	 * @param sigResp
	 *            MSS_SignatureResponse on which the receipt request is
	 *            constructed
	 * @param apTransId
	 *            each new MSS request needs a new apTransID
	 * @param message
	 * @throws MalformedURIException 
	 */

	public MSS_ReceiptReqType createReceiptRequest(MSS_SignatureRespType sigResp,
			String apTransId, String msisdn, Message message) throws MalformedURIException {
		MSS_ReceiptReqType req = new MSS_ReceiptReqType();

		fillMatStuff(req, apTransId);

		if (sigResp == null) {
			throw new IllegalArgumentException("null sigResp not allowed.");
		}

		if (sigResp.getMSSP_Info() == null) {
			throw new IllegalArgumentException(
					"null sigResp.MSSP_Info not allowed.");
		}
		MeshMemberType msspId = sigResp.getMSSP_Info().getMSSP_ID();
		if (msspId == null) {
			throw new IllegalArgumentException(
					"null sigResp.MSSP_Info.MSSP_ID not allowed.");
		}

		if (msisdn == null)
			throw new IllegalArgumentException("null msisdn is not allowed.");
		MobileUser muObject = new MobileUser();
		muObject.setMSISDN(msisdn);
		req.setMobileUser(muObject);
		req.getMSSP_Info().setMSSP_ID(msspId); // fillMatStuff creates an empty
												// MSSP_Info
		req.setMSSP_TransID(sigResp.getMSSP_TransID());
		
		StatusType status = new StatusType();
		status.setStatusCode(sigResp.getStatus().getStatusCode());
		status.setStatusMessage(sigResp.getStatus().getStatusMessage());
		req.setStatus(status);

		if (message != null) {
			req.setMessage(message);
		}

		return req;
	}

	/**
	 * Create a status request for a signature response.
	 * 
	 * @param apTransID
	 *            new AP transaction id
	 * @param sigResp
	 * @throws MalformedURIException
	 */

	public MSS_StatusReqType createStatusRequest(MSS_SignatureRespType sigResp,
			String apTransId) throws MalformedURIException {
		MSS_StatusReqType req = new MSS_StatusReqType();

		fillMatStuff(req, apTransId);

		if (sigResp == null) {
			throw new IllegalArgumentException("null sigResp not allowed.");
		}

		if (sigResp.getMSSP_Info() == null) {
			throw new IllegalArgumentException(
					"null sigResp.MSSP_Info not allowed.");
		}
		MeshMemberType msspId = sigResp.getMSSP_Info().getMSSP_ID();
		if (msspId == null) {
			throw new IllegalArgumentException(
					"null sigResp.MSSP_Info.MSSP_ID not allowed.");
		}
		req.getMSSP_Info().setMSSP_ID(msspId); // fillMatStuff creates an empty
												// MSSP_Info

		String msspTransId = sigResp.getMSSP_TransID().toString();
		req.setMSSP_TransID(new NCName(msspTransId));

		return req;
	}

	/**
	 * Create a profile request.
	 * 
	 * @param apTransID
	 *            new AP transaction id
	 * @param sigResp
	 * @throws MalformedURIException
	 */

	public MSS_ProfileReqType createProfileRequest(String msisdn,
			String apTransId) throws MalformedURIException {
		MSS_ProfileReqType req = new MSS_ProfileReqType();
		fillMatStuff(req, apTransId);

		if (msisdn == null)
			throw new IllegalArgumentException("null msisdn is not allowed.");
		MobileUser muObject = new MobileUser();
		muObject.setMSISDN(msisdn);
		req.setMobileUser(muObject);

		return req;
	}
	
	/**
	 * Creates a Registration request.
	 * 
	 * @param apTransId
	 *            not null.
	 * @param msisdn
	 *            not null.
	 * @param signatureProfile
	 * 
	 * @throws MalformedURIException
	 */
	public MSS_RegistrationReqType createRegistrationRequest(String apTransId,
			String msisdn, String signatureProfile) throws MalformedURIException {
		
		MSS_RegistrationReqType req = new MSS_RegistrationReqType();

		fillMatStuff(req, apTransId);

		if (msisdn == null)
			throw new IllegalArgumentException("null msisdn is not allowed.");
		
		MobileUser muObject = new MobileUser();
		muObject.setMSISDN(msisdn);
		req.setMobileUser(muObject);
		if(signatureProfile == null) {
			// do nothing
		} else {
			CertificateResponse[] certResp = new CertificateResponse[1];
			certResp[0] = new CertificateResponse();
			certResp[0].setCertificateURI(signatureProfile);
			req.setCertificateResponse(certResp);
		}
		return req;
	}

	/**
	 * Send the MSS_SignatureRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_SignatureReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */
	public MSS_SignatureRespType send(MSS_SignatureReqType req)
			throws IOException {
		if (req.getAdditionalServices().length == 0)
			req.setAdditionalServices(null);
		return (MSS_SignatureRespType) send((MessageAbstractType) req);
	}

	/**
	 * Send the MSS_ReceiptRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_ReceiptReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */

	public MSS_ReceiptRespType send(MSS_ReceiptReqType req) throws IOException {
		return (MSS_ReceiptRespType) send((MessageAbstractType) req);
	}

	/**
	 * Send the MSS_HandshakeRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_HandshakeReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */
	/*
	 * public MSS_HandshakeResp send(MSS_HandshakeReq req) throws IOException {
	 * return (MSS_HandshakeResp)send((MessageAbstractType)req); }
	 */
	/**
	 * Send the MSS_StatusRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_StatusReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */

	public MSS_StatusRespType send(MSS_StatusReqType req) throws IOException {
		return (MSS_StatusRespType) send((MessageAbstractType) req);
	}

	/**
	 * Send the MSS_ProfileRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_ProfileReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */

	public MSS_ProfileRespType send(MSS_ProfileReqType req) throws IOException {
		return (MSS_ProfileRespType) send((MessageAbstractType) req);
	}

	/**
	 * Send the MSS_RegistrationRequest to MSS system receiving answer
	 * 
	 * @param req
	 *            the MSS_RegistrationReq
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */
	
	 public MSS_RegistrationRespType send(MSS_RegistrationReqType req) throws IOException {
		 return (MSS_RegistrationRespType)send((MessageAbstractType)req); }
	 
	/**
	 * Sends a signature request.
	 *
	 * @exception IOException
	 *                if a HTTP communication error occurred i.e. a SOAP fault
	 *                was generated by the <i>local</i> SOAP client stub.
	 */
	private MessageAbstractType send(MessageAbstractType req) throws AxisFault,
			IOException {
		Stub port = null;
		try {
			long timeout = 0;
			if (req instanceof MSS_SignatureReqType) {
				timeout = ((MSS_SignatureReqType) req).getTimeOut().longValue();
				port = (MSS_SignatureBindingStub) mssSignatureService
						.getMSS_SignaturePort(MSSP_SI_URL);
			} else if (req instanceof MSS_ReceiptReqType) {
				port = (MSS_ReceiptBindingStub) mssSignatureService
						.getMSS_ReceiptPort(MSSP_RC_URL);
				// debug..
				// } else if (req instanceof MSS_HandshakeReq) {
				// port =
				// (MSS_HandshakeBindingStub)mssService.getMSS_HandshakePort(MSSP_HS_URL);
				// // debug..
			} else if (req instanceof MSS_StatusReqType) {
				port = (MSS_StatusQueryBindingStub) mssSignatureService
						.getMSS_StatusQueryPort(MSSP_ST_URL);
				// debug..
			} else if (req instanceof MSS_ProfileReqType) {
				port = (MSS_ProfileQueryBindingStub) mssSignatureService
						.getMSS_ProfilePort(MSSP_PR_URL);
				// debug..
			} else if (req instanceof MSS_RegistrationReqType) {
				port = (MSS_RegistrationBindingStub) mssSignatureService
						.getMSS_RegistrationPort(MSSP_RG_URL);
			}
			if (timeout > 0)
				port.setTimeout((int) (timeout * 1000) + 1000);
		} catch (ServiceException se) {
			log.debug("ServiceException");
			throw new IOException(se.getMessage());
		}
		try {
			if (port._getCall() == null) {
				port._createCall();
			}
		} catch (Exception e) {
			// huh? should never happen..
			log.error("Can not do port._createCall(), SHOULD NEVER HAPPEN", e);
		}

		MessageContext clientContext = port._getCall().getMessageContext();

		try {
			if (port instanceof MSS_SignatureBindingStub) {
				return ((MSS_SignatureBindingStub) port)
						.MSS_Signature((MSS_SignatureReqType) req);
			} else if (port instanceof MSS_StatusQueryBindingStub) {
				return ((MSS_StatusQueryBindingStub) port)
						.MSS_StatusQuery((MSS_StatusReqType) req);
			} else if (port instanceof MSS_ReceiptBindingStub) {
				return ((MSS_ReceiptBindingStub) port)
						.MSS_Receipt((MSS_ReceiptReqType) req);
				// } else if (port instanceof MSS_HandshakeBindingStub) {
				// return ((MSS_HandshakeBindingStub)port).
				// MSS_Handshake((MSS_HandshakeReq)req);
			} else if (port instanceof MSS_ProfileQueryBindingStub) {
				return ((MSS_ProfileQueryBindingStub) port)
						.MSS_ProfileQuery((MSS_ProfileReqType) req);
				
			} else if (port instanceof MSS_RegistrationBindingStub) {
				return ((MSS_RegistrationBindingStub)port)
						.MSS_Registration((MSS_RegistrationReqType)req);
			}
		} catch (AxisFault af) {
			log.error("AxisFault", af);
			throw af;
		} catch (RemoteException re) {
			log.error("RemoteException", re);
			throw re;
		}
		throw new IOException("Invalid call parameters");
	}

	/**
	 * Return whether s is a valid xs:NCName String.
	 */
	public static boolean isNCName(String s) {
		if (s == null) {
			return false;
		} else {
			return org.apache.axis.types.NCName.isValid(s);
		}
	}

}
