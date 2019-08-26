package com.google.cloude.vision.samples.imagedetect;

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
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

/**
 * A sample application that uses the Vision API to label an image.
 */
@SuppressWarnings("serial")

public class ImageApp {
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
	   /* if (args.length != 1) {
	      System.err.println("Missing imagePath argument.");
	      System.err.println("Usage:");
	      System.err.printf("\tjava %s imagePath\n", LabelApp.class.getCanonicalName());
	      System.exit(1);
	    }*/
	    Path imagePath = Paths.get("D:\\workspaceg\\image.jpg");
	    ImageApp app = new ImageApp(getVisionService());
	    ImageProperties images  = app.detectImageProperties(imagePath, MAX_RESULTS);
	    System.out.printf("Writing to file %s\n", images);
	   // System.out.printf(images);
	  }

	  /**
	   * Prints the labels received from the Vision API.
	   */
	 /* public static void printImages(PrintStream out, Path imagePath, ImageProperties images) {
	    out.printf("Properties for image %s:\n", imagePath);
	  /* for (DominantColorsAnnotation image : images) {
	      out.printf(
	          "\t%s (score: %.3f)\n",
	          image.getDominantColors());
	    }*/
	   // out.printf(
		         // "\t%s (score: %.3f)\n",
		        //  images.getDominantColors());
	  //  if (images.isEmpty()) {
	     // out.println("\tNo properties found.");
	   // }
	 // }
	  // [END run_application]

	  // [START authenticate]
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
	  // [END authenticate]

	  private final Vision vision;

	  /**
	   * Constructs a {@link LabelApp} which connects to the Vision API.
	   */
	  public ImageApp(Vision vision) {
	    this.vision = vision;
	  }

	  /**
	   * Gets up to {@code maxResults} labels for an image stored at {@code path}.
	   */
	  public ImageProperties  detectImageProperties(Path path, int maxResults) throws IOException {
	    // [START construct_request]
	    byte[] data = Files.readAllBytes(path);

	    AnnotateImageRequest request =
	        new AnnotateImageRequest()
	            .setImage(new Image().encodeContent(data))
	            .setFeatures(ImmutableList.of(
	                new Feature()
	                    .setType("IMAGE_PROPERTIES")
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
	    if (response.getImagePropertiesAnnotation() == null) {
	      throw new IOException(
	          response.getError() != null
	              ? response.getError().getMessage()
	              : "Unknown error getting image annotations");
	    }
	    return response.getImagePropertiesAnnotation();
	    // [END parse_response]
	  }

}
