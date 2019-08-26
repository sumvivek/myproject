package com.my360face.AWSRekognition.service;

//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;


@Service
public class OpenCVFaceService {
@Autowired
public FaceService face;

//	public List<?> detectFaces(MultipartFile file ) 
//	{
//		File tmp=null;
//	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//	    try {
//			 tmp=face.convert_MultiPartToFile(file);
//		} catch (IOException e1) {
//			 e1.printStackTrace();
//		}
//	    
//	    
//	    MatOfRect matFaceList = new MatOfRect();
//	    Mat image =Imgcodecs.imread(file.getOriginalFilename(),0); 
//	    
//	    String xmlFile="src/main/resources/xml/lbpcascade_frontalface_improved.xml"; 
//	    CascadeClassifier faceDetector = new CascadeClassifier(xmlFile); 
//	   
//	    faceDetector.detectMultiScale(image, matFaceList);
//	    
//	    List<File> faceList = new ArrayList<>();
//	    
//	    File _tempFolder=new File("src/main/resources/faces");
//	    if (!matFaceList.empty()) {
//	        int i=0;
//	        String uniqueID = UUID.randomUUID().toString();
//	        for (Rect faceRectangle : matFaceList.toArray()) {
//	          Mat faceImage = image.submat(faceRectangle);
//	          String fileName = _tempFolder + "\\Face_"+ uniqueID +"_"+ i + ".jpg";
//	          Imgcodecs.imwrite(fileName, faceImage);
//	          faceList.add(new File(fileName));
//
//	          i++;
//
//	          try {
//	            Thread.sleep(1000);
//	          } catch (InterruptedException e) {
//	            e.printStackTrace();
//	          }
//	        }
//	      }
//	    
//   	    System.out.println(String.format("Detected %s faces", matFaceList.toArray().length));   
//   	    
//
//		List<String> faceCounts=new ArrayList<String>();
//		JSONObject response=new JSONObject();
//		try {
//			response.put("Detected face(s)", matFaceList.toArray().length);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		faceCounts.add(response.toString());
//		tmp.delete();
//		return  faceCounts;	
//    	 
//	}
//	
//	public void recognize() {
//		// mention the directory the faces has been saved
//				String trainingDir = "./faces";
//				File root = new File(trainingDir);
//				
//				FilenameFilter imgFilter = new FilenameFilter() {
//					public boolean accept(File dir, String name) {
//						name = name.toLowerCase();
//						return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
//					}
//				};
//				
//				File[] imageFiles = root.listFiles(imgFilter);
//				
//				
//				
//				
//				
//				
//	}

	}
