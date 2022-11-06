package com.gallery.layer.service;

import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IS3MultipleBucketService {

    void setUploadFilesSize(long uploadFilesSize);
    void createBucket(String bucketName);
    void createBucket(String bucketName, String region);
    void createBucket(String bucketName, PublicAccessBlockConfiguration publicAccessBlockConfiguration);
    void createBucket(String bucketName, String region, PublicAccessBlockConfiguration publicAccessBlockConfiguration);
    void cleanUpBucket(String bucketName);
    void deleteBucket(String bucketName);
    void deleteFile(String objectKey);
    void deleteFolder(String objectKey);
    S3ObjectSummary getS3ObjectSummary(String objectKey);
    S3Object getS3Object(String objectKey);
    String getFileUrl(String objectKey);
    boolean doesObjectExist(String objectKey);
    boolean doesFolderPathExist(String objectKey);
    boolean doesBucketExist(String bucketName);
    byte[] downloadFile(String objectKey);
    void uploadFile(String folderPath, File file);
    void uploadFileAsync(String folderPath, File file);
    void uploadMultipartFile(String folderPath, MultipartFile multipartFile);
    void uploadMultipartFileAsync(String folderPath, MultipartFile multipartFile);
    void copyFolderAndRemove(String folderPath, String destinationPath);
    void copyFolder(String folderPath, String destinationPath);
}
