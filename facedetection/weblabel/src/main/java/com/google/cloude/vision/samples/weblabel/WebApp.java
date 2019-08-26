package com.google.cloude.vision.samples.weblabel;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebLabel;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;


/**
 * A sample application that uses the Vision API to label an image.
 */
@SuppressWarnings("serial")


public class WebApp {
	/**
	   * Be sure to specify the name of your application. If the application name is {@code null} or
	   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	   */
	private static final String APPLICATION_NAME = "Google-VisionDetectLandmark/1.0";

	 // private static final int MAX_LABELS = 3;

	private static final int MAX_RESULTS = 100;

	  // [START run_application]
	  /**
	   * Annotates an image using the Vision API.
	   */
	public static void main(String[] args) throws IOException, GeneralSecurityException {
	Path imagePath = Paths.get("D:\\workspaceg\\quinten.jpg");
	WebApp app = new WebApp(getVisionService());
	WebDetection weblabel = app.detectMultipleObjects(imagePath, MAX_RESULTS);
	    System.out.printf("Logo description: %s\n", weblabel);
	  
	  }

	  /**
	   * Connects to the Vision API using Application Default Credentials.
	   */
	  public static Vision getVisionService() throws IOException, GeneralSecurityException {
	    GoogleCredential credential =
	        GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
	    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	    return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
	            .setApplicationName(APPLICATION_NAME)
	            .build();
	  }

	  private final Vision vision;

	  /**
	   * Constructs a {@link DetectLandmark} which connects to the Vision API.
	   */
	  public WebApp(Vision vision) {
	    this.vision = vision;
	  }

	  /**
	   * Gets up to {@code maxResults} landmarks for an image stored at {@code uri}.
	   */
	  public WebDetection detectMultipleObjects(Path path, int maxResults) throws IOException {
		  byte[] data = Files.readAllBytes(path);

		    AnnotateImageRequest request =
		        new AnnotateImageRequest()
		            .setImage(new Image().encodeContent(data))
		            .setFeatures(ImmutableList.of(
		                new Feature()
		                    .setType("WEB_DETECTION")
		                    .setMaxResults(maxResults)));
		    Vision.Images.Annotate annotate =
		        vision.images()
		            .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
		    // Due to a bug: requests to Vision API containing large images fail when GZipped.
		    annotate.setDisableGZipContent(true);
		    // [END construct_request]

		    // [START parse_response]
		    BatchAnnotateImagesResponse batchResponse = annotate.execute();
		    assert batchResponse.getResponses().size() == 1;
		    AnnotateImageResponse response = batchResponse.getResponses().get(0);
		    if (response.getWebDetection()== null) {
		      throw new IOException(
		          response.getError() != null
		              ? response.getError().getMessage()
		              : "Unknown error getting image annotations");
		    }
		   return response.getWebDetection();

	  }
	

}
