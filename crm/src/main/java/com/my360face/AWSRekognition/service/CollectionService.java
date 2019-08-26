package com.my360face.AWSRekognition.service;

import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import com.my360face.image.property.ImageStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CollectionService  {

	@Autowired
	private AWSRekognitionService aws;
	
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

    public CollectionService(ImageStorageProperties fileStorageProperties) {

    }

    public String createCollecton(String collectionId) {

        try {

            System.out.println("Creating collection: " + collectionId);

            CreateCollectionRequest request = new CreateCollectionRequest()
                    .withCollectionId(collectionId);

            CreateCollectionResult createCollectionResult = aws.getRekognitionClient().createCollection(request);
            System.out.println("CollectionArn : " +
                    createCollectionResult.getCollectionArn());
            System.out.println("Status code : " +
                    createCollectionResult.getStatusCode().toString());

            return createCollectionResult.getStatusCode().toString();

        } catch (Exception e) {
            throw new AWSRekognitionException("Error: create collection unsuccessful: ", e);
        }
    }

    public ListCollectionsResult listCollections(int limit) {

        try {

            System.out.println("Listing collections");

            ListCollectionsResult listCollectionsResult = null;
            String paginationToken = null;
            do {
                if (listCollectionsResult != null) {
                    paginationToken = listCollectionsResult.getNextToken();
                }
                ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
                        .withMaxResults(limit)
                        .withNextToken(paginationToken);
                listCollectionsResult = aws.getRekognitionClient().listCollections(listCollectionsRequest);

                List<String> collectionIds = listCollectionsResult.getCollectionIds();
                for (String resultId : collectionIds) {
                    System.out.println(resultId);
                }
            } while (listCollectionsResult != null && listCollectionsResult.getNextToken() !=
                    null);

            return listCollectionsResult;
        } catch (Exception e) {
            throw new AWSRekognitionException("Error: list collection unsuccessful: ", e);
        }

    }
    
    public String deleteCollection(String collectionId) {

        try {

            System.out.println("Deleting collection: " + collectionId);
            DeleteCollectionRequest request =new DeleteCollectionRequest().withCollectionId(collectionId);
            DeleteCollectionResult result =aws.getRekognitionClient().deleteCollection(request);
            return result.getStatusCode().toString();

        } catch (Exception e) {
            throw new AWSRekognitionException("Error: delete collection unsuccessful: ", e);
        }
    }
}
