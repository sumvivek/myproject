/**
 * 
 */
package com.my360face.AWSRekognition.payloads;

import java.util.List;

/**
 * @author AshokSelva
 *
 * Date:04/17/2019
 */
public class SearchResponse {
	
	public String faceUrl;
	private java.util.List<String> faceId;
		
	public SearchResponse(String faceUrl, List<String> faceId) {
		this.faceUrl = faceUrl;
		this.faceId = faceId;
	}	

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}
	
	public java.util.List<String> getFaceId() {
		return faceId;
	}

	public void setFaceId(java.util.List<String> faceId) {
		this.faceId = faceId;
	}

	


	

}
