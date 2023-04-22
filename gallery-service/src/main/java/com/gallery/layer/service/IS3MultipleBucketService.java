package com.gallery.layer.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface IS3MultipleBucketService {

    void createBucket(String bucketName, long bucketLimit);

    void createBucket(String bucketName, long bucketLimit, String region);

    void createBucket(String bucketName, long bucketLimit,
                      PublicAccessBlockConfiguration publicAccessBlockConfiguration);

    void createBucket(String bucketName, long bucketLimit, String region,
                      PublicAccessBlockConfiguration publicAccessBlockConfiguration);
    void cleanUpBucket(String bucketName);
    void deleteBucket(String bucketName);
    void deleteFile(String objectKey);
    void deleteFolder(String objectKey);
    List<Bucket> getBucketList();
    List<S3ObjectSummary> getS3ObjectSummaryList(String folderPath);
    List<S3ObjectSummary> getS3ObjectSummaryListWithLimit(String folderPath, int limit);
    S3ObjectSummary getS3ObjectSummary(String objectKey);
    S3Object getS3Object(String objectKey);
    String getFileUrl(String objectKey);
    boolean doesObjectExist(String objectKey);
    boolean doesFolderPathExist(String objectKey);
    boolean doesBucketExist(String bucketName);
    void uploadFile(String folderPath, File file);
    void uploadFileTfm(String folderPath, File file);
    void uploadFile(String folderPath, File file, List<Tag> tagList);
    void uploadFileTfm(String folderPath, File file, List<Tag> tagList);
    void uploadFolder(String targetPrefix, File folder);
    void uploadFolder(String targetPrefix, File folder, List<Tag> tagList);
    void uploadMultipartFile(String folderPath, MultipartFile multipartFile);
    void uploadMultipartFileTfm(String folderPath, MultipartFile multipartFile);
    byte[] downloadFile(String objectKey);
    void downloadFolder(String folderPath, String destinationPath);
    void copyFileAndRemove(String objectKey, String destinationObjectKey);
    void copyFile(String objectKey, String destinationObjectKey);
    void copyFolderAndRemove(String folderPath, String destinationPath);
    void copyFolder(String folderPath, String destinationPath);
    List<Tag> getObjectTagging(String objectKey);
}
