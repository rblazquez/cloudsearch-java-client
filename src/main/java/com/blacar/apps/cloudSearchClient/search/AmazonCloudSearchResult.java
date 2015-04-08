package com.blacar.apps.cloudSearchClient.search;

import java.util.List;

import com.amazonaws.services.cloudsearchdomain.model.Hit;

/**
 * Result of a query executed on Amazon Cloud Search.
 * 
 * @author Tahseen Ur Rehman Fida
 * @email tahseen.ur.rehman@gmail.com
 *
 */
public class AmazonCloudSearchResult {
	public String rid;
	
	public long time;
	
	public Long found;
	
	public Long start;
	
	public List<Hit> hits;
	
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Long getFound() {
		return found;
	}

	public void setFound(Long found) {
		this.found = found;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public List<Hit> getHits() {
		return hits;
	}

	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
}
