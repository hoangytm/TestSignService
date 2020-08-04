package com.tomicalab.mobileid;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class VerificationCodeCalculator {

  /**
   * The Verification Code (VC) is computed as:
   * <p>
   * integer(SHA256(hash)[−2:−1]) mod 10000
   * <p>
   * where we take SHA256 result, extract 2 rightmost bytes from it,
   * interpret them as a big-endian unsigned integer and take the last 4 digits in decimal for display.
   * <p>
   * SHA256 is always used here, no matter what was the algorithm used to calculate hash.
   *
   * @param documentHash hash used to calculate verification code.
   * @return verification code.
   */
  public static String calculate(byte[] documentHash) {
    byte[] digest = hash256(documentHash);
    ByteBuffer byteBuffer = ByteBuffer.wrap(digest);
    int shortBytes = Short.SIZE / Byte.SIZE; // Short.BYTES in java 8
    
    int rightMostBytesIndex = byteBuffer.limit() - shortBytes;
    short twoRightmostBytes = byteBuffer.getShort(rightMostBytesIndex);
    short twoLeftmostBytes = byteBuffer.getShort(0);
    
    int rightPositiveInteger = ((int) twoRightmostBytes) & 0xffff;
    int leftPositiveInteger = ((int) twoLeftmostBytes) & 0xffff;
   
    String rightCode = String.valueOf(rightPositiveInteger);
    String leftCode = String.valueOf(leftPositiveInteger);
    
    String rightPaddedCode = "0000" + rightCode;
    String leftPaddedCode = "0000" + leftCode;
    String finalCode = rightPaddedCode.substring(rightPaddedCode.length()-3) + leftPaddedCode.substring(leftPaddedCode.length()-3);
    return finalCode;
    
    
  }
  
  private static byte[] hash256(byte[] data) {
	  byte[] hashData = null;
	  try {
		  MessageDigest md = MessageDigest.getInstance("SHA-256");
		  md.update(data);
		  hashData = md.digest();
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
	  return hashData;
  }
}
