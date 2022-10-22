package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.gallery.layer.cli.AwsCliProcess;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;

public class S3MultipleBucketService implements IS3MultipleBucketService {
    private final AmazonS3 s3Client;
    private final long bucketLimit;
    private final AwsCliProcess awsCliProcess;
    private final S3BucketService s3BucketService;
    private final List<Bucket> bucketList;

    public S3MultipleBucketService(AmazonS3 s3Client, long bucketLimit) {
        this.s3Client = s3Client;
        this.bucketLimit = bucketLimit;
        this.s3BucketService = new S3BucketService(s3Client);
        this.awsCliProcess = new AwsCliProcess();
        this.bucketList = getBucketList();
    }

    @Override
    public void uploadFile(MultipartFile multipartFile) {
        getFreeBucket()
                .findFirst()
                .ifPresent(bucket -> s3BucketService.uploadFile(bucket.getName(), multipartFile));
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
    public void deleteFile(String objectKey) {
        getFilteredBucket(objectKey)
                .findFirst()
                .ifPresent(bucket -> s3Client.deleteObject(bucket.getName(), objectKey));
    }

    private List<Bucket> getBucketList() {
        return s3Client.listBuckets();
    }

    private Stream<Bucket> getFreeBucket() {
        return bucketList.stream()
                .filter(bucket -> awsCliProcess.isBucketAvailable(bucket.getName(), bucketLimit));
    }

    private Stream<Bucket> getFilteredBucket(String objectKey) {
        return bucketList.stream()
                .filter(bucket -> s3Client.doesObjectExist(bucket.getName(), objectKey));
    }
}
