package com.gallery.layer.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.util.IConverter;
import com.gallery.layer.util.InStreamToByteConverter;
import com.gallery.layer.util.MultipartFileToFileConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static com.gallery.layer.util.S3BucketUtils.getExpireDate;

public class S3BucketService implements IS3BucketService {

    private final AmazonS3 s3Client;
    private final IConverter<MultipartFile, File> multipartFileToFileConverter =
            new MultipartFileToFileConverter();
    private final IConverter<S3ObjectInputStream, byte[]> inStreamToByteConverter =
            new InStreamToByteConverter();

    public S3BucketService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void createBucket(String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest =
                    new CreateBucketRequest(bucketName, s3Client.getRegion());
            s3Client.createBucket(createBucketRequest);
        }
        waitWhileBucketCreate(bucketName);
    }

    @Override
    public void createBucket(String bucketName, String region) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest =
                    new CreateBucketRequest(bucketName, region);
            s3Client.createBucket(createBucketRequest);
        }
        waitWhileBucketCreate(bucketName);
    }

    @Override
    public void deleteBucket(String bucketName) {
        if (s3Client.doesBucketExistV2(bucketName)) {
            DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
            s3Client.deleteBucket(deleteBucketRequest);
        }
    }

    @Override
    public List<Bucket> getBucketList() {
        return s3Client.listBuckets();
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectKey) {
        return s3Client.doesObjectExist(bucketName, objectKey);
    }

    @Override
    public byte[] downloadFile(String bucket, String fileName) {
        S3Object s3Object = s3Client.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return inStreamToByteConverter.convert(s3ObjectInputStream);
    }

    @Override
    public void copyFolderAndRemove(String bucketName, String folderPath, String destinationPath) {

    }

    @Override
    public void renameFolder(String bucketName, String folderPath, String newFolderPath) {

    }

    @Override
    public void uploadFile(String bucketName, MultipartFile multipartFile) {
        File file = multipartFileToFileConverter.convert(multipartFile);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getName(), file));
        file.delete();
    }

    @Override
    public void uploadEmptyFolder(String bucket, String folderName) {

    }

    @Override
    public void deleteFile(String bucket, String objectKey) {
        s3Client.deleteObject(bucket, objectKey);
    }

    @Override
    public void deleteFolder(String bucketName, String objectKey) {

    }

    @Override
    public void uploadFolder(String bucketName, String folderName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, "");
        s3Client.putObject(putObjectRequest);
    }

    @Override
    public void uploadFileToFolder(String bucket, String folder, String objectKey) {

    }

    @Override
    public S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(objectKey);
        ListObjectsV2Result listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request);
        S3ObjectSummary s3ObjectSummary = null;
        for (S3ObjectSummary objectSummary: listObjectsV2Result.getObjectSummaries()) {
            s3ObjectSummary = objectSummary;
            break;
        }

        return s3ObjectSummary;
    }

    @Override
    public S3Object getS3Object(String bucketName, String objectKey) {
        return s3Client.getObject(bucketName, objectKey);
    }

    @Override
    public String getFileUrl(String bucketName, String objectKey) {
        URL url;
        try {
            // Set the presigned URL to expire after one hour.
            Date expiration = getExpireDate(1000 * 60 * 60);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return url.toString();
    }

    private void waitWhileBucketCreate(String bucketName) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 60 * 1000;
        while (!s3Client.doesBucketExistV2(bucketName) && endTime > System.currentTimeMillis()) {
            try {
//                LOG.info("Waiting for Amazon S3 to create bucket " + bucketName);
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
        if (!s3Client.doesBucketExistV2(bucketName)) {
            throw new IllegalStateException("Could not create bucket " + bucketName);
        }
    }
}
