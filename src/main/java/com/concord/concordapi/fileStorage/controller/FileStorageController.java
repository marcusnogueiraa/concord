package com.concord.concordapi.fileStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.concord.concordapi.fileStorage.dto.FileUploadResponseDto;
import com.concord.concordapi.fileStorage.service.FileStorageService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("images")
    public ResponseEntity<FileUploadResponseDto> uploadImage(
            @RequestParam("image")
            @NotNull MultipartFile image
    ) {
        FileUploadResponseDto response = fileStorageService.storeImageTemporarily(image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("images")
    public ResponseEntity<UrlResource> downloadImage(@RequestParam("file-id") String filePath) {
        var file = fileStorageService.downloadFile(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }

 
}