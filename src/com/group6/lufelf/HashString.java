package com.group6.lufelf;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 
 * This class converts a String into a Hashed string using MD5 encrption.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class HashString {

	/**
	 * This method takes in a string and encrypts it using MD5 which is then
	 * returned as a seperate string.
	 * 
	 * @param str
	 *            - String you wish to Encrypt
	 * @return MD5 - String hashed using MD5 Encryption.
	 * @throws Exception
	 */
	public String HashString(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes(), 0, str.length());
		String MD5 = new BigInteger(1, md.digest()).toString(16);
		System.out.println("MD5: " + MD5);
		return MD5;
	}

}
