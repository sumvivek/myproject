package com.my360face.AWSRekognition.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Celebrity;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.DeleteFacesRequest;
import com.amazonaws.services.rekognition.model.DeleteFacesResult;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.amazonaws.services.rekognition.model.ModerationLabel;
import com.amazonaws.services.rekognition.model.QualityFilter;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesRequest;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.services.rekognition.model.UnindexedFace;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my360face.AWSRekognition.exception.AWSRekognitionException;
import com.my360face.AWSRekognition.payloads.IndexResponse;
import com.my360face.AWSRekognition.payloads.SearchResponse;
import com.my360face.AWSRekognition.property.ImageFileStorageProperties;
import com.my360face.image.exception.ImageStorageException;

@Service
public class FaceService  {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Autowired
	private AWSRekognitionService aws;
	@Autowired
	public DrawBoundingBoxService box;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FaceService.class);
	private static String collectionId = "cafe17_comxCafe";
	private static String bucket = "my360crm.comx.com";
	private static String photo = "";
	private static String serverName = "kindy001/";
	private static String businessName = "RotiGhar/";
	private Path imageFileStorageLocation;

	public String getCollectionId() {
		return collectionId;
	}
	public String getBucket() {
		return bucket;
	}
	public String getServerName() {
		return serverName;
	}
	public String getBusinessName() {
		return businessName;
	}
	
	public void setDetails(String bucketName,String galleryName) {
		if(bucketName==null) {
			System.out.println("BucketName:"+bucket);
		}
		else FaceService.bucket=bucketName; 
		if(galleryName==null) {
			System.out.println("CollectionName:"+collectionId);
		}
		else FaceService.collectionId=galleryName;
	}
	
	public FaceService(ImageFileStorageProperties imageFileStorageProperties) {

		this.imageFileStorageLocation = Paths.get(imageFileStorageProperties.getUploadDir()).toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.imageFileStorageLocation);
		} catch (Exception ex) {
			throw new AWSRekognitionException("Could not create the directory where the uploaded files will be stored.",ex);
		}
	}

//filtering actions based on no of faces	    
	public Object indexValidate(MultipartFile imageFile, Boolean multipleFace,Boolean searchFaces,String bucketName,String galleryName) {
		Boolean allowMultipleFaces = multipleFace;
		Boolean searchFace=searchFaces;

		int faceCount = detectFaces(imageFile).size();

		if (faceCount < 1) {
			throw new AWSRekognitionException("The image is invalid.Please upload image with atleast one face");
		} 
		else if (faceCount == 1) {
				
				if(searchFace==true) {
					return searchFaces(imageFile,bucketName,galleryName);
				}
				else return indexFace(imageFile,bucketName,galleryName);
		}
			
		else if (faceCount > 1 && allowMultipleFaces == true) {
				if(searchFace==true) {
					return searchFaces(imageFile,bucketName,galleryName);
				}
				else return indexFace(imageFile,bucketName,galleryName);
		}
		else {
			throw new AWSRekognitionException("Please upload image with single face - Valid FaceImage required");
		}

	}

// Indexing faces  
	public IndexResponse indexFace(MultipartFile imageFile,String bucketName,String galleryName) throws NullPointerException {
		
		setDetails(bucketName, galleryName);
		
		String externalImageName = null;
		List<String> faceIds = new ArrayList<String>();

		try {
			File file = convert_MultiPartToFile(imageFile);
			externalImageName = generateFileName(imageFile);
			photo = serverName + businessName + externalImageName;
			uploadFileTos3bucket(photo, file);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Normalize file name
		String imageName = StringUtils.cleanPath(imageFile.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (imageName.contains("..")) {
				throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
			}

////		  upload file to local directory   
//            Path targetLocation = this.imageFileStorageLocation.resolve(imageName);
//            Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//            photo = generateFileName(imageFile);
//            File file =new File("uploads/"+imageName);
//            uploadFileTos3bucket(photo, file);     

			Image image = new Image().withS3Object(new S3Object().withBucket(bucket) // make it dynamic
					.withName(photo));

			IndexFacesRequest indexFacesRequest = new IndexFacesRequest().withImage(image)
					.withQualityFilter(QualityFilter.AUTO).withMaxFaces(10).withCollectionId(collectionId) // make it
																											// dynamic
					.withExternalImageId(externalImageName).withDetectionAttributes("DEFAULT");

			IndexFacesResult indexFacesResult = aws.getRekognitionClient().indexFaces(indexFacesRequest);
			System.out.println("Faces indexed:");
			List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
			for (FaceRecord faceRecord : faceRecords) {

				String id = faceRecord.getFace().getFaceId();
				faceIds.add(id);
				System.out.println("  Face ID: " + faceRecord.getFace().getFaceId()
						+ (" \n Face Details:" + faceRecord.getFaceDetail().getBoundingBox().toString()));
			}

			List<UnindexedFace> unindexedFaces = indexFacesResult.getUnindexedFaces();
			System.out.println("=============================================================");
			System.out.println("Faces not indexed:");
			for (UnindexedFace unindexedFace : unindexedFaces) {
				System.out.println("  Location:" + unindexedFace.getFaceDetail().getBoundingBox().toString());
				System.out.println("  Reasons:");
				for (String reason : unindexedFace.getReasons()) {
					System.out.println("   " + reason);
				}
			}

//            if(file.delete()) {
//            System.out.println("file deleted successfully..!");
//            }

			String E_URl = "https://s3.amazonaws.com/" + bucket + "/" + photo;
			return new IndexResponse(E_URl, faceIds);
		} catch (Exception ex) {
			throw new AWSRekognitionException("Error: Index face failed(" + imageName + ")...Please try again!"+ex, ex);
		}
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

//	method for uploading file to s3bucket    
	public String uploadFileTos3bucket(String fileName, File file) {
		try {
		aws.getS3Client()
				.putObject(new PutObjectRequest(bucket, photo, file).withCannedAcl(CannedAccessControlList.PublicRead));
		}catch(Exception e) {
			throw new AWSRekognitionException(e.toString());
		}
		// Set the presigned URL to expire after one hour.
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);

		String URL = aws.getS3Client().generatePresignedUrl(bucket, fileName, expiration).toString();
		// aws.getS3Client().generatePresignedUrl(bucket, fileName, null);
		return URL;
	}

//	method for converting multipart into File    
	public File convert_MultiPartToFile(MultipartFile File) throws IOException {
		File convFile = new File(File.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(File.getBytes());
		fos.close();
		return convFile;
	}

//	method for generating distinct name to avoid validation error    
	public String generateFileName(MultipartFile multiPart) {
		System.out.println("=============================================================");
		System.out.println(new Date().getTime() + "-" + multiPart.getOriginalFilename().replaceAll("[^.,a-zA-Z0-9]", "_"));
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replaceAll("[^.,a-zA-Z0-9]", "_");

	}

	// searching the image uploaded whether it matches with any indexed faces
	public SearchResponse searchFaces(MultipartFile imageFile,String bucketName,String galleryName) {
		String imageURL = null;
		setDetails(bucketName, galleryName);
		List<String> faceIds = new ArrayList<String>();
		try {

			File file = convert_MultiPartToFile(imageFile);
			byte[] bytearr = Files.readAllBytes(file.toPath());
			ByteBuffer bf = ByteBuffer.wrap(bytearr);

			SearchFacesByImageRequest imageRequest = new SearchFacesByImageRequest().withCollectionId(collectionId)
					.withImage(new Image().withBytes(bf));

			SearchFacesByImageResult imageResult = aws.getRekognitionClient().searchFacesByImage(imageRequest);
			
			List<FaceMatch> facematches = imageResult.getFaceMatches();
			for (FaceMatch facematch : facematches) {
				System.out.println("Face matches: " + facematch.getFace());
//				imageURL = aws.getS3Client().generatePresignedUrl(bucket,
//						serverName + businessName + facematch.getFace().getExternalImageId(), null).toString();
				imageURL="https://s3.amazonaws.com/" + bucket + "/" +serverName + businessName + facematch.getFace().getExternalImageId() ;
				faceIds.add(facematch.getFace().getFaceId());
			}
			file.delete();
			
//			try {
//				box.searchFaceBoundingBox(imageResult);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			return new SearchResponse(imageURL, faceIds);

		} catch (IOException e) {
			throw new AWSRekognitionException("Error:Recognizing face failed.. Please try again!."+e, e);
		}
	}

//detecting the faces in the image uploaded
	public List<FaceDetail> detectFaces(MultipartFile imageFile) {
		ByteBuffer bf=null;
		File file=null;
		// Normalize file name
		String imageName = StringUtils.cleanPath(imageFile.getOriginalFilename());
		try {
			file = convert_MultiPartToFile(imageFile);
			byte[] bytearr = Files.readAllBytes(file.toPath());
			bf = ByteBuffer.wrap(bytearr);
			}
			catch (Exception e) {
				throw new AWSRekognitionException("Could not find File"+imageName+".. Please try again!", e);
			}
		try {
			
			// Check if the file's name contains invalid characters
			if (imageName.contains("..")) {
					throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
			}
			DetectFacesRequest detectFacesRequest = new DetectFacesRequest().withImage(new Image().withBytes(bf))
					.withAttributes("ALL");

			DetectFacesResult detectFacesResult = aws.getRekognitionClient().detectFaces(detectFacesRequest);
			List<FaceDetail> faceDetails = detectFacesResult.getFaceDetails();
			for (FaceDetail faceDetail : faceDetails) {
				System.out.println("  Face Details: " + faceDetail + faceDetail.getEmotions());
				System.out.println("==================================================================");
			}
			file.delete();
			return faceDetails;
		} catch (Exception e) {
			throw new AWSRekognitionException("Error:Detect faces failed..!Please try again."+e);
		}

	}

// detecting no of faces indexed in each collection
	public List<String> getFaceCount(int limit) {
		try {
			System.out.println("Listing collections");
			List<String> faceCountList = new ArrayList<String>();
			ListCollectionsResult listCollectionsResult = null;
			String paginationToken = null;
			do {
				if (listCollectionsResult != null) {
					paginationToken = listCollectionsResult.getNextToken();
				}
				ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest().withMaxResults(limit)
						.withNextToken(paginationToken);
				listCollectionsResult = aws.getRekognitionClient().listCollections(listCollectionsRequest);
				List<String> collectionIds = listCollectionsResult.getCollectionIds();
				System.out.println(collectionIds.size());
				for (String resultId : collectionIds) {
					DescribeCollectionRequest describeCollectionRequest = new DescribeCollectionRequest()
							.withCollectionId(resultId);
					DescribeCollectionResult describeCollectionResult = aws.getRekognitionClient()
							.describeCollection(describeCollectionRequest);
					faceCountList.add(resultId + ":" + describeCollectionResult.getFaceCount().toString());

				}
				System.out.println(faceCountList);
				return faceCountList;
			} while (listCollectionsResult != null && listCollectionsResult.getNextToken() != null);

		} catch (Exception e) {
			throw new AWSRekognitionException("Error: list collection unsuccessful."+e, e);
		}
	}

	// detecting no of faces indexed in particular collection
	public String getFaceCount(String collectionName) {
		try {
		DescribeCollectionRequest describeCollectionRequest = new DescribeCollectionRequest()
				.withCollectionId(collectionName);
		DescribeCollectionResult describeCollectionResult = aws.getRekognitionClient()
				.describeCollection(describeCollectionRequest);
		String faceCount = (collectionName + ":" + describeCollectionResult.getFaceCount().toString());
		return faceCount;
		}catch(Exception e) {
			throw new AWSRekognitionException("Error: Get face count unsuccessful "+e, e);
		}
	}

	// compare faces dynamically
	public List<CompareFacesMatch> compareFaces(MultipartFile source, MultipartFile target)  {
		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;
		File sourceFile;
		try  {
			sourceFile = convert_MultiPartToFile(source);
		} catch (IOException e1) {
			throw new AWSRekognitionException("Could not find File"+source.getOriginalFilename()+".. Please try again!", e1);
		}
		File targetFile;
		try {
			targetFile = convert_MultiPartToFile(target);
		} catch (IOException e1) {
			throw new AWSRekognitionException("Could not find File"+target.getOriginalFilename()+".. Please try again!", e1);
		}
		// Load source and target images and create input parameters
		
		try (InputStream inputStream = new FileInputStream(sourceFile)) {
			sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			throw new AWSRekognitionException("Failed to load source image " + source.getOriginalFilename() , e);
		}
		try (InputStream inputStream = new FileInputStream(targetFile)) {
			targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			throw new AWSRekognitionException("Failed to load source image " +  target.getOriginalFilename() , e);
		}

		try {
			Image sourceImage = new Image().withBytes(sourceImageBytes);
			Image targetImage = new Image().withBytes(targetImageBytes);
			CompareFacesRequest compareFacesRequest = new CompareFacesRequest().withSourceImage(sourceImage)
					.withTargetImage(targetImage).withSimilarityThreshold(60f);
			CompareFacesResult compareFacesResult = aws.getRekognitionClient().compareFaces(compareFacesRequest);
			System.out.println(compareFacesResult);
	
			// Display results
			List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
				for (CompareFacesMatch match : faceDetails) {
						ComparedFace face = match.getFace();
						BoundingBox position = face.getBoundingBox();
						System.out.println("Face at " + position.getLeft().toString() + " " + position.getTop().toString()
								+ " matches with " + match.getSimilarity().toString() + "% confidence.");
		
				}
			List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
			System.out.println("There was " + uncompared.size() + " face(s) that did not match");
//			try {
//				box.compareFaceBoundingBox(compareFacesResult, sourceImageBytes, targetImageBytes);
//			}
//			catch(Exception e) {
//				
//			}
			
			sourceFile.delete();
			targetFile.delete();
			return faceDetails;
		}
		
		catch (Exception e) {
			throw new AWSRekognitionException("Error:comparing faces failed..Please try again!"+e, e);
		}
	}

	// Listing Faces in a Collection
	public List<Face> listFaces(String collectionName, int limit)  {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Face> faces = null;
		ListFacesResult listFacesResult = null;
		System.out.println("Faces in collection " + collectionName);
		try {
			String paginationToken = null;
			do {
				if (listFacesResult != null) {
					paginationToken = listFacesResult.getNextToken();
				}
	
				ListFacesRequest listFacesRequest = new ListFacesRequest().withCollectionId(collectionName)
						.withMaxResults(limit).withNextToken(paginationToken);
	
				listFacesResult = aws.getRekognitionClient().listFaces(listFacesRequest);
				faces = listFacesResult.getFaces();
				for (Face face : faces) {
					System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
				}
			} while (listFacesResult != null && listFacesResult.getNextToken() != null);
	
			return faces;
		}catch(Exception e) {
			throw new AWSRekognitionException("Error: Listing faces unsuccessful."+e, e);
		}
	}

	// delete face using faceID
	public void deleteFaceFromCollection(String faceId,String collectionId) {
		if(collectionId!=null || !collectionId.isEmpty()) {
			FaceService.collectionId=collectionId;
		}
		try {
			DeleteFacesRequest deleteFacesRequest = new DeleteFacesRequest().withCollectionId(FaceService.collectionId)
					.withFaceIds(faceId);
			DeleteFacesResult deleteFacesResult = aws.getRekognitionClient().deleteFaces(deleteFacesRequest);

         List < String > faceRecords = deleteFacesResult.getDeletedFaces();
         System.out.println(Integer.toString(faceRecords.size()) + " face(s) deleted:");
         for (String face: faceRecords) {
            System.out.println("FaceID: " + face);
         }
//         return deleteFacesResult.toString();
		} catch (Exception e) {
			throw new AWSRekognitionException("Error:Deleting FaceId unsuccesful..!"+e, e);
		}

	}

	// celebrity recognizing
	public List<Celebrity> recognizeCelebrities(MultipartFile image) {

		ByteBuffer imageBytes = null;

		File imageFile = null;
		try {
			imageFile = convert_MultiPartToFile(image);
		} catch (IOException e1) {
			throw new AWSRekognitionException("Failed to load  image " + image.getOriginalFilename() , e1);

		}

		try (InputStream inputStream = new FileInputStream(imageFile)) {
			imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			throw new AWSRekognitionException("Failed to load  image " + image.getOriginalFilename() , e);
		}
		try {
			RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest()
					.withImage(new Image().withBytes(imageBytes));
			RecognizeCelebritiesResult result = aws.getRekognitionClient().recognizeCelebrities(request);
	
			// Display recognized celebrity information
			List<Celebrity> celebs = result.getCelebrityFaces();
			System.out.println(celebs.size() + " celebrity(s) were recognized.\n");
	
			for (Celebrity celebrity : celebs) {
				System.out.println("Celebrity recognized: " + celebrity.getName());
				System.out.println("Celebrity ID: " + celebrity.getId());
				BoundingBox boundingBox = celebrity.getFace().getBoundingBox();
				System.out.println("position: " + boundingBox.getLeft().toString() + " " + boundingBox.getTop().toString());
				System.out.println("Further information (if available):");
				for (String url : celebrity.getUrls()) {
					System.out.println(url);
				}
				System.out.println();
			}
			imageFile.delete();
			return celebs;
		}catch(Exception e) {
			throw new AWSRekognitionException("Error:Recognizing celebrities unsuccesful..!"+e, e);
		}
	}

	// Detecting objects
	public ArrayList<?> detectObject(MultipartFile imageFile) {
		// Normalize file name
		String imageName = StringUtils.cleanPath(imageFile.getOriginalFilename());

		try {
			File file = convert_MultiPartToFile(imageFile);
			String externalImageName = generateFileName(imageFile);
			photo = serverName + businessName + externalImageName;
			uploadFileTos3bucket(photo, file);
			file.delete();
		} catch (Exception e) {
			throw new AWSRekognitionException("Could not find File"+imageName+".. Please try again!", e);
		}
		
		try {
			// Check if the file's name contains invalid characters
			if (imageName.contains("..")) {
				throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
			}

			Image image = new Image().withS3Object(new S3Object().withBucket(bucket) // make it dynamic
					.withName(photo));

			DetectLabelsRequest detectLabelsRequest = new DetectLabelsRequest().withImage(image).withMaxLabels(19)
					.withMinConfidence(60f);
			DetectLabelsResult result = aws.getRekognitionClient().detectLabels(detectLabelsRequest);
			List<Label> labels = result.getLabels();

			return (ArrayList<?>) labels;
		} catch (Exception e) {
			throw new AWSRekognitionException("Error:Recognizing objects unsuccesful..!"+e, e);
		}
	}

	// detect text
	public ArrayList<?> detectText(MultipartFile imageFile) {
		// Normalize file name
		String imageName = StringUtils.cleanPath(imageFile.getOriginalFilename());
		
		try {
			File file = convert_MultiPartToFile(imageFile);
			String externalImageName = generateFileName(imageFile);
			photo = serverName + businessName + externalImageName;
			uploadFileTos3bucket(photo, file);
			file.delete();
		} catch (Exception e) {
			throw new AWSRekognitionException("Could not store image " + imageName + ". Please try again!", e);
		}
		
		try {
			// Check if the file's name contains invalid characters
			if (imageName.contains("..")) {
				throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
			}
			Image image = new Image().withS3Object(new S3Object().withBucket(bucket) // make it dynamic
					.withName(photo));
			DetectTextRequest detectTextRequest = new DetectTextRequest().withImage(image);
			DetectTextResult result = aws.getRekognitionClient().detectText(detectTextRequest);
			List<TextDetection> textDetections = result.getTextDetections();
			return (ArrayList<?>) textDetections;
		} catch (Exception e) {
			throw new AWSRekognitionException("Error:Recognizing text unsuccessful..!"+e, e);
		}
	}

	// detect explicit content
	public ArrayList<?> detectUnsafeContent(MultipartFile imageFile,String bucketName) {
		setDetails(bucketName,null);
		// Normalize file name
		String imageName = StringUtils.cleanPath(imageFile.getOriginalFilename());

		try {
			File file = convert_MultiPartToFile(imageFile);
			String externalImageName = generateFileName(imageFile);
			photo = serverName + businessName + externalImageName;
			uploadFileTos3bucket(photo, file);
			file.delete();
		} catch (Exception e) {
			throw new AWSRekognitionException("Could not store image " + imageName + ". Please try again!", e);
		}
		
		try {
			// Check if the file's name contains invalid characters
			if (imageName.contains("..")) {
				throw new ImageStorageException("Sorry! Imagename contains invalid path sequence " + imageName);
			}
			Image image = new Image().withS3Object(new S3Object().withBucket(bucket) // make it dynamic
					.withName(photo));
			DetectModerationLabelsRequest detectModerationLabelsRequest = new DetectModerationLabelsRequest()
					.withImage(image).withMinConfidence(60f);
			DetectModerationLabelsResult result = aws.getRekognitionClient()
					.detectModerationLabels(detectModerationLabelsRequest);
			List<ModerationLabel> labels = result.getModerationLabels();

			return (ArrayList<?>) labels;
		} catch (Exception e) {
			throw new AWSRekognitionException("Unable to recognize .... Please try again!"+e, e);
		}

	}
	
   //verify whether already indexed or not
	public SearchResponse verify(MultipartFile imageFile,String bucketName,String galleryName) {
		SearchResponse response=searchFaces(imageFile,bucketName,galleryName);
		System.out.println("Number of faces indexed:"+response.getFaceId().size());
		return response;
	}
   //verify whether already indexed or not	
	public void deleteAllFace(String collectionName) {
		ListFacesRequest listFacesRequest = new ListFacesRequest().withCollectionId(collectionName);
		ListFacesResult	listFacesResult = aws.getRekognitionClient().listFaces(listFacesRequest);
		List<Face> faces = listFacesResult.getFaces();
		for (Face face : faces) {
			deleteFaceFromCollection(face.getFaceId(),collectionName);
		}
		
	}
	
	
}

