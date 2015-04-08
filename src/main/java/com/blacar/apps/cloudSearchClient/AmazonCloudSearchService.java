package com.blacar.apps.cloudSearchClient;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClient;
import com.amazonaws.services.cloudsearchdomain.model.ContentType;
import com.amazonaws.services.cloudsearchdomain.model.DocumentServiceException;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.blacar.apps.cloudSearchClient.documents.AmazonCloudSearchAddRequest;
import com.blacar.apps.cloudSearchClient.documents.AmazonCloudSearchDeleteRequest;
import com.blacar.apps.cloudSearchClient.search.AmazonCloudSearchQuery;
import com.blacar.apps.cloudSearchClient.search.AmazonCloudSearchResult;

public class AmazonCloudSearchService {
	private AmazonCloudSearchDomainClient client;

	final static Logger log = Logger.getLogger(AmazonCloudSearchService.class);

	public AmazonCloudSearchService() {
	}

	public AmazonCloudSearchService(String awsAccessKey, String awsSecretKey,
			String documentEndpoint) {
		AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey,
				awsSecretKey);
		;
		this.client = new AmazonCloudSearchDomainClient(awsCredentials);
		this.client.setEndpoint(documentEndpoint);
	}

	void init(String awsAccessKey, String awsSecretKey, String documentEndpoint) {
		AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey,
				awsSecretKey);
		;

		this.client = new AmazonCloudSearchDomainClient(awsCredentials);
		this.client.setEndpoint(documentEndpoint);
	}

	public Long addDocument(AmazonCloudSearchAddRequest document) {
		JSONArray docs = new JSONArray();
		Long resultCount = null;
		try {
			docs.put(toJSON(document));
			System.out.println(docs.toString());
			UploadDocumentsResult result = updateDocumentRequest(docs
					.toString());
			return (result.getAdds());
		} catch (DocumentServiceException dse) {
			log.error(dse.getMessage());
		} catch (JSONException e) {
			log.error("", e);
		}
		return resultCount;
	}

	public Long addDocuments(List<AmazonCloudSearchDeleteRequest> documents) {
		JSONArray docs = new JSONArray();
		Long resultCount = null;
		try {
			for (AmazonCloudSearchDeleteRequest doc : documents) {
				docs.put(toJSON(doc));
			}
			UploadDocumentsResult result = updateDocumentRequest(docs
					.toString());
			return (result.getAdds());
		} catch (DocumentServiceException dse) {
			log.error(dse.getMessage());
		} catch (JSONException e) {
			log.error("", e);
		}
		return resultCount;
	}

	public Long deleteDocument(AmazonCloudSearchDeleteRequest document) {
		JSONArray docs = new JSONArray();
		Long resultCount = null;
		try {
			docs.put(toJSON(document));
			UploadDocumentsResult result = updateDocumentRequest(docs
					.toString());
			return (result.getDeletes());
		} catch (DocumentServiceException dse) {
			log.error(dse.getMessage());
		} catch (JSONException e) {
			log.error("", e);
		}
		return resultCount;
	}

	public Long deleteDocuments(List<AmazonCloudSearchDeleteRequest> documents) {
		Long resultCount = null;
		JSONArray docs = new JSONArray();
		try {
			for (AmazonCloudSearchDeleteRequest doc : documents) {
				docs.put(toJSON(doc));
			}
			UploadDocumentsResult result = updateDocumentRequest(docs
					.toString());
			return resultCount = result.getDeletes();
		} catch (DocumentServiceException dse) {
			log.error(dse.getMessage());
		} catch (JSONException e) {
			log.error("", e);
		}
		return resultCount;
	}

	private UploadDocumentsResult updateDocumentRequest(String body)
			throws DocumentServiceException {

		ByteArrayInputStream bais;
		try {
			bais = new ByteArrayInputStream(body.getBytes("UTF-8"));
			UploadDocumentsRequest req = new UploadDocumentsRequest();
			req.setDocuments(bais);
			req.setContentLength(new Integer(body.length()).longValue());
			req.setContentType(ContentType.Applicationjson.toString());

			UploadDocumentsResult result = client.uploadDocuments(req);
			log.warn("Result Warnings: " + result.getWarnings().toString());
			return result;
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	public AmazonCloudSearchResult search(AmazonCloudSearchQuery query) {
		AmazonCloudSearchResult results = null;
		try {
			SearchResult sr = client.search(query.build());

			results = new AmazonCloudSearchResult();
			results.setHits(sr.getHits().getHit());
			results.setFound(sr.getHits().getFound());
			results.setStart(sr.getHits().getStart());
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}

		return results;
	}

	private Object toJSON(AmazonCloudSearchDeleteRequest document)
			throws JSONException {
		JSONObject doc = new JSONObject();
		doc.put("type", "delete");
		doc.put("id", document.id.toLowerCase());
		doc.put("version", document.version);
		return doc;
	}

	private JSONObject toJSON(AmazonCloudSearchAddRequest document)
			throws JSONException {
		JSONObject doc = new JSONObject();
		doc.put("type", "add");
		doc.put("id", document.id.toLowerCase());
		doc.put("version", document.version);
		doc.put("lang", document.lang);

		JSONObject fields = new JSONObject();
		for (Map.Entry<String, Object> entry : document.fields.entrySet()) {
			if (entry.getValue() instanceof Collection) {
				JSONArray array = new JSONArray();
				Iterator i = ((Collection) entry.getValue()).iterator();
				while (i.hasNext()) {
					array.put(i.next());
				}
				fields.put(entry.getKey(), array);
			} else {
				fields.put(entry.getKey(), entry.getValue());
			}
		}
		doc.put("fields", fields);
		return doc;
	}
}
