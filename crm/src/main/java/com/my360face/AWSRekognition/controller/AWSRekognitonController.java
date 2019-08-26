package com.my360face.AWSRekognition.controller;

import com.my360face.AWSRekognition.service.AWSRekognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class AWSRekognitonController {

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(AWSRekognitonController.class);

    @Autowired
    private AWSRekognitionService awsRekognitionService;

    public AWSRekognitionService getAwsRekognitionService() {
        return awsRekognitionService;
    }

    public void setAwsRekognitionService(AWSRekognitionService awsRekognitionService) {
        this.awsRekognitionService = awsRekognitionService;
    }

}
