package com.my360face.AWSRekognition.controller;
/**
 * @author AshokSelva
 *
 * Date:05/16/2019
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my360face.AWSRekognition.service.VideoService;
import com.my360face.image.exception.ImageStorageException;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class VideoController {
	@Autowired 
	public VideoService videoService;
	private static final List<String> contentTypesVideo  = Arrays.asList("video/x-flv", "video/mp4", "video/MP2T","video/3gpp",	"video/x-ms-wmv","video/mkv");
	  
	  @PostMapping("/detectface-video")
	  public ArrayList<?> detectFaceVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="detectFace";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
				return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	  
	  @PostMapping("/detectobject-video")
	  public ArrayList<?> detectObjectVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="detectObject";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
		    	return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	
	  @PostMapping("/recognize-video")
	  public ArrayList<?> searchFaceVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="searchFace";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
		    	return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	
	  @PostMapping("/trackperson-video")
	  public ArrayList<?> trackPersonVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="trackPerson";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
		    	return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	
	  @PostMapping("/detectcelebrity-video")
	  public ArrayList<?> detectCelebrityVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="detectCelebrity";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
		    	return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	
	  @PostMapping("/detectunsafecontent-video")
	  public ArrayList<?> detectUnsafeContentVideo(@RequestParam("video")MultipartFile videoFile,@RequestParam("limit") int limit,@RequestParam(value="bucket",required=false)String bucket) throws Exception{
	  	final String method="detectUnsafeContent";
	  	if(contentTypesVideo.contains(videoFile.getContentType())) {
		    	return videoService.videoRekognition(videoFile,method,limit,bucket);
			}else throw new ImageStorageException("Sorry! The uploaded file is not a video file..!");
	  }
	  
	  @PostMapping("/teststream")
	  public void testStream() {
		  videoService.test();
	  }
}
