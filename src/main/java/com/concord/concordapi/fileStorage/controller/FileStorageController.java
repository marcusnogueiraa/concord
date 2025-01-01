package com.concord.concordapi.fileStorage.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.concord.concordapi.fileStorage.dto.FileUploadResponseDto;
import com.concord.concordapi.fileStorage.service.FileStorageService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("upload/image")
    public ResponseEntity<FileUploadResponseDto> uploadImage(
            @RequestParam("image")
            @NotNull MultipartFile image
    ) {
        FileUploadResponseDto response = fileStorageService.storeImageTemporarily(image);
        return ResponseEntity.ok(response);
    }

    @PostMapping("upload/images")
    public ResponseEntity<List<FileUploadResponseDto>> uploadImages(
            @RequestParam("images")
            @Size(min = 1, max = 10) List<MultipartFile> images
    ) {
        List<FileUploadResponseDto> response = fileStorageService.storeMultipleImagesTemporarily(images);
        return ResponseEntity.ok(response);
    }

    @PostMapping("upload/pdf")
    public ResponseEntity<FileUploadResponseDto> uploadPdf(
            @RequestParam @NotNull MultipartFile file
    ) {
        FileUploadResponseDto response = fileStorageService.storePdfTemporarily(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("image")
    public ResponseEntity<UrlResource> downloadImage(@RequestParam("file-id") String filePath) {
        var file = fileStorageService.downloadFile(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }

    @GetMapping("pdf")
    public ResponseEntity<UrlResource> downloadPdf(@RequestParam("file-id") String filePath) {
        var file = fileStorageService.downloadFile(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}