package com.techwithden.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class Controller {

    @Autowired
    private DriveService service;

    // Endpoint to upload an image to Google Drive
    @PostMapping("/uploadToGoogleDrive")
    public Object handleFileUpload(@RequestParam("image") MultipartFile file) throws IOException, GeneralSecurityException {
        if (file.isEmpty()) {
            return "File is empty";
        }
        
        // Create a temporary file and transfer the contents of the uploaded file to it
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        
        // Call the service to upload the image to Google Drive and return the result
        Res res = service.uploadImageToDrive(tempFile);
        return res;
    }

    // Endpoint to get all uploaded images from Google Drive
    @GetMapping("/getUploadedImages")
    public List<String> getUploadedImages() throws GeneralSecurityException, IOException {
        return service.listImagesInDrive();
    }
}
