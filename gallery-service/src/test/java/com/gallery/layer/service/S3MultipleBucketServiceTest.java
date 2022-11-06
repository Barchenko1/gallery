package com.gallery.layer.service;

import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3MultipleBucketServiceTest extends BaseBucketServiceTest {
    private static IS3MultipleBucketService s3MultipleBucketService;

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
        s3MultipleBucketService = new S3MultipleBucketService(
                s3Config.getS3ClientStaticCredentials(ACCESS_KEY, SECRET_KEY, REGION), BUCKET_LIMIT);
        s3MultipleBucketService.cleanUpBucket(TEST_MULTIPLE_BUCKET);
        s3MultipleBucketService.deleteBucket(TEST_MULTIPLE_BUCKET);
    }

    @Test
    @Order(1)
    public void createBucketTest() {
        PublicAccessBlockConfiguration publicAccessBlockConfiguration =
                new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                        .withIgnorePublicAcls(true)
                        .withBlockPublicPolicy(true)
                        .withRestrictPublicBuckets(true);
        s3MultipleBucketService.createBucket(TEST_MULTIPLE_BUCKET, publicAccessBlockConfiguration);
        Assertions.assertTrue(s3MultipleBucketService.doesBucketExist(TEST_MULTIPLE_BUCKET));
        waitNextTest();
    }

    @Test
    @Order(2)
    public void uploadFileAsyncTest() {
        File file = Paths.get(getFullFilePath(TEST_ASYNC_FILE_TXT)).toFile();
        s3MultipleBucketService.uploadFileAsync(TEST_ASYNC_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectAsyncKey()));
    }

    @Test
    @Order(3)
    public void uploadFileTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3MultipleBucketService.uploadFile(TEST_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectKey()));
    }

    @Test
    @Order(4)
    public void uploadFolderAsyncTest() {
        //todo
    }

    @Test
    @Order(5)
    public void downloadFileTest() {
        loadFile(s3MultipleBucketService, getObjectKey(), TEST_OUTPUT_FILE_TXT);
        String outputMsg = readLoadFile(TEST_OUTPUT_FILE_TXT);
        Assertions.assertEquals(TEST_MSG, outputMsg);
    }

    @Test
    @Order(6)
    public void getS3ObjectTest() {
        S3Object s3Object = s3MultipleBucketService.getS3Object(getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3Object.getKey());
        Assertions.assertEquals(TEST_FILE_CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());
    }

    @Test
    @Order(7)
    public void getFileUrlTest() {
        String url = s3MultipleBucketService.getFileUrl(getObjectKey());
        Assertions.assertTrue(url.contains(getObjectKey()));
    }

    @Test
    @Order(8)
    public void getS3ObjectSummaryTest() {
        S3ObjectSummary s3ObjectSummary = s3MultipleBucketService.getS3ObjectSummary(getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3ObjectSummary.getKey());
    }

    @Test
    @Order(9)
    public void copyFolderTest() {
        String destinationFolder = "coped";
        s3MultipleBucketService.copyFolder(TEST_FOLDER_PREFIX_PATH, destinationFolder);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(10)
    public void copyFolderAndRemoveTest() {
        String destinationFolder = "copedAndRemove";
        s3MultipleBucketService.copyFolderAndRemove(TEST_FOLDER_PREFIX_PATH, destinationFolder);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(destinationFolder + "/" + TEST_FILE_TXT));
        Assertions.assertFalse(s3MultipleBucketService.doesObjectExist(getObjectKey()));
    }

    @Test
    @Order(11)
    public void deleteFileTest() {
        s3MultipleBucketService.deleteFile(getObjectAsyncKey());
        Assertions.assertFalse(s3MultipleBucketService.doesFolderPathExist(getObjectAsyncKey()));
    }

    @Test
    @Order(12)
    public void deleteFolderTest() {
        s3MultipleBucketService.deleteFolder(TEST_FOLDER_PREFIX_PATH);
        Assertions.assertFalse(s3MultipleBucketService.doesFolderPathExist(getObjectKey()));
    }

    @Test
    @Order(13)
    public void cleanUpBucketTest() {
        s3MultipleBucketService.cleanUpBucket(TEST_MULTIPLE_BUCKET);
        Assertions.assertFalse(s3MultipleBucketService.doesFolderPathExist(getObjectKey()));
    }

    @Test
    @Order(16)
    public void deleteBucketTest() {
        s3MultipleBucketService.deleteBucket(TEST_MULTIPLE_BUCKET);
        Assertions.assertFalse(s3MultipleBucketService.doesBucketExist(TEST_MULTIPLE_BUCKET));
    }
}
