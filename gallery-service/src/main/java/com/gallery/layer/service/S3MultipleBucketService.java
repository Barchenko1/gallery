package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.cli.AwsCliProcess;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class S3MultipleBucketService implements IS3MultipleBucketService {
    private final S3BucketService s3BucketService;
    private final long bucketLimit;
    private final AwsCliProcess awsCliProcess;
    private final List<Bucket> bucketList;
    private ConcurrentMap<String, Long> bucketNameSizeMap;
    private long uploadFilesSize;
    private List<String> bucketNameList;

    public S3MultipleBucketService(AmazonS3 s3Client, long bucketLimit) {
        this.s3BucketService = new S3BucketService(s3Client);
        this.bucketLimit = bucketLimit;
        this.awsCliProcess = new AwsCliProcess();
        this.bucketList = s3BucketService.getBucketList();
        this.bucketNameSizeMap = new ConcurrentHashMap<>();
    }

    public S3MultipleBucketService(AmazonS3 s3Client, List<String> bucketNameList, long bucketLimit) {
        this.s3BucketService = new S3BucketService(s3Client);
        this.bucketLimit = bucketLimit;
        this.awsCliProcess = new AwsCliProcess();
        this.bucketList = s3BucketService.getSelectedBucketList(bucketNameList);
        this.bucketNameSizeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void setUploadFilesSize(long uploadFilesSize) {
        this.uploadFilesSize = uploadFilesSize;
    }

    @Override
    public void uploadFile(String folderPath, File file) {
        executeConsumerOnFreeBucket(bucket ->
                s3BucketService.uploadFile(bucket.getName(), folderPath, file));
    }

    @Override
    public void uploadFileAsync(String folderPath, File file) {
        executeConsumerOnFreeBucket(bucket ->
                s3BucketService.uploadFileAsync(bucket.getName(), folderPath, file));
    }

    @Override
    public void uploadMultipartFile(String folderPath, MultipartFile multipartFile) {
        executeConsumerOnFreeBucket(bucket ->
                s3BucketService.uploadMultipartFile(bucket.getName(), folderPath, multipartFile));
    }

    @Override
    public void uploadMultipartFileAsync(String folderPath, MultipartFile multipartFile) {
        executeConsumerOnFreeBucket(bucket ->
                s3BucketService.uploadMultipartFileAsync(bucket.getName(), folderPath, multipartFile));
    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {
        getFilteredBucket(folderPath)
                .findFirst()
                .ifPresent(bucket ->
                        s3BucketService.copyFolderAndRemove(bucket.getName(), folderPath, destinationPath));
    }

    @Override
    public void copyFolder(String folderPath, String destinationPath) {
        getFilteredBucket(folderPath)
                .findFirst()
                .ifPresent(bucket ->
                        s3BucketService.copyFolder(bucket.getName(), folderPath, destinationPath));
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
    public boolean doesObjectExist(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.doesObjectExist(bucket.getName(), objectKey))
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean doesFolderPathExist(String objectKey) {
        return getFilteredBucket(objectKey)
                .map(bucket -> s3BucketService.doesFolderPathExist(bucket.getName(), objectKey))
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        return bucketList.stream()
                .map(bucket -> s3BucketService.doesBucketExist(bucketName))
                .findFirst()
                .orElse(false);
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
    public void createBucket(String bucketName, PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        s3BucketService.createBucket(bucketName, publicAccessBlockConfiguration);
    }

    @Override
    public void createBucket(String bucketName, String region, PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        s3BucketService.createBucket(bucketName, region, publicAccessBlockConfiguration);
    }

    @Override
    public void cleanUpBucket(String bucketName) {
        s3BucketService.cleanUpBucket(bucketName);
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
        getFilteredBucket(objectKey)
                .findFirst()
                .ifPresent(bucket -> s3BucketService.deleteFolder(bucket.getName(), objectKey));
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

    private void executeConsumerOnFreeBucket(Consumer<Bucket> s3BucketConsumer) {
        bucketList.stream()
                .filter(bucket -> awsCliProcess.isBucketAvailable(bucket.getName(), calculateLimitSize()))
                .findFirst()
                .ifPresent(s3BucketConsumer);
    }

    private Stream<Bucket> getFilteredBucket(String objectKey) {
        return bucketList.stream()
                .filter(bucket -> isObjectKeyExist(bucket.getName(), objectKey));
    }

    private boolean isObjectKeyExist(String bucketName, String objectKey) {
        return s3BucketService.doesObjectExist(bucketName, objectKey)
                || s3BucketService.doesFolderPathExist(bucketName, objectKey);
    }

    private long calculateLimitSize() {
        return bucketLimit - uploadFilesSize;
    }

}
