package com.my360face.AWSRekognition.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import com.my360face.AWSRekognition.service.BucketService;
import com.my360face.security.payloads.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class BucketController extends AWSRekognitonController{

    private static final Logger logger = LoggerFactory.getLogger(BucketController.class);

    @Autowired
    private BucketService bucketService;

    @PostMapping("/createbucket")
    public ResponseEntity<?> createBucket(@RequestParam("bucketName") String bucketName) {

        if (null == bucketName || bucketName.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Bucket Name is Invalid!"), HttpStatus.BAD_REQUEST);
        }

        try {
            bucketService.createBucket(bucketName);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Bucket Name is already exists!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse(true, "AWS Rekognition Bucket Created Successfully"), HttpStatus.OK);
    }
    
    @PostMapping("/createfolder")
    public ResponseEntity<?> createFolder(@RequestParam("bucketName") String bucketName,@RequestParam("folderName") String folderName) {

        if (null == bucketName || bucketName.isEmpty() || null==folderName || folderName.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Bucket/Folder Name is Invalid!"), HttpStatus.BAD_REQUEST);
        }

        try {
            bucketService.createFolder(bucketName, folderName);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "Folder already exists!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse(true, folderName+" folder was created successfully inside "+bucketName+" bucket"), HttpStatus.OK);
    }

    @GetMapping("/listbucket")
    public List<Bucket> listCollections(@RequestParam("limit") int limit) {

        try {
            return bucketService.listBuckets(limit);
        } catch (AWSRekognitionException ae) {
            //return new ResponseEntity(new ApiResponse(false, "AWS Rekognition Bucket Name is already taken!"), HttpStatus.BAD_REQUEST);
            logger.error("list bucket unsuccessful.");
        }

        return null;
    }
    
    
    @DeleteMapping("/deletebucket")
    public ResponseEntity<?>  deleteBucket(@RequestParam("bucketName")String bucketName) {
    	if (null == bucketName || bucketName.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Bucket Name is Invalid!"), HttpStatus.BAD_REQUEST);
        }
    	try {
    		bucketService.deleteBucket(bucketName);
    		return new ResponseEntity<>(new ApiResponse(true, "AWS Rekognition Bucket Deleted Successfully"), HttpStatus.OK);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "No such Bucket exists!"), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/deleteObject")
    public ResponseEntity<?>  deleteObject(@RequestParam("bucketName")String bucketName,@RequestParam("key")String key) {
    	if (null == bucketName || bucketName.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Bucket Name is Invalid!"), HttpStatus.BAD_REQUEST);
        }
    	if (null == key || key.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "key is Invalid!"), HttpStatus.BAD_REQUEST);
        }
    	try {
    		bucketService.deleteObject(bucketName, key);
    		return new ResponseEntity<>(new ApiResponse(true, "Object Deleted Successfully"), HttpStatus.OK);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "No such object exists!"), HttpStatus.BAD_REQUEST);
        }
    }

}
