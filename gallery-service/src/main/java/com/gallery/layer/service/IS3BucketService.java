package com.gallery.layer.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IS3BucketService {
    void createBucket(String bucketName);
    void createBucket(String bucketName, String region);
    void deleteBucket(String bucketName);
    List<Bucket> getBucketList();
    boolean doesObjectExist(String bucketName, String objectKey);
    void uploadFile(String bucket, MultipartFile multipartFile);
    byte[] downloadFile(String bucket, String objectKey);
    String getFileUrl(String bucketName, String objectKey);
    void deleteFile(String bucket, String objectKey);
    S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey);
    S3Object getS3Object(String bucketName, String objectKey);
    void addFileToFolder(String bucket, String prefix, String objectKey);
    void addFolder(String bucket, String folderName);
//    List<PictureModal> getFolderPictureList(String folderName);
//    List<PictureModal> getAllPictures();
//    List<PictureModal> getPicturesChunk(int chunk);
//    Optional<PictureModal> getPictureByName(String name);
//    void deletePictureByName(String name);
//    void deleteFolderByName(String folderName);

}
