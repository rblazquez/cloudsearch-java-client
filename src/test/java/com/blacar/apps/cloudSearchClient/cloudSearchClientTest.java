package com.blacar.apps.cloudSearchClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.blacar.apps.cloudSearchClient.documents.AmazonCloudSearchAddRequest;
import com.blacar.apps.cloudSearchClient.search.AmazonCloudSearchQuery;
import com.blacar.apps.cloudSearchClient.search.AmazonCloudSearchResult;

public class cloudSearchClientTest {

	final static Logger log = Logger.getLogger(cloudSearchClientTest.class);

	private static Properties prop = null;
	private static AmazonCloudSearchService css = new AmazonCloudSearchService();

	{
		log.warn("REMEMBER TO SET UP YOUR OWN CLOUDSEARCH DATA BEFORE TESTING");
		log.warn("IN config.properties FILE");
		try {
			readProperties();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readProperties() throws FileNotFoundException {
		prop = new Properties();
		String propFileName = "config.properties";

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}
	}

	@Test
	public void testAddRequest() {
		log.info("*************************");
		log.info("*      testAddRequest   *");
		log.info("*************************");

		AmazonCloudSearchAddRequest addRequest = new AmazonCloudSearchAddRequest();
		addRequest.id = "abc123";
		addRequest.version = new Integer(123);
		addRequest.lang = "abc123";
		addRequest.addField("title", "abc123");
		addRequest.addField("rank", new Integer(123));
		addRequest.addField("actors", "Some Value");

		Long resultCount = null;
		try {
			resultCount = css.addDocument(addRequest);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		} catch (Exception ex) {
			System.out.println("Exception cached");
			throw new RuntimeException(ex);
		}
		Assert.assertNotEquals(null, resultCount);
	}

	@Test
	public void testAddRequestBad() {
		log.info("*************************");
		log.info("*      testAddRequestBad   *");
		log.info("*************************");

		AmazonCloudSearchAddRequest addRequest = new AmazonCloudSearchAddRequest();
		addRequest.id = "abc123";
		addRequest.version = new Integer(123);
		addRequest.lang = "abc123";
		addRequest.addField("xxxxx", "abc123");
		addRequest.addField("rank", new Integer(123));
		addRequest.addField("actors", "Some Value");

		Long resultCount = null;
		try {
			resultCount = css.addDocument(addRequest);
		} catch (Exception ex) {
			System.out.println("Exception cached");
			throw new RuntimeException(ex);
		}
		Assert.assertEquals(null, resultCount);
	}

	@Test
	public void testSearchRequest() {
		log.info("*************************");
		log.info("*      testSearchRequest   *");
		log.info("*************************");

		AmazonCloudSearchQuery csq = new AmazonCloudSearchQuery();
		csq.setFields("title", "plot");
		csq.query = "ab*";

		try {
			AmazonCloudSearchResult csr = css.search(csq);
			log.info(csr.getHits().toString());
			Assert.assertTrue(csr.getFound() > 0);
		} catch (Exception ex) {
			log.error("Exception cached", ex);
			throw new RuntimeException(ex);
		}
	}

}
