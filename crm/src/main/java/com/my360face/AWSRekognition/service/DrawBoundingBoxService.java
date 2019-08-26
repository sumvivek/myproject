package com.my360face.AWSRekognition.service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class DrawBoundingBoxService  {
/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

@Autowired
public FaceService face;

@Autowired
public AWSRekognitionService aws;
	
BufferedImage image;
static int scale;
SearchFacesByImageResult result;

	
	public void searchFaceBoundingBox( Object imageResult) throws IOException {
		 List<FaceMatch> faceDetails = ((SearchFacesByImageResult) imageResult).getFaceMatches();
		 List<BoundingBox> boundingBoxes = new ArrayList<>(faceDetails.size());
		 String t="";
		 for (FaceMatch face : faceDetails) {
			  boundingBoxes.add(face.getFace().getBoundingBox());
		      t=face.getFace().getExternalImageId();  
		 }
		 
		 com.amazonaws.services.s3.model.S3Object s3object = aws.getS3Client().getObject("my360crm.comx.com", ("kindy001/RotiGhar/"+t));
		 S3ObjectInputStream inputStream= s3object.getObjectContent();
		 BufferedImage image = ImageIO.read(inputStream);		 //	 drawtest((SearchFacesByImageResult) imageResult, bytearr);
		 drawBoundingBoxes(image,"ROTATE_0",boundingBoxes);
		 
	}
	
	
	public void compareFaceBoundingBox (CompareFacesResult imageResult,ByteBuffer sourceImageBytes,ByteBuffer targetImageBytes) throws IOException {
		List<CompareFacesMatch> faceDetails = ((CompareFacesResult) imageResult).getFaceMatches();
		List<BoundingBox> boundingBoxes = new ArrayList<>(faceDetails.size());
		for (CompareFacesMatch match : faceDetails) {
				ComparedFace face = match.getFace();
				boundingBoxes.add(face.getBoundingBox());
				
		}

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(targetImageBytes.array()));
		drawBoundingBoxes(image,"ROTATE_0",boundingBoxes);
	}
	
	
	
	
	
	
	
	
	
	 public void drawBoundingBoxes(BufferedImage img, String orientationCorrection, List<BoundingBox> boundingBoxes) throws IOException {
	        int width;
	        int height;
	       // BufferedImage img;
	        Graphics2D graphics;
	        // img = ImageIO.read(new ByteArrayInputStream(bytes));
	            width    = img.getWidth();
	            height	 = img.getHeight();
	            graphics = img.createGraphics();
	        System.out.println("Image: width=" + width + ", height=" + height);
	        
	        
	        for (BoundingBox bb : boundingBoxes) {

	            drawBoundingBox(bb, orientationCorrection, width, height, graphics);
	        }
	        
	        try {
	            ImageIO.write(img, "jpg", new File("img_bb.jpg"));
	        } catch (IOException e) {
	            System.err.println("Failed to write image: " + e.getLocalizedMessage());
	        }
	    }

	    private void drawBoundingBox(BoundingBox bb, String orientationCorrection, int width, int height,
	            Graphics2D graphics) {
	        
	        BoundingBox cbb = convertBoundingBox(bb, orientationCorrection, width, height);
	        if (cbb == null) {
	            return;
	        }
	        
	        graphics.setColor(Color.GREEN);
	        graphics.setStroke(new BasicStroke(5));
	        graphics.drawRect(cbb.getLeft().intValue(), cbb.getTop().intValue(), 
	                cbb.getWidth().intValue(), cbb.getHeight().intValue());
	    }
	 
	    private BoundingBox convertBoundingBox(BoundingBox bb, String orientationCorrection, int width, int height) {
	        if (orientationCorrection == null) {
	            System.out.println("No orientationCorrection available.");
	            return null;
	        } else {
	            float left = -1;
	            float top = -1;
	            switch (orientationCorrection) {
	            case "ROTATE_0":
	                left = width * bb.getLeft();
	                top = height * bb.getTop();
	                break;
	            case "ROTATE_90":
	                left = height * (1 - (bb.getTop() + bb.getHeight()));
	                top = width * bb.getLeft();
	                break;
	            case "ROTATE_180":
	                left = width - (width * (bb.getLeft() + bb.getWidth()));
	                top = height * (1 - (bb.getTop() + bb.getHeight()));
	                break;
	            case "ROTATE_270":
	                left = height * bb.getTop();
	                top = width * (1 - bb.getLeft() - bb.getWidth());
	                break;
	            default:
	                System.out.println("Orientation correction not supported: " + 
	                        orientationCorrection);
	                return null;
	            }
	            System.out.println("BoundingBox: left=" + (int)left + ", top=" + 
	                    (int)top + ", width=" + (int)(bb.getWidth()*width) + 
	                    ", height=" + (int)(bb.getHeight()*height));
	            BoundingBox outBB = new BoundingBox();
	            outBB.setHeight(bb.getHeight()*height);
	            outBB.setWidth(bb.getWidth()*width);
	            outBB.setLeft(left);
	            outBB.setTop(top);
	            return outBB;
	        }
	    }
	    
	 
	
	
	
	
	
}
