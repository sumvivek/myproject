package com.my360face.image;

import com.my360face.image.property.ImageStorageProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		ImageStorageProperties.class
})
public class My360FaceApplication {

	/*public static void main(String[] args) {
		SpringApplication.run(My360FaceApplication.class, args);
	}*/
}
