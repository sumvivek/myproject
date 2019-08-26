package com.my360face.AWSRekognition.controller;

import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import com.my360face.AWSRekognition.service.CollectionService;
import com.my360face.security.payloads.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class CollectionController extends AWSRekognitonController{

    private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);

    @Autowired
    private CollectionService collectionService;

    @PostMapping("/createcollection")
    public ResponseEntity<?> createCollection(@RequestParam("collectionId") String collectionId) {

        if (null == collectionId || collectionId.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Collection is Invalid!"), HttpStatus.BAD_REQUEST);
        }

        try {
            collectionService.createCollecton(collectionId);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Collection is already taken!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse(true, "AWS Rekognition Collection Created Successfully"), HttpStatus.OK);
    }

    @GetMapping("/listcollection")
    public ListCollectionsResult listCollections(@RequestParam("limit") int limit) {

        try {
            return collectionService.listCollections(limit);
        } catch (AWSRekognitionException ae) {
            //return new ResponseEntity(new ApiResponse(false, "AWS Rekognition Collection is already taken!"), HttpStatus.BAD_REQUEST);
            logger.error("list collection unsuccessful.");
        }

        return null;
    }
    
    @DeleteMapping("/deletecollection")
    public  ResponseEntity<?> deleteCollection(@RequestParam("collectionId")String collectionId){
        if (null == collectionId || collectionId.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition Collection is Invalid!"), HttpStatus.BAD_REQUEST);
        }
        try {
            collectionService.deleteCollection(collectionId);
            return new ResponseEntity<>(new ApiResponse(true, "AWS Rekognition Collection Deleted Successfully"), HttpStatus.OK);
        } catch (AWSRekognitionException ae) {
            return new ResponseEntity<>(new ApiResponse(false, "No such collection is found..!"), HttpStatus.BAD_REQUEST);
        }
    	
    }



}
