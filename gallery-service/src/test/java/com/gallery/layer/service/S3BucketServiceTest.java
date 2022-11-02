package com.gallery.layer.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.config.S3Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3BucketServiceTest extends BaseBucketServiceTest {

    private static IS3BucketService s3BucketService;

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
        s3BucketService = new S3BucketService(
                s3Config.getS3ClientStaticCredentials(ACCESS_KEY, SECRET_KEY, REGION));
    }

    @Test
    @Order(1)
    public void createBucketTest() {
        s3BucketService.createBucket(TEST_BUCKET);
        Assertions.assertTrue(s3BucketService.doesBucketExist(TEST_BUCKET));
        waitNextTest();
    }

    @Test
    @Order(2)
    public void uploadFileAsyncTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3BucketService.uploadFileAsync(TEST_BUCKET, FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(2)
    public void uploadFileTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3BucketService.uploadFile(TEST_BUCKET, FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(3)
    public void uploadFolderAsyncTest() {

    }

    @Test
    @Order(4)
    public void copyFolderAndRemoveTest() {

    }

    @Test
    @Order(5)
    public void downloadFileTest() {
        loadFile(s3BucketService, getObjectKey(), TEST_OUTPUT_FILE_TXT);
        String outputMsg = readLoadFile(TEST_OUTPUT_FILE_TXT);
        Assertions.assertEquals(TEST_MSG, outputMsg);
    }

    @Test
    @Order(6)
    public void getS3ObjectTest() {
        S3Object s3Object = s3BucketService.getS3Object(TEST_BUCKET, getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3Object.getKey());
        Assertions.assertEquals(TEST_FILE_CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());
    }

    @Test
    @Order(7)
    public void getFileUrlTest() {
        String url = s3BucketService.getFileUrl(TEST_BUCKET, getObjectKey());
        Assertions.assertTrue(url.contains(getObjectKey()));
    }

    @Test
    @Order(8)
    public void getS3ObjectSummaryTest() {
        S3ObjectSummary s3ObjectSummary = s3BucketService.getS3ObjectSummary(
                TEST_BUCKET, getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3ObjectSummary.getKey());
    }

    @Test
    @Order(9)
    public void deleteFileTest() {
        s3BucketService.deleteFile(TEST_BUCKET, getObjectKey());
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(9)
    public void deleteFolderTest() {
        s3BucketService.deleteFolder(TEST_BUCKET, FOLDER_PREFIX_PATH + "/");
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(10)
    public void cleanUpBucketTest() {
        s3BucketService.cleanUpBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(11)
    public void getBucketListTest() {
        List<Bucket> bucketList = s3BucketService.getBucketList();
        Assertions.assertEquals(TEST_BUCKET, bucketList.get(0).getName());
    }

    @Test
    @Order(12)
    public void deleteBucketTest() {
        s3BucketService.deleteBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesBucketExist(TEST_BUCKET));
    }

}
