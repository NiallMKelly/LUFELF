package com.group6.lufelf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This class checks if the email is a valid email address.
 * 
 * @author Niall Kelly
 * @version 1.0.0
 * 
 */

public class ValidEmail {

	/**
	 * This method checks if the String passed to it is a valid email address,
	 * using regular expressions.
	 * 
	 * @param email
	 *            - The email you wish to check if it is valid.
	 * @return returns True if the email is valid, False if the email is not
	 *         valid.
	 */
	public boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

}
