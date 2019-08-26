package com.my360face.AWSRekognition.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;

import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
@Service
public class BucketService  {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(BucketService.class);
    
	@Autowired
	private AWSRekognitionService aws;
	
    public BucketService() {

    }

    public String createBucket(String bucketName) {

        try {

            System.out.println("Creating bucket: " + bucketName);


            Bucket b = null;
            if (aws.getS3Client().doesBucketExistV2(bucketName)) {
                System.out.format("Bucket %s already exists.\n", bucketName);
                throw new AWSRekognitionException("Error: Bucket already exists.");

            } else {
                try {
                    b = aws.getS3Client().createBucket(bucketName);
                    System.out.println("Creating bucket " + bucketName + "\n");
                } catch (AmazonS3Exception e) {
                    System.err.println(e.getErrorMessage());
                }
            }
            return b.toString();
        } catch (Exception e) {
            throw new AWSRekognitionException("Error: create bucket unsuccessful: ", e);
        }
    }

    public List<Bucket> listBuckets(int limit) {

        try {

            System.out.println("Listing buckets");

            List<Bucket> buckets = aws.getS3Client().listBuckets();
            for (Bucket bucket : buckets) {
                System.out.println(bucket.toString());
            }

            return buckets;
        } catch (Exception e) {
            throw new AWSRekognitionException("Error: list bucket unsuccessful: ", e);
        }

    }
    public void createFolder(String bucketName,String folderName) {
    	final String SUFFIX = "/";
    	ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
//		List fileList = aws.getS3Client().listObjects(bucketName, folderName).getObjectSummaries();
//		System.out.println(fileList);
		if(aws.getS3Client().doesObjectExist(bucketName, folderName+"/")) {
			throw new AWSRekognitionException("Error: Folder already exists.");
		}
		else{
				try{
					// send request to S3 to create folder
				aws.getS3Client().putObject(putObjectRequest);
    		}
				catch(AmazonS3Exception e) {
					System.err.println(e.getErrorMessage());
				}
		}
	}
    
    public void deleteBucket(String bucketName){
    	   try {
    		   aws.getS3Client().deleteBucket(bucketName);
           } catch (Exception e) {
               throw new AWSRekognitionException("Error: delete bucket unsuccessful: ", e);
           }
    }
    
    public void deleteObject(String bucketName,String key) {
    	try {
    		aws.getS3Client().deleteObject(bucketName, key);
    	   } catch (Exception e) {
               throw new AWSRekognitionException("Error: delete Object unsuccessful: ", e);
           }
    }
    
    
}
