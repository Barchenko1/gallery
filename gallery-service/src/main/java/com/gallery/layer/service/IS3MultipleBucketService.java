package com.gallery.layer.service;

import org.springframework.web.multipart.MultipartFile;

public interface IS3MultipleBucketService {
    void uploadFile(MultipartFile multipartFile);
    String getFileUrl(String objectKey);
    public byte[] downloadFile(String objectKey);
    void deleteFile(String objectKey);
}
