package com.gallery.layer.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

public interface IS3MultipleBucketService {
    void uploadFile(MultipartFile multipartFile);
    String getFileUrl(String objectKey);
    public byte[] downloadFile(String objectKey);
    void deleteFile(String objectKey);
    S3ObjectSummary getS3ObjectSummary(String objectKey);
    S3Object getS3Object(String objectKey);
}
