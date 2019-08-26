package com.my360face.AWSRekognition.service;
/**
 * @author AshokSelva
 *
 * Date:05/15/2019
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.rekognition.model.CelebrityDetail;
import com.amazonaws.services.rekognition.model.CelebrityRecognition;
import com.amazonaws.services.rekognition.model.CelebrityRecognitionSortBy;
import com.amazonaws.services.rekognition.model.ContentModerationDetection;
import com.amazonaws.services.rekognition.model.ContentModerationSortBy;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetection;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceSearchSortBy;
import com.amazonaws.services.rekognition.model.GetCelebrityRecognitionRequest;
import com.amazonaws.services.rekognition.model.GetCelebrityRecognitionResult;
import com.amazonaws.services.rekognition.model.GetContentModerationRequest;
import com.amazonaws.services.rekognition.model.GetContentModerationResult;
import com.amazonaws.services.rekognition.model.GetFaceDetectionRequest;
import com.amazonaws.services.rekognition.model.GetFaceDetectionResult;
import com.amazonaws.services.rekognition.model.GetFaceSearchRequest;
import com.amazonaws.services.rekognition.model.GetFaceSearchResult;
import com.amazonaws.services.rekognition.model.GetLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.GetLabelDetectionResult;
import com.amazonaws.services.rekognition.model.GetPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.GetPersonTrackingResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.LabelDetection;
import com.amazonaws.services.rekognition.model.LabelDetectionSortBy;
import com.amazonaws.services.rekognition.model.NotificationChannel;
import com.amazonaws.services.rekognition.model.Parent;
import com.amazonaws.services.rekognition.model.PersonDetection;
import com.amazonaws.services.rekognition.model.PersonMatch;
import com.amazonaws.services.rekognition.model.PersonTrackingSortBy;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.StartCelebrityRecognitionRequest;
import com.amazonaws.services.rekognition.model.StartCelebrityRecognitionResult;
import com.amazonaws.services.rekognition.model.StartContentModerationRequest;
import com.amazonaws.services.rekognition.model.StartContentModerationResult;
import com.amazonaws.services.rekognition.model.StartFaceDetectionRequest;
import com.amazonaws.services.rekognition.model.StartFaceDetectionResult;
import com.amazonaws.services.rekognition.model.StartFaceSearchRequest;
import com.amazonaws.services.rekognition.model.StartFaceSearchResult;
import com.amazonaws.services.rekognition.model.StartLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.StartLabelDetectionResult;
import com.amazonaws.services.rekognition.model.StartPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.StartPersonTrackingResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.rekognition.model.VideoMetadata;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
@Service
public class VideoService {
	
	@Autowired
	private AWSRekognitionService aws;
	@Autowired
	private FaceService face;
	
 	private static final String queueUrl 		= "https://sqs.us-east-1.amazonaws.com/040989129850/my360";
    private static final String ROLE_ARN		= "arn:aws:iam::040989129850:role/my360_video";
    private static final String SNS_TOPIC_ARN 	= "arn:aws:sns:us-east-1:040989129850:my360crmface";
    
////============================================================================================================================////    
    
    
	String streamProcessorName="my360VideoStreamProcessor";
    String kinesisVideoStreamArn="arn:aws:kinesisvideo:us-east-1:040989129850:stream/my360crm-stream/1558009003962"; 
	String kinesisDataStreamArn="arn:aws:kinesis:us-east-1:040989129850:stream/my360crm-videoStream";
	String roleArn="arn:aws:iam::040989129850:role/my360_video";
	String collectionId="cafe17_comxCafe";
	Float matchThreshold=50f;
public void test() {
	
	try {
		StreamManager sm= new StreamManager(streamProcessorName,
				kinesisVideoStreamArn,
				kinesisDataStreamArn,
				roleArn,
				collectionId,
				matchThreshold);
		//sm.createStreamProcessor();
		sm.startStreamProcessor();
		//sm.deleteStreamProcessor();
		//sm.deleteStreamProcessor();
		//sm.stopStreamProcessor();
		//sm.listStreamProcessors();
		//sm.describeStreamProcessor();
	}
	catch(Exception e){
		System.out.println(e.getMessage());
	}
}
////============================================================================================================================////    
    
    private static NotificationChannel channel = new NotificationChannel()
										            .withSNSTopicArn(SNS_TOPIC_ARN)
										            .withRoleArn(ROLE_ARN);
										  
    private static String startJobId;
    
	public String videoName ="";
	public static String bucketName="";
	public void bucketName(String Bucket) {
		if(Bucket==null) {
			bucketName=face.getBucket();
		}	else bucketName=Bucket;
	}
	
//	method for uploading file to s3bucket    
	public String uploadFileTos3bucket(String fileName, File file) {
		try {
			aws.getS3Client()
						.putObject(new PutObjectRequest(bucketName, videoName, file).withCannedAcl(CannedAccessControlList.PublicRead));
		}catch(Exception e) {
			throw new AWSRekognitionException(e.toString());
		}
		// Set the presigned URL to expire after one hour.
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);

		String URL = aws.getS3Client().generatePresignedUrl(bucketName, fileName, expiration).toString();
		// aws.getS3Client().generatePresignedUrl(bucket, fileName, null);
		return URL;
	}
	
	// video detection
		public ArrayList<?> videoRekognition(MultipartFile videoFile,String method,int limit,String Bucket) {
			ArrayList<?> response = null;
			String decision=method;
			bucketName(Bucket);
			try {
	         	File file = face.convert_MultiPartToFile(videoFile);
	         	String externalImageName=face.generateFileName(videoFile);
	         	videoName=face.getServerName()+face.getBusinessName()+externalImageName;
	            uploadFileTos3bucket(videoName, file);
	            file.delete();
	            System.out.println(videoName);
	            }
	    	 catch(Exception e) {
	    		 e.printStackTrace();
	    	 }
				
				if(decision.equals("detectFace")) {
					StartFaces(bucketName, videoName);
				}
				else if(decision.equals("detectObject")) {
					StartLabels(bucketName,videoName);
				}
				else if(decision.equals("searchFace")) {
					StartFaceSearchCollection(bucketName,videoName);
				}
				else if(decision.equals("trackPerson")) {
					StartPersons(bucketName,videoName);
				}
				else if(decision.equals("detectCelebrity")) {
					StartCelebrities(bucketName,videoName);
				}
				else if(decision.equals("detectUnsafeContent")) {
					StartModerationLabels(bucketName,videoName);
				}
			
		      System.out.println("Waiting for job: " + startJobId);
		      
		      List<Message> messages=null;
		      int dotLine=0;
		      boolean jobFound=false;
		      
		      //loop until the job status is published. Ignore other messages in queue.
		        do{
		            messages = aws.getSQSclient().receiveMessage(queueUrl).getMessages();
		            if (dotLine++<20){
		                System.out.print(".");
		            }else{
		                System.out.println();
		                dotLine=0;
		            }

		            if (!messages.isEmpty()) {
		                //Loop through messages received.
		                for (Message message: messages) {
		                    String notification = message.getBody();

		                    // Get status and job id from notification.
		                    ObjectMapper mapper = new ObjectMapper();
		                    
		                    JsonNode jsonMessageTree = null;
							try {
								jsonMessageTree = mapper.readTree(notification);
							} catch (IOException e) {
								throw new AWSRekognitionException(e.toString());
							}
		                    JsonNode messageBodyText = jsonMessageTree.get("Message");
		                    ObjectMapper operationResultMapper = new ObjectMapper();
		                    
		                    
		                    JsonNode jsonResultTree;
							try {
								jsonResultTree = operationResultMapper.readTree(messageBodyText.textValue());
							} catch (IOException e) {
								throw new AWSRekognitionException(e.toString());
							}
							
		                    JsonNode operationJobId = jsonResultTree.get("JobId");
		                    JsonNode operationStatus = jsonResultTree.get("Status");
		                    System.out.println("Job found was " + operationJobId);
		                    // Found job. Get the results and display.
		                    if(operationJobId.asText().equals(startJobId)){
		                        jobFound=true;
		                        System.out.println("Job id: " + operationJobId );
		                        System.out.println("Status : " + operationStatus.toString());
		                        if (operationStatus.asText().equals("SUCCEEDED")){
		                            //============================================
		                        	if(decision.equals("detectFace")) {
		                        		response = GetResultsFaces(limit);
		                        	}
		                        	else if(decision.equals("detectObject")) {
		                        		response = GetResultsLabels(limit);
		                        	}
		                        	else if(decision.equals("searchFace")) {
		                        		response = GetResultsFaceSearchCollection(limit);
		                			}
		                			else if(decision.equals("trackPerson")) {
		                				response =GetResultsPersons(limit);
		                			}
		                			else if(decision.equals("detectCelebrity")) {
		                				response =GetResultsCelebrities(limit);
		                			}
		                			else if(decision.equals("detectUnsafeContent")) {
		                				response =GetResultsModerationLabels(limit);
		                			}
		                            //============================================
		                        }
		                        else{
		                            System.out.println("Video analysis failed");
		                        }
		                        
		                        aws.getSQSclient().deleteMessage(queueUrl,message.getReceiptHandle());
		                       
		                    }
		                    
		                    else{
		                        System.out.println("Job received was not job " +  startJobId);
		                        //Delete unknown message. Consider moving message to dead letter queue
		                        aws.getSQSclient().deleteMessage(queueUrl,message.getReceiptHandle());
		                    }
		                }
		            }
		        } while (!jobFound);


		        System.out.println("Done!");
				return response;
		
	   }
		
		
		 private void StartFaces(String bucket, String photo){
			
			try {
		       StartFaceDetectionRequest req = new StartFaceDetectionRequest()
		               .withVideo(new Video()
		                       .withS3Object(new S3Object()
		                           .withBucket(bucket)
		                           .withName(photo)))
		               .withNotificationChannel(channel);
		       
		       StartFaceDetectionResult startLabelDetectionResult = aws.getRekognitionClient().startFaceDetection(req);
		       startJobId=startLabelDetectionResult.getJobId();
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Face Detection unsuccesssful..!"+e);
	    	}     
		} 
		 
		 
	    private void StartLabels(String bucket, String video){
	    	try {
		        StartLabelDetectionRequest req = new StartLabelDetectionRequest()
		                .withVideo(new Video()
		                        .withS3Object(new S3Object()
		                                .withBucket(bucket)
		                                .withName(video)))
		                .withMinConfidence(50F)
		                .withJobTag("DetectingLabels")
		                .withNotificationChannel(channel);
	
		        StartLabelDetectionResult startLabelDetectionResult = aws.getRekognitionClient().startLabelDetection(req);
		        startJobId=startLabelDetectionResult.getJobId();
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Label Detection unsuccesssful..!"+e);
	    	}  
	    }
	    
	    
	    private  void StartFaceSearchCollection(String bucket, String video){

	    	try { 
		        StartFaceSearchRequest req = new StartFaceSearchRequest()
		                .withCollectionId(face.getCollectionId())
		                .withVideo(new Video()
		                        .withS3Object(new S3Object()
		                                .withBucket(bucket)
		                                .withName(video)))
		                .withNotificationChannel(channel);
	
		        StartFaceSearchResult startPersonCollectionSearchResult = aws.getRekognitionClient().startFaceSearch(req);
		        startJobId=startPersonCollectionSearchResult.getJobId();
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Face Search unsuccesssful..!"+e);
	    	}  
	    } 
	    
	    private  void StartPersons(String bucket, String video){

		     try {   
			     StartPersonTrackingRequest req = new StartPersonTrackingRequest()
			             .withVideo(new Video()
			                     .withS3Object(new S3Object()
			                         .withBucket(bucket)
			                         .withName(video)))
			             .withNotificationChannel(channel);
			  
			     StartPersonTrackingResult startPersonDetectionResult = aws.getRekognitionClient().startPersonTracking(req);
			     startJobId=startPersonDetectionResult.getJobId();
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Person Tracking unsuccesssful..!"+e);
	    	}   
	   } 
	    
	    private  void StartCelebrities(String bucket, String video) {
	    	try {
		        StartCelebrityRecognitionRequest req = new StartCelebrityRecognitionRequest()
		              .withVideo(new Video()
		                    .withS3Object(new S3Object()
		                          .withBucket(bucket)
		                          .withName(video)))
		              .withNotificationChannel(channel);
	
		        StartCelebrityRecognitionResult startCelebrityRecognitionResult = aws.getRekognitionClient().startCelebrityRecognition(req);
			        startJobId=startCelebrityRecognitionResult.getJobId();
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Detecting Celebrities unsuccesssful..!"+e);
	    	}
	     }
	     private  void StartModerationLabels(String bucket, String video){
	        
	    	try { 
		        StartContentModerationRequest req = new StartContentModerationRequest()
		                .withVideo(new Video()
		                        .withS3Object(new S3Object()
		                            .withBucket(bucket)
		                            .withName(video)))
		                .withNotificationChannel(channel);
		                             
		         StartContentModerationResult startModerationLabelDetectionResult = aws.getRekognitionClient().startContentModeration(req);
		         startJobId=startModerationLabelDetectionResult.getJobId();
	    	}
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Detecting unsafe content unsuccesssful..!"+e);
	    	}
	     } 
	     
	     private ArrayList<?> GetResultsFaces(int limit){
			 List<FaceDetection> faces;
		       int maxResults=limit;
		       String paginationToken=null;
		       GetFaceDetectionResult faceDetectionResult=null;
		      try { 
			       do{
			           if (faceDetectionResult !=null){
			               paginationToken = faceDetectionResult.getNextToken();
			           }
			       
			           faceDetectionResult = aws.getRekognitionClient().getFaceDetection(new GetFaceDetectionRequest()
			                .withJobId(startJobId)
			                .withNextToken(paginationToken)
			                .withMaxResults(maxResults));
			       
			           VideoMetadata videoMetaData=faceDetectionResult.getVideoMetadata();
			               
			           System.out.println("Format: " 	+ videoMetaData.getFormat());
			           System.out.println("Codec: " 	+ videoMetaData.getCodec());
			           System.out.println("Duration: " 	+ videoMetaData.getDurationMillis());
			           System.out.println("FrameRate: " + videoMetaData.getFrameRate());
			               
			               
			           //Show faces, confidence and detection times
			           faces= faceDetectionResult.getFaces();
			        
			           for (FaceDetection face: faces) { 
			               long seconds=face.getTimestamp()/1000;
			               System.out.print("Sec: " + Long.toString(seconds) + " ");
			               System.out.println(face.getFace().toString());
			               System.out.println();           
			           }
			       } while (faceDetectionResult !=null && faceDetectionResult.getNextToken() != null);
			         
			           return (ArrayList<?>) faces;
		      }
		      catch(Exception e) {
		    	  throw new AWSRekognitionException("Error:Face Detection unsuccesssful..!"+e);
	    	}   
		      
		 }
	     
	     
	     private ArrayList<?> GetResultsLabels(int limit){
	    	List<LabelDetection>  detectedLabels=null;
	        int maxResults=limit;
	        String paginationToken=null;
	        GetLabelDetectionResult labelDetectionResult=null;
	        try {
		        do {
		            if (labelDetectionResult !=null){
		                paginationToken = labelDetectionResult.getNextToken();
		            }
	
		            GetLabelDetectionRequest labelDetectionRequest= new GetLabelDetectionRequest()
		                    .withJobId(startJobId)
		                    .withSortBy(LabelDetectionSortBy.TIMESTAMP)
		                    .withMaxResults(maxResults)
		                    .withNextToken(paginationToken);
	
		            labelDetectionResult = aws.getRekognitionClient().getLabelDetection(labelDetectionRequest);
	
		            VideoMetadata videoMetaData=labelDetectionResult.getVideoMetadata();
	
		            System.out.println("Format: " + videoMetaData.getFormat());
		            System.out.println("Codec: " + videoMetaData.getCodec());
		            System.out.println("Duration: " + videoMetaData.getDurationMillis());
		            System.out.println("FrameRate: " + videoMetaData.getFrameRate());
	
	
		            //Show labels, confidence and detection times
		            detectedLabels= labelDetectionResult.getLabels();
		            
		            for (LabelDetection detectedLabel: detectedLabels) {
		                long seconds=detectedLabel.getTimestamp();
		                Label label=detectedLabel.getLabel();
		                System.out.println("Millisecond: " + Long.toString(seconds) + " ");
		                
		                System.out.println("   Label:" + label.getName()); 
		                System.out.println("   Confidence:" + detectedLabel.getLabel().getConfidence().toString());
		      
		                List<com.amazonaws.services.rekognition.model.Instance> instances = label.getInstances();
		                System.out.println("   Instances of " + label.getName());
		                if (instances.isEmpty()) {
		                    System.out.println("        " + "None");
		                } else {
		                    for (com.amazonaws.services.rekognition.model.Instance instance : instances) {
		                        System.out.println("        Confidence: " + instance.getConfidence().toString());
		                        System.out.println("        Bounding box: " + instance.getBoundingBox().toString());
		                    }
		                }
		                System.out.println("   Parent labels for " + label.getName() + ":");
		                List<Parent> parents = label.getParents();
		                if (parents.isEmpty()) {
		                    System.out.println("        None");
		                } else {
		                    for (Parent parent : parents) {
		                        System.out.println("        " + parent.getName());
		                    }
		                }
		                System.out.println();
		            }
		        } while (labelDetectionResult !=null && labelDetectionResult.getNextToken() != null);
		        return (ArrayList<?>) detectedLabels;
	    	}
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Label Detection unsuccesssful..!"+e);
	    	}  
	    }    
	    
	     private ArrayList<?> GetResultsFaceSearchCollection(int limit){
	    	 List<PersonMatch> matches=null;
	         GetFaceSearchResult faceSearchResult=null;
	         int maxResults=limit;
	         String paginationToken=null;
	         try {
		         do {
	
		             if (faceSearchResult !=null){
		                 paginationToken = faceSearchResult.getNextToken();
		             }
	
		             faceSearchResult  = aws.getRekognitionClient().getFaceSearch(
		                     new GetFaceSearchRequest()
		                     .withJobId(startJobId)
		                     .withMaxResults(maxResults)
		                     .withNextToken(paginationToken)
		                     .withSortBy(FaceSearchSortBy.TIMESTAMP)
		                     );
	
	
		             VideoMetadata videoMetaData=faceSearchResult.getVideoMetadata();
	
		             System.out.println("Format: " + videoMetaData.getFormat());
		             System.out.println("Codec: " + videoMetaData.getCodec());
		             System.out.println("Duration: " + videoMetaData.getDurationMillis());
		             System.out.println("FrameRate: " + videoMetaData.getFrameRate());
		             System.out.println();      
	
		             //Show search results
		             matches=faceSearchResult.getPersons();
	
		             for (PersonMatch match: matches) { 
		                 long milliSeconds=match.getTimestamp();
		                 System.out.print("Timestamp: " + Long.toString(milliSeconds));
		                 System.out.println(" Person number: " + match.getPerson().getIndex());
		                 List <FaceMatch> faceMatches = match.getFaceMatches();
		                 if (faceMatches != null) {
		                     System.out.println("Matches in collection...");
		                     for (FaceMatch faceMatch: faceMatches){
		                         Face face=faceMatch.getFace();
		                         System.out.println("Face Id: "+ face.getFaceId());
		                         System.out.println("Similarity: " + faceMatch.getSimilarity().toString());
		                         System.out.println();
		                     }
		                 }
		                 System.out.println();           
		             } 
		             System.out.println(); 
	
		         } while (faceSearchResult !=null && faceSearchResult.getNextToken() != null);
		         return (ArrayList<?>) matches;
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Face Search unsuccesssful..!"+e);
	    	} 
	     } 
	     
	     private  ArrayList<?> GetResultsPersons(int limit){
	    	 List<PersonDetection> detectedPersons=null;
	    	 int maxResults=limit;
	         String paginationToken=null;
	         GetPersonTrackingResult personTrackingResult=null;
	         try {
		         do{
		             if (personTrackingResult !=null){
		                 paginationToken = personTrackingResult.getNextToken();
		             }
		             
		             personTrackingResult = aws.getRekognitionClient().getPersonTracking(new GetPersonTrackingRequest()
		                  .withJobId(startJobId)
		                  .withNextToken(paginationToken)
		                  .withSortBy(PersonTrackingSortBy.TIMESTAMP)
		                  .withMaxResults(maxResults));
		       
		             VideoMetadata videoMetaData=personTrackingResult.getVideoMetadata();
		                 
		             System.out.println("Format: " + videoMetaData.getFormat());
		             System.out.println("Codec: " + videoMetaData.getCodec());
		             System.out.println("Duration: " + videoMetaData.getDurationMillis());
		             System.out.println("FrameRate: " + videoMetaData.getFrameRate());
		                 
		                 
		             //Show persons, confidence and detection times
		             detectedPersons= personTrackingResult.getPersons();
		          
		             for (PersonDetection detectedPerson: detectedPersons) { 
		                 
		                long seconds=detectedPerson.getTimestamp()/1000;
		                System.out.print("Sec: " + Long.toString(seconds) + " ");
		                System.out.println("Person Identifier: "  + detectedPerson.getPerson().getIndex());
		                   System.out.println();             
		             }
		         }  while (personTrackingResult !=null && personTrackingResult.getNextToken() != null);
		         return (ArrayList<?>) detectedPersons;
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Person Tracking unsuccesssful..!"+e);
	    	}   
	     } 
	     
	     private ArrayList<?> GetResultsCelebrities(int limit){
	    	 List<CelebrityRecognition> celebs=null;
	         int maxResults=limit;
	         String paginationToken=null;
	         GetCelebrityRecognitionResult celebrityRecognitionResult=null;
	         try {
		         do{
		            if (celebrityRecognitionResult !=null){
		               paginationToken = celebrityRecognitionResult.getNextToken();
		            }
		            celebrityRecognitionResult = aws.getRekognitionClient().getCelebrityRecognition(new GetCelebrityRecognitionRequest()
		                  .withJobId(startJobId)
		                  .withNextToken(paginationToken)
		                  .withSortBy(CelebrityRecognitionSortBy.TIMESTAMP)
		                  .withMaxResults(maxResults));
	
	
		            System.out.println("File info for page");
		            VideoMetadata videoMetaData=celebrityRecognitionResult.getVideoMetadata();
	
		            System.out.println("Format: " + videoMetaData.getFormat());
		            System.out.println("Codec: " + videoMetaData.getCodec());
		            System.out.println("Duration: " + videoMetaData.getDurationMillis());
		            System.out.println("FrameRate: " + videoMetaData.getFrameRate());
	
		            System.out.println("Job");
	
		            System.out.println("Job status: " + celebrityRecognitionResult.getJobStatus());
	
	
		            //Show celebrities
		            celebs= celebrityRecognitionResult.getCelebrities();
	
		            for (CelebrityRecognition celeb: celebs) { 
		               long seconds=celeb.getTimestamp()/1000;
		               System.out.print("Sec: " + Long.toString(seconds) + " ");
		               CelebrityDetail details=celeb.getCelebrity();
		               System.out.println("Name: " + details.getName());
		               System.out.println("Id: " + details.getId());
		               System.out.println(); 
		            }
		         } while (celebrityRecognitionResult !=null && celebrityRecognitionResult.getNextToken() != null);
		         return (ArrayList<?>) celebs;
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Detecting Celebrities unsuccesssful..!"+e);
	    	}
	      } 
	     
	     private ArrayList<?> GetResultsModerationLabels(int limit){
	    	 List<ContentModerationDetection> moderationLabelsInFrames=null;
	         int maxResults=limit;
	         String paginationToken=null;
	         GetContentModerationResult moderationLabelDetectionResult =null;
	         try {
		         do{
		             if (moderationLabelDetectionResult !=null){
		                 paginationToken = moderationLabelDetectionResult.getNextToken();
		             }
		             
		             moderationLabelDetectionResult = aws.getRekognitionClient().getContentModeration(
		                     new GetContentModerationRequest()
		                         .withJobId(startJobId)
		                         .withNextToken(paginationToken)
		                         .withSortBy(ContentModerationSortBy.TIMESTAMP)
		                         .withMaxResults(maxResults));
		                     
		             
		    
		             VideoMetadata videoMetaData=moderationLabelDetectionResult.getVideoMetadata();
		                 
		             System.out.println("Format: " + videoMetaData.getFormat());
		             System.out.println("Codec: " + videoMetaData.getCodec());
		             System.out.println("Duration: " + videoMetaData.getDurationMillis());
		             System.out.println("FrameRate: " + videoMetaData.getFrameRate());
		                 
		                 
		             //Show moderated content labels, confidence and detection times
		            moderationLabelsInFrames=moderationLabelDetectionResult.getModerationLabels();
		          
		             for (ContentModerationDetection label: moderationLabelsInFrames) { 
		                 long seconds=label.getTimestamp()/1000;
		                 System.out.print("Sec: " + Long.toString(seconds));
		                 System.out.println(label.getModerationLabel().toString());
		                 System.out.println();           
		             }  
		         } while (moderationLabelDetectionResult !=null && moderationLabelDetectionResult.getNextToken() != null);
		         return (ArrayList<?>) moderationLabelsInFrames;
		    }
	    	catch(Exception e) {
	    		throw new AWSRekognitionException("Error:Detecting unsafe content unsuccesssful..!"+e);
	    	}
	     } 
	
	
  }