package com.blacar.apps.cloudSearchClient.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.cloudsearchdomain.model.QueryParser;
import com.amazonaws.services.cloudsearchdomain.model.SearchRequest;

public class AmazonCloudSearchQuery {
	final static Logger log = Logger.getLogger(AmazonCloudSearchQuery.class);
	
	public String cursor;

	public Map<String, String> expressions = new HashMap<String, String>();

	public List<Facet> facets = new ArrayList<Facet>();

	public String structuredQuery;

	public List<Highlight> highlights = new ArrayList<Highlight>();

	public Boolean partial;

	public String query;

	public Map<String, String> queryOptions = new HashMap<String, String>();

	public String queryParser;

	public String returnFields;

	public Long size;

	public Map<String, String> sort = new HashMap<String, String>();

	public Long start;

	public void addExpression(String name, String expression) {
		expressions.put(name, expression);
	}

	// for example, year desc,title asc.
	public void addSort(String fieldOrExpr, String direction) {
		sort.put(fieldOrExpr, direction);
	}

	public void addSort(String fieldOrExpr) {
		sort.put(fieldOrExpr, "asc");
	}

	public void setDefaultOperator(String operator) {
		queryOptions.put("defaultOperator", operator);
	}

	public void setFields(String... fields) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < fields.length; i++) {
			builder.append("'").append(fields[i]).append("'");
			if (i != fields.length - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		queryOptions.put("fields", builder.toString());
	}

	public void setPhraseFields(String... fields) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < fields.length; i++) {
			builder.append("'").append(fields[i]).append("'");
			if (i != fields.length - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		queryOptions.put("phraseFields", builder.toString());
	}

	public void setPhraseSlop(int phraseSlop) {
		queryOptions.put("phraseSlop", String.valueOf(phraseSlop));
	}

	public void setExplicitPhraseSlop(int explicitPhraseSlop) {
		queryOptions.put("explicitPhraseSlop",
				String.valueOf(explicitPhraseSlop));
	}

	public void setTieBreaker(double tieBreaker) {
		queryOptions.put("tieBreaker", String.valueOf(tieBreaker));
	}

	public void disableOperators(String... operators) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < operators.length; i++) {
			builder.append("'").append(operators[i]).append("'");
			if (i != operators.length - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		queryOptions.put("operators", builder.toString());
	}

	public void setReturnFields(String... returnFields) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < returnFields.length; i++) {
			builder.append(returnFields[i]);
			if (i != returnFields.length - 1) {
				builder.append(",");
			}
		}

		this.returnFields = builder.toString();
	}

	public SearchRequest build() throws UnsupportedEncodingException {
		SearchRequest sr = new SearchRequest();

		if (cursor != null) {
			log.info("SET CURSOR ... " + cursor);
			sr.setCursor(cursor);
		}

		if (expressions.size() > 0) {
			for (Map.Entry<String, String> entry : expressions.entrySet()) {
				log.info("SET EXPR ... " + entry.getValue());
				sr.setExpr(entry.getValue());
			}
		}

		if (facets.size() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for (Facet facet : facets) {
				log.info("SET FACET: " + facet.field);
				
				if (builder.length() > 0) {
					builder.append(",");
				}
				builder.append(facet.field).append(":");

				StringBuilder value = new StringBuilder();
				value.append("{");
				if (facet.sort != null) {
					value.append("sort").append(":").append("\"")
							.append(facet.sort).append("\"");
				}
				if (facet.buckets != null) {
					value.append("buckets").append(":").append(facet.buckets);
				}
				if (facet.size != null) {
					value.append("size").append(":").append(facet.size);
				}

				value.append("}");

				builder.append(value.toString());
			}
			builder.append("}");
		}

		if (structuredQuery != null) {
			log.info("SET FILTER QUERY: " + structuredQuery);			
			sr.setFilterQuery(structuredQuery);
		}

		if (highlights.size() > 0) {					
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for (Highlight highlight : highlights) {
				log.info("SET HIGHLIGHT: " + highlight.field);
				
				if (builder.length() > 0) {
					builder.append(",");
				}
				builder.append(highlight.field).append(":");

				StringBuilder value = new StringBuilder();
				value.append("{");
				if (highlight.format != null) {
					value.append("format").append(":").append("'")
							.append(highlight.format).append("'");
				}
				if (highlight.maxPhrases != null) {
					value.append("max_phrases").append(":")
							.append(highlight.maxPhrases);
				}
				if (highlight.preTag != null) {
					value.append("pre_tag").append(":").append("'")
							.append(highlight.preTag).append("'");
				}
				if (highlight.postTag != null) {
					value.append("post_tag").append(":").append("'")
							.append(highlight.postTag).append("'");
				}
				value.append("}");

				builder.append(value.toString());
			}
			builder.append("}");
		}

		if (partial != null) {
			log.info("IS PARTIAL: " + partial);
			sr.setPartial(partial);
		}

		if (query != null) {
			log.info("SET QUERY: " + query);
			sr.setQuery(query);
		}

		if (queryOptions.size() > 0) {			
			StringBuilder value = new StringBuilder();
			value.append("{");
			for (Map.Entry<String, String> entry : queryOptions.entrySet()) {				
				value.append(entry.getKey()).append(":")
						//.append("'")  <-- a JSON array do not need this
						.append(entry.getValue())
						//.append("'")  <-- a JSON array do not need this
						;
			}
			value.append("}");

			log.info("SET QUERY OPTIONS: " + value.toString());
			sr.setQueryOptions(value.toString());
		}

		if (queryParser != null) {
			QueryParser parser = QueryParser.fromValue(queryParser);
			sr.setQueryParser(parser);
		}

		if (returnFields != null) {
			log.info("SET RETURN FIELDS: " + returnFields);
			sr.setReturn(returnFields);
		}

		if (size != null) {
			log.info("SET SIZE: " + size);
			sr.setSize(size);
		}

		// for example, year desc,title asc.
		if (sort.size() > 0) {
			StringBuilder value = new StringBuilder();
			int i = 0;
			for (Map.Entry<String, String> entry : sort.entrySet()) {
				log.info("SET SORT: " + entry.getKey() + " " + entry.getValue());
				value.append(entry.getKey()).append(" ")
						.append(entry.getValue());
				if (i != sort.size() - 1) {
					value.append(",");
				}
			}
			sr.setSort(value.toString());
		}

		if (start != null) {
			log.info("SET START: " + start);
			sr.setStart(start);
		}

		return sr;
	}
}
