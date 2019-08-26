package com.my360face.AWSRekognition.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AWSRekognitionService {

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(AWSRekognitionService.class);
    final AmazonRekognition rekognitionClient;
    private final AmazonS3 s3Client;
    final AmazonSQS sqsClient;

    //@Autowired
    public AWSRekognitionService() {

        //Get AWS Rekognition client
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("my360").getCredentials();
            	
            
            this.rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        } catch (Exception e) {
            throw new AWSRekognitionException("Cannot load the credentials: ", e);
        }

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        
        sqsClient=AmazonSQSClientBuilder.standard()
        		.withRegion(Regions.US_EAST_1)
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.build();
    }
 
    public AmazonRekognition getRekognitionClient() {
        return rekognitionClient;
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }
    
    public AmazonSQS getSQSclient() {
    	return sqsClient;
    }
}
