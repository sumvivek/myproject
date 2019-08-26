package com.my360face.AWSRekognition.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.rekognition.model.Celebrity;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.Face;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import com.my360face.AWSRekognition.payloads.IndexResponse;
import com.my360face.AWSRekognition.payloads.SearchResponse;
import com.my360face.AWSRekognition.service.FaceService;
import com.my360face.AWSRekognition.service.OpenCVFaceService;
import com.my360face.image.exception.ImageStorageException;
import com.my360face.security.payloads.ApiResponse;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")

public class FaceController extends AWSRekognitonController{

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FaceController.class);

    @Autowired
	public FaceService faceService;
    
    @Autowired
    public OpenCVFaceService openCV;

    private static final List<String> contentTypesImage = Arrays.asList("image/png", "image/jpeg", "image/jpg","image/bmp","image/gif","image/ief");
    
    @PostMapping("/enroll")
    public IndexResponse indexFace(	@RequestParam("image") MultipartFile image,
    								@RequestParam(value="multipleFace",required=false)Boolean multipleFace,
						    		@RequestParam(value="multipleEnroll",required=false)Boolean multipleEnroll,
						    		@RequestParam(value="bucket",required=false)String bucket,
						    		@RequestParam(value="gallery_name",required=false)String galleryName) {
    	
    	    	
    //setting default values for multipleFace and multipleEnroll
    	if(multipleFace==null) {
    		multipleFace=false;
    	}
    	if(multipleEnroll==null) {
    		multipleEnroll=false;
    	}
    //checks whether the file uploaded is image or not	
    	if(contentTypesImage.contains(image.getContentType())&&image!=null) {
    	  
    		//check whether the face is already indexed or not
    		SearchResponse searchResponse=faceService.verify(image,bucket,galleryName);
    		int indexCount=searchResponse.getFaceId().size();
	    	
    		if(indexCount==0) {
		    	IndexResponse response = (IndexResponse) faceService.indexValidate(image,multipleFace,false,bucket,galleryName);
		    	return response;
	    	}
	    	else if (indexCount>0 && multipleEnroll==true) {
	    		IndexResponse response = (IndexResponse) faceService.indexValidate(image,multipleFace,false,bucket,galleryName);
		    	return response;
	    	}
	    	else {
	    		throw new AWSRekognitionException("The face submited is already enrolled..!");
	    	}
	    	
        }
    	else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
    }

    @PostMapping("/verify")
    public SearchResponse verify(@RequestParam("image") MultipartFile image,
    							 @RequestParam(value="bucket",required=false)String bucket,
    							 @RequestParam(value="gallery_name",required=false)String galleryName) {
    	
    	if(contentTypesImage.contains(image.getContentType())&&image!=null) {
	    	int faceCount = faceService.detectFaces(image).size();
	    	if (faceCount==1) {
	    		SearchResponse searchResponse=faceService.verify(image,bucket,galleryName);
	    		return searchResponse;
	    	}
	    	else throw new AWSRekognitionException("Please upload image with single face - Valid FaceImage required..!");     
	    }
    	else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
    	
    }
    
    @PostMapping("/recognize")
    public SearchResponse  searchFaces(@RequestParam("image") MultipartFile image,@RequestParam(value="multipleFace",required=false)Boolean multipleFace,
								 	   @RequestParam(value="bucket",required=false)String bucket,
									   @RequestParam(value="gallery_name",required=false)String galleryName) {
    	
    	if(multipleFace==null) {
    		multipleFace=false;
    	}
    	// Normalize file name
    	 String imageName = StringUtils.cleanPath(image.getOriginalFilename());
    	 
         // Check if the file's name contains invalid characters
         if(imageName.contains("..")) {
                throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
         }
         else if(contentTypesImage.contains(image.getContentType())&&image!=null) {
        	 
         SearchResponse response = (SearchResponse) faceService.indexValidate(image, multipleFace,true,bucket,galleryName);
          	return response;
         }
         else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");            
         	
    }
      
    
    @PostMapping("/detectface")
    public List<?> detectFace(	@RequestParam("image")MultipartFile image,
    									@RequestParam(value="detectionMode",required=false)String DetectionMode){
    		if(DetectionMode==null) {
    			DetectionMode="aws";
    		}
    	
    		if(contentTypesImage.contains(image.getContentType()) && image!=null) {

    			switch(DetectionMode.toLowerCase()) {
//    				case "opencv":
//    					System.out.printf("DetectionMode: %s  ",DetectionMode);
//    					return openCV.detectFaces(image);
    				case "aws":
    					System.out.println("DetectionMode : AWS");
    					return faceService.detectFaces(image);
    				default:
    					System.out.println("DetectionMode : AWS-default");
    					return faceService.detectFaces(image);

			}
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
    }
    
    
    @PostMapping("/comparefaces")
    public List<CompareFacesMatch> compareFaces(@RequestParam("source")MultipartFile source,@RequestParam("target")MultipartFile target) throws IOException {
    
    		if(contentTypesImage.contains(source.getContentType()) && contentTypesImage.contains(target.getContentType()) && target!=null && source!=null) {
    				return faceService.compareFaces(source, target);
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
    	
    }
    
    
    @PostMapping("/recognizecelebrities")
    public List<Celebrity> recognizeCelebrities(@RequestParam(value="image",required=true)MultipartFile image){
    	
    		if(contentTypesImage.contains(image.getContentType()) && image!=null) {
    				return faceService.recognizeCelebrities(image);
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
   
    }
    
    
    
    @PostMapping("/detectobject")
    public ArrayList<?> detectObject(@RequestParam(value="image",required=true)MultipartFile image){
    	
    		if(contentTypesImage.contains(image.getContentType())&& image!=null) {
    				return faceService.detectObject(image);
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
		
    }
    
    
    @PostMapping("/detecttext")
    public ArrayList<?> detectText(@RequestParam(value="image",required=true)MultipartFile image){
    	
    		if(contentTypesImage.contains(image.getContentType())&& image!=null) {
    				return faceService.detectText(image);
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");
	
    }
    
    
    @PostMapping("/detect_unsafe_content")
    public ArrayList<?> detectUnsafeContent(@RequestParam(value="image",required=true)MultipartFile image,
    										@RequestParam(value="bucket",required=false)String bucket){
    	
    		if(contentTypesImage.contains(image.getContentType())) {
    				return faceService.detectUnsafeContent(image,bucket);
    			}
    		else throw new ImageStorageException("Sorry! The uploaded file is not a image file..!");

    }
   
    @GetMapping("/getfacecountlist")
    public List<String> getFaceCount(@RequestParam("limit") int limit) {
        if(limit<1) {  
        	throw new AWSRekognitionException("Limit is invalid..!");
    	}
    	return faceService.getFaceCount(limit);     
    }
    
    
    @GetMapping("/getfacecount")
    public String indexdedfacecount(@RequestParam("gallery_name") String gallery_name){
    	if(null==gallery_name) {
    		throw new AWSRekognitionException("gallery_name is invalid..!");
    	}
    	return faceService.getFaceCount(gallery_name);    
    }
    
    
    @GetMapping("/getfacelist")
    public List <Face> getFaceList(@RequestParam("gallery_name")String collectionId,@RequestParam(value="limit",required=true)int limit){
			if(null==collectionId||collectionId.isEmpty()||limit<1) {
				throw new AWSRekognitionException("gallery_name/limit is invalid..!");
			}
			return faceService.listFaces(collectionId,limit);
    }
    
    
    @DeleteMapping("/deleteface")
    public ResponseEntity<?> deleteFace(@RequestParam("faceId")String faceId,@RequestParam(value="gallery_name",required=false)String collectionId){
    	   if (null == faceId || faceId.isEmpty()) {
               return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition faceID is Invalid!"), HttpStatus.BAD_REQUEST);
           }
           try {
               faceService.deleteFaceFromCollection(faceId,collectionId);
               return new ResponseEntity<>(new ApiResponse(true, "Face id "+faceId+" has been successfully removed"), HttpStatus.OK);
           } 
           catch (AWSRekognitionException ae) {
               return new ResponseEntity<>(new ApiResponse(false, "FaceID not found..!"), HttpStatus.BAD_REQUEST);
           }
      }
    
    
    @DeleteMapping("/deleteallface")
    public ResponseEntity<?> deleteAllFace(@RequestParam("gallery_name")String collectionId){
    	   if (null == collectionId || collectionId.isEmpty()) {
               return new ResponseEntity<>(new ApiResponse(false, "AWS Rekognition faceID is Invalid!"), HttpStatus.BAD_REQUEST);
           }
           try {
               faceService.deleteAllFace(collectionId);
               return new ResponseEntity<>(new ApiResponse(true, "All face details have been successfully removed..!"), HttpStatus.OK);
           } 
           catch (AWSRekognitionException ae) {
               return new ResponseEntity<>(new ApiResponse(false, "Collection not found..!"), HttpStatus.BAD_REQUEST);
           }
      }

}
