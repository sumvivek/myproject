package com.my360face.AWSRekognition.exception;

import com.amazonaws.AmazonClientException;

public class AWSRekognitionException extends AmazonClientException {
    public AWSRekognitionException(String message) {
        super(message);
    }

    public AWSRekognitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
