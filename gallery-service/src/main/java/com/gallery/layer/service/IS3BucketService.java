package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface IS3BucketService {

    AmazonS3 getS3Client();
    void createBucket(String bucketName);
    void createBucket(String bucketName, PublicAccessBlockConfiguration publicAccessBlockConfiguration);
    void createBucket(String bucketName, String region, PublicAccessBlockConfiguration publicAccessBlockConfiguration);
    void createBucket(String bucketName, String region);
    void cleanUpBucket(String bucketName);
    void deleteBucket(String bucketName);
    void deleteFile(String bucketName, String objectKey);
    void deleteFolder(String bucketName, String objectKey);
    List<Bucket> getBucketList();
    List<S3ObjectSummary> getS3ObjectSummaryList(String bucketName, String folderPath);
    S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey);
    S3Object getS3Object(String bucketName, String objectKey);
    String getFileUrl(String bucketName, String objectKey);
    boolean doesObjectExist(String bucketName, String objectKey);
    boolean doesFolderPathExist(String bucketName, String objectKey);
    boolean doesBucketExist(String bucketName);
    void uploadFile(String bucketName, String folderPath, File file);
    void uploadFileTfm(String bucketName, String folderPath, File file);
    void uploadFile(String bucketName, String folderPath, File file, List<Tag> tagList);
    void uploadFileTfm(String bucketName, String folderPath, File file, List<Tag> tagList);
    void uploadFolder(String bucketName, String targetPrefix, File folder);
    void uploadFolder(String bucketName, String targetPrefix, File folder, List<Tag> tagList);
    void uploadMultipartFile(String bucketName, String folderPath, MultipartFile multipartFile);
    void uploadMultipartFileTfm(String bucketName, String folderPath, MultipartFile multipartFile);
    void uploadMultipartFile(String bucketName, String folderPath, MultipartFile multipartFile, List<Tag> tagList);
    void uploadMultipartFileTfm(String bucketName, String folderPath, MultipartFile multipartFile, List<Tag> tagList);
    byte[] downloadFile(String bucketName, String objectKey);
    void downloadFolder(String bucketName, String folderPath, String destinationPath);
    void copyFileAndRemove(String bucketName, String objectKey, String destinationBucketName, String destinationObjectKey);
    void copyFile(String bucketName, String objectKey, String destinationBucketName, String destinationObjectKey);
    void copyFolderAndRemove(String bucketName, String folderPath, String destinationBucketName, String destinationPath);
    void copyFolder(String bucketName, String folderPath, String destinationBucketName, String destinationPath);
    List<Tag> getObjectTagging(String bucketName, String objectKey);
}
