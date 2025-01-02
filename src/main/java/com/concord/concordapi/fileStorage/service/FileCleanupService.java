package com.concord.concordapi.fileStorage.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.concord.concordapi.shared.exception.FileStorageException;

@Service
public class FileCleanupService {

    @Value("${file.storage.path}")
    private String DIRECTORY_PATH;

    @Scheduled(cron = "0 0 0 * * ?") 
    public void cleanUpOldFiles() {
        File directory = new File(DIRECTORY_PATH + "/tempfiles");

        if (!directory.exists() || !directory.isDirectory()) 
            throw new FileStorageException(directory.toString() + "dont exists (Cron Job)");

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        Instant twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS);

        for (File file : files) {
            if (file.isFile()) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    Instant fileCreationTime = attrs.creationTime().toInstant();
                    
                    if (fileCreationTime.isBefore(twoDaysAgo)) 
                        file.delete();

                } catch (Exception e) {
                    System.err.println("Erro ao processar arquivo: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
    }
}