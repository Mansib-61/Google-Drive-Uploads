package com.techwithden.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

@Service
public class DriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACOUNT_KEY_PATH = getPathToGoogleCredentials();

    private static String getPathToGoogleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "cred.json");
        return filePath.toString();
    }

    public Res uploadImageToDrive(File file) throws GeneralSecurityException, IOException {
        Res res = new Res();

        try {
            String folderId = "1tKXn6pDPksruMickfTdLx6alkeqbPfSE"; // Your Google Drive folder ID
            Drive drive = createDriveService();
            
            // Create file metadata
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));

            // File content type
            FileContent mediaContent = new FileContent("image/jpeg", file);

            // Upload file
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();

            // Set file permissions to public
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            drive.permissions().create(uploadedFile.getId(), permission).execute();

            // Generate public URL
            String imageUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
            file.delete();
            
            // Return success response
            res.setStatus(200);
            res.setMessage("Image Successfully Uploaded To Drive");
            res.setUrl(imageUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            res.setStatus(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }
    // New method to list all images in the Google Drive folder
    public List<String> listImagesInDrive() throws GeneralSecurityException, IOException {
        Drive drive = createDriveService();
        String folderId = "1tKXn6pDPksruMickfTdLx6alkeqbPfSE"; // Your folder ID
        String query = "'" + folderId + "' in parents and mimeType contains 'image/'";
        FileList result = drive.files().list().setQ(query).setFields("files(id, name)").execute();

        List<String> imageUrls = new ArrayList<>();
        for (com.google.api.services.drive.model.File file : result.getFiles()) {
            String imageUrl = "https://drive.google.com/uc?export=view&id=" + file.getId();
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .build();
    }
}
