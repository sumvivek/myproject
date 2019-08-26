/**
 * 
 */
package com.my360face.AWSRekognition.payloads;


/**
 * @author AshokSelva
 *
 * Date:04/17/2019
 */
public class IndexResponse {

		
		private String faceURL;
		private java.util.List<String> faceId;
		/**
		 * @param faceURL
		 * @param 
		 */
		public IndexResponse(String faceURL, java.util.List<String> faceId) {
			this.faceURL = faceURL;
			this.faceId = faceId;
		}
		/**
		 * @return the faceURL
		 */
		public String getFaceURL() {
			return faceURL;
		}
		/**
		 * @param faceURL the faceURL to set
		 */
		public void setFaceURL(String faceURL) {
			this.faceURL = faceURL;
		}
		/**
		 * @return the faceId
		 */
		public java.util.List<String> getFaceId() {
			return faceId;
		}
		/**
		 * @param faceId the faceId to set
		 */
		public void setFaceId(java.util.List<String> faceId) {
			this.faceId = faceId;
		}
		
		

	}
