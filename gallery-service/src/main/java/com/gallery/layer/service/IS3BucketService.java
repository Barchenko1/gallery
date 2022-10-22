package com.gallery.layer.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IS3BucketService {
    void createBucket(String bucketName);
    void createBucket(String bucketName, String region);
    void deleteBucket(String bucketName);
    void deleteFile(String bucketName, String objectKey);
    void deleteFolder(String bucketName, String objectKey);
    List<Bucket> getBucketList();
    S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey);
    S3Object getS3Object(String bucketName, String objectKey);
    String getFileUrl(String bucketName, String objectKey);
    boolean doesObjectExist(String bucketName, String objectKey);
    void uploadFiles(String bucketName, String folderPath, List<MultipartFile> multipartFileList);
    void uploadFolder(String bucketName, String folderName);
    void uploadFilesMultipart(String bucketName, String folderPath, List<MultipartFile> multipartFileList);
    byte[] downloadFile(String bucketName, String objectKey);
    void copyFolderAndRemove(String bucketName, String folderPath, String destinationPath);
    void renameFolder(String bucketName, String folderPath, String newFolderPath);
//    List<?> getFolderFileList(String folderName);
//    List<?> getFileChunk(String folderName, int chunk);
}
