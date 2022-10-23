package com.gallery.layer.service;

import com.gallery.layer.config.S3Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;

public class S3BucketServiceTest {

    private static IS3BucketService s3BucketService;
    private static final String TEST_BUCKET = "test-bucket";
    private static final String FOLDER_PREFIX_PATH = "test/s3/";

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
        s3BucketService = new S3BucketService(s3Config.s3Client("", "", ""));
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
    public void deleteBucket() {
        s3BucketService.deleteBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesBucketExist(TEST_BUCKET));
    }
}
