package com.concord.concordapi.fileStorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.concord.concordapi.fileStorage.dto.FileUploadResponseDto;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.entity.FileType;
import com.concord.concordapi.shared.exception.EmptyFileException;
import com.concord.concordapi.shared.exception.FileFormatException;
import com.concord.concordapi.shared.exception.FileNotFoundException;
import com.concord.concordapi.shared.exception.FileStorageException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.UrlResource;


@Service
public class FileStorageService {

    private Path storagePath;

    public FileStorageService(@Value("${file.storage.path}") String storagePath){
        this.storagePath = Paths.get(storagePath);
    }

    public FileUploadResponseDto storeImageTemporarily(MultipartFile image) {
        validateFile(image);
        if (isImage(image)) {
            String id = storeFileTemporarily(image);
            return new FileUploadResponseDto(id, FileType.IMAGE);
        }
        throw new FileFormatException("Invalid format! Only images are allowed");
    }

    public String persistImage(FilePrefix prefix, String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")||fileName.endsWith(".png") || fileName.endsWith(".webp")) {
            return persistFile(prefix, fileName);
        }
        throw new FileFormatException("File must be .jpg, .png, or .webp");
    }

    private String persistFile(FilePrefix prefix, String filename) {
       try {
           Path sourcePath = getTargetPath("tempfiles", filename);
           validateFileExistence(sourcePath);

           Path destinationPath = getTargetPath(prefix.getDisplayName(), filename);
           Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

           return prefix.getDisplayName() + "/" + filename;
       } catch (FileNotFoundException e) {
           throw e;
       } catch (Exception e) {
           throw new FileStorageException("Failed to persist file.", e);
       }
    }

    public void deleteFile(String filePath) {
        try {
            Path sourcePath = storagePath.resolve(filePath);
            validateFileExistence(sourcePath);
            Files.delete(sourcePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to remove image.", e);
        }
    }

    public UrlResource downloadFile(String filePath) {
        Path sourcePath = storagePath.resolve(filePath);
        validateFileExistence(sourcePath);
        try {
            return new UrlResource(sourcePath.toUri());
        } catch (Exception e) {
            throw new FileStorageException("Error while trying to read the file " + filePath);
        }
    }

    public String getImageUrl(String s) {
        return getServerUrl() + "/api/files/images?file-id=" + s;
    }

    public String updateFile(String oldPath, String newFilename, FilePrefix filePrefix) {
        try {
            String persisted = persistFile(filePrefix, newFilename);
            deleteFile(oldPath);
            return persisted;
        } catch (RuntimeException e) {
            throw new FileStorageException("Failed while trying to update the image", e);
        }
    }

    private String storeFileTemporarily(MultipartFile file) {
        try {
            String filename = getUniqueFilename(file.getOriginalFilename());
            Path targetPath = getTargetPath("tempfiles", filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file temporarily.", e);
        }
    }

    private void validateFile(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("The uploaded file is empty.");
        }
    }
    public boolean fileExists(String filePath){
        Path sourcePath = storagePath.resolve(filePath);
        return Files.exists(sourcePath);
    }

    private void validateFileExistence(Path filePath){
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File " + filePath +  " does not exist.");
        }
    }
 
    private String getUniqueFilename(String originalFilename){
        String uuid = UUID.randomUUID().toString();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return uuid + fileExtension;
    }

    public Path getTargetPath(String folderName, String filename) throws IOException {
        Path directoryPath = storagePath.resolve(folderName).normalize();
        if (!Files.exists(directoryPath)){
            Files.createDirectories(directoryPath);
        }
        return directoryPath.resolve(filename).normalize();
    }

    private boolean isImage(MultipartFile file) {
        List<String> imageMimeTypes = List.of("image/jpeg", "image/png", "image/webp");

        String mimeType = file.getContentType();
        return imageMimeTypes.contains(mimeType);
    }

    private String getServerUrl() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attrs.getRequest();

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        return scheme + "://" + serverName + ":" + serverPort;
    }

}