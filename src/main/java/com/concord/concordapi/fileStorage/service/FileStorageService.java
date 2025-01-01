package com.concord.concordapi.fileStorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.concord.concordapi.fileStorage.dto.FileUploadResponseDto;
import com.concord.concordapi.fileStorage.entity.FileType;
import com.concord.concordapi.shared.exception.EntityNotFoundException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileStorageService(@Value("${file.storage.path}") String storagePath){
        this.storagePath = Paths.get(storagePath);
    }

    public FileUploadResponseDto storePdfTemporarily(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        if (contentType.equals("application/pdf")) {
            String id = storeFileTemporarily(file);
            return new FileUploadResponseDto(id, FileType.PDF);
        }

        throw new FileFormatException("Invalid format! Only PDFs are allowed");
    }

    public FileUploadResponseDto storeImageTemporarily(MultipartFile image) {
        validateFile(image);

        if (isImage(image)) {
            String id = storeFileTemporarily(image);
            return new FileUploadResponseDto(id, FileType.IMAGE);
        }

        throw new FileFormatException("Invalid format! Only images are allowed");
    }

    public List<FileUploadResponseDto> storeMultipleImagesTemporarily(List<MultipartFile> files) {
        List<FileUploadResponseDto> filenames = new ArrayList<>();
        for (MultipartFile file : files){
            filenames.add(storeImageTemporarily(file));
        }
        return filenames;
    }

    public String persistImage(FilePrefix prefix, String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return persistFile(prefix, fileName);
        }
        throw new FileFormatException("File must be .jpg");
    }

    public String persistPdf(FilePrefix prefix, String fileName) {
        if (fileName.endsWith(".pdf")) {
            return persistFile(prefix, fileName);
        }
        throw new FileFormatException("File must be .pdf");
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

    public void deleteFiles(List<String> filesPath) {
        for (String filePath: filesPath) {
            deleteFile(filePath);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path sourcePath = storagePath.resolve(filePath);
            validateFileExistence(sourcePath);
            Files.delete(sourcePath);
        } catch (APIException e) {
            throw e;
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
        return getServerUrl() + "/api/image?file-id=" + s;
    }

    public String getPdfUrl(String s) {
        return getServerUrl() + "/api/pdf?file-id=" + s;
    }

    public String updateFile(String oldPath, String newFilename, FilePrefix filePrefix) {
        try {
            String persisted = persistFile(filePrefix, newFilename);
            deleteFile(oldPath);
            return persisted;
        } catch (APIException e) {
            throw e;
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

    private Path getTargetPath(String folderName, String filename) throws IOException {
        Path directoryPath = storagePath.resolve(folderName).normalize();
        if (!Files.exists(directoryPath)){
            Files.createDirectories(directoryPath);
        }
        return directoryPath.resolve(filename).normalize();
    }

    private boolean isImage(MultipartFile file) {
        List<String> imageMimeTypes = List.of("image/jpeg");

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