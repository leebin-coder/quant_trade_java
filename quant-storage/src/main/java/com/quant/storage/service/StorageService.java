package com.quant.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Storage Service Interface
 */
@Slf4j
@Service
public class StorageService {

    public void upload(String path, InputStream inputStream) {
        log.info("Uploading file to path: {}", path);
        // TODO: Implement file upload logic
    }

    public InputStream download(String path) {
        log.info("Downloading file from path: {}", path);
        // TODO: Implement file download logic
        return null;
    }

    public void delete(String path) {
        log.info("Deleting file at path: {}", path);
        // TODO: Implement file deletion logic
    }
}
