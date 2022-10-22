package com.gallery.layer.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

public interface IS3MultipleBucketService {
    void createBucket(String bucketName);
    void createBucket(String bucketName, String region);
    void deleteBucket(String bucketName);
    void deleteFile(String objectKey);
    void deleteFolder(String objectKey);
    S3ObjectSummary getS3ObjectSummary(String objectKey);
    S3Object getS3Object(String objectKey);
    String getFileUrl(String objectKey);
    public byte[] downloadFile(String objectKey);
    void uploadFile(MultipartFile multipartFile);
    void uploadEmptyFolder(String folderName);
    void uploadFolder(String folderName);
    void uploadFileToFolder(String folder, String objectKey);
    void moveFolder(String folderPath, String destinationPath);
    void renameFolder(String folderPath, String newFolderPath);

}
