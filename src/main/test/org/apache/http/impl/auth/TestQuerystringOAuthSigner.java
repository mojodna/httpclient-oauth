package org.apache.http.impl.auth;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestQuerystringOAuthSigner extends TestCase {
	// ------------------------------------------------------------ Constructor
	public TestQuerystringOAuthSigner(final String testName) throws IOException {
		super(testName);
	}

	// ------------------------------------------------------------------- Main
	public static void main(String args[]) {
		String[] testCaseName = { TestQuerystringOAuthSigner.class.getName() };
		junit.textui.TestRunner.main(testCaseName);
	}

	// ------------------------------------------------------- TestCase Methods

	public static Test suite() {
		TestSuite suite = new TestSuite(TestQuerystringOAuthSigner.class);
		return suite;
	}
}
