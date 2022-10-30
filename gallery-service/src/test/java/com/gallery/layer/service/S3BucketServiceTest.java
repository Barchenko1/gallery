package com.gallery.layer.service;

import com.gallery.layer.config.S3Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class S3BucketServiceTest {

    private static IS3BucketService s3BucketService;
    private static final String TEST_BUCKET = "test-bucket";
    private static final String FOLDER_PREFIX_PATH = "test/s3/";

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
        s3BucketService = new S3BucketService(s3Config.getS3ClientEnvironmentVariableCredentials("us-east-1"));
    }

    @Test
    @Order(1)
    public void createBucket() {
        s3BucketService.createBucket(TEST_BUCKET);
        Assertions.assertTrue(s3BucketService.doesBucketExist(TEST_BUCKET));
    }

    @Test
    @Order(2)
    public void uploadFile() {
        File file = new File("");
        s3BucketService.uploadFileAsync(TEST_BUCKET, FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, FOLDER_PREFIX_PATH + file.getName()));
    }

    @Test
    @Order(3)
    public void downloadFile() {
        File file = null;
        try (OutputStream out = new FileOutputStream("")) {
            out.write(s3BucketService.downloadFile(TEST_BUCKET, FOLDER_PREFIX_PATH + ""));
        } catch (IOException ex) {

        }
    }

    @Test
    @Order(4)
    public void getS3Object() {
        s3BucketService.getS3Object(TEST_BUCKET, FOLDER_PREFIX_PATH + "");
    }

    @Test
    @Order(5)
    public void getFileUrl() {
        s3BucketService.getFileUrl(TEST_BUCKET, FOLDER_PREFIX_PATH + "");
    }

    @Test
    @Order(6)
    public void getS3ObjectSummary() {
        s3BucketService.getS3ObjectSummary(TEST_BUCKET, FOLDER_PREFIX_PATH + "");
    }

    @Test
    @Order(19)
    public void cleanUpBucketTest() {
        s3BucketService.cleanUpBucket(TEST_BUCKET);

    }

    @Test
    @Order(20)
    public void deleteBucket() {
        s3BucketService.deleteBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesBucketExist(TEST_BUCKET));
    }
}
