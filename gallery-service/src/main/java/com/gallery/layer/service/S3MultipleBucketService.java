package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.cli.AwsCliProcess;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;

public class S3MultipleBucketService implements IS3MultipleBucketService {
    private final long bucketLimit;
    private final AwsCliProcess awsCliProcess;
    private final S3BucketService s3BucketService;
    private final List<Bucket> bucketList;

    public S3MultipleBucketService(AmazonS3 s3Client, long bucketLimit) {
        this.s3BucketService = new S3BucketService(s3Client);
        this.bucketLimit = bucketLimit;
        this.awsCliProcess = new AwsCliProcess();
        this.bucketList = s3BucketService.getBucketList();
    }

    @Override
    public void uploadFile(MultipartFile multipartFile) {
        getFreeBucket()
                .findFirst()
                .ifPresent(bucket -> s3BucketService.uploadFile(bucket.getName(), multipartFile));
    }

    @Override
    public void uploadEmptyFolder(String folderName) {

    }

    @Override
    public void uploadFolder(String folderName) {

    }

    @Override
    public void uploadFileToFolder(String folder, String objectKey) {

    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {

    }

    @Override
    public void renameFolder(String folderPath, String newFolderPath) {

    }

    @Override
    public String getFileUrl(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.getFileUrl(bucket.getName(), objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("url with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public byte[] downloadFile(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.downloadFile(bucket.getName(), objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("file with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public void createBucket(String bucketName) {
        s3BucketService.createBucket(bucketName);
    }

    @Override
    public void createBucket(String bucketName, String region) {
        s3BucketService.createBucket(bucketName, region);
    }

    @Override
    public void deleteBucket(String bucketName) {
        s3BucketService.deleteBucket(bucketName);
    }

    @Override
    public void deleteFile(String objectKey) {
        getFilteredBucket(objectKey)
                .findFirst()
                .ifPresent(bucket -> s3BucketService.deleteFile(bucket.getName(), objectKey));
    }

    @Override
    public void deleteFolder(String objectKey) {

    }

    @Override
    public S3ObjectSummary getS3ObjectSummary(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.getS3ObjectSummary(bucket.getName(), objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("s3ObjectSummary with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public S3Object getS3Object(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.getS3Object(bucket.getName(), objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("s3Object with objectKey: %s wasn't found", objectKey)));
    }

    private Stream<Bucket> getFreeBucket() {
        return bucketList.stream()
                .filter(bucket -> awsCliProcess.isBucketAvailable(bucket.getName(), bucketLimit));
    }

    private Stream<Bucket> getFilteredBucket(String objectKey) {
        return bucketList.stream()
                .filter(bucket -> s3BucketService.doesObjectExist(bucket.getName(), objectKey));
    }
}
