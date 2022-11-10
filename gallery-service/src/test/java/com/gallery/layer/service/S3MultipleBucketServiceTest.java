package com.gallery.layer.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3MultipleBucketServiceTest extends BaseBucketServiceTest {

    private static IS3MultipleBucketService s3MultipleBucketService;

    @BeforeAll
    public static void setupTest() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        s3MultipleBucketService = new S3MultiBucketService(credentialsProvider, REGION_ENV,
                BUCKET_NAME_LIMIT_LIST);
        s3MultipleBucketService.cleanUpBucket(TEST_MULTIPLE_BUCKET);
        s3MultipleBucketService.deleteBucket(TEST_MULTIPLE_BUCKET);
    }

    @Test
    @Order(1)
    public void createBucketPublicAccessTest() {
        PublicAccessBlockConfiguration publicAccessBlockConfiguration =
                new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                        .withIgnorePublicAcls(true)
                        .withBlockPublicPolicy(true)
                        .withRestrictPublicBuckets(true);
        s3MultipleBucketService.createBucket(TEST_MULTIPLE_BUCKET, 1000,
                publicAccessBlockConfiguration);
        waitNextTest();
        Assertions.assertTrue(s3MultipleBucketService.doesBucketExist(TEST_MULTIPLE_BUCKET));
    }

    @Test
    @Order(2)
    public void uploadFileTfmTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TRM_TXT)).toFile();
        s3MultipleBucketService.uploadFileTfm(TEST_MULTI_BUCKET_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(3)
    public void uploadFileTfmTagsTest() {
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFileWithTagsTest1"),
                new Tag("test2", "uploadFileWithTagsTest2"));
        File file = Paths.get(getFullFilePath(TEST_FILE_TRM_TXT)).toFile();
        s3MultipleBucketService.uploadFileTfm(TEST_MULTI_BUCKET_FOLDER_PREFIX_PATH, file, tagList);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectKeyForMultiFolder()));
        List<Tag> tagListResult = s3MultipleBucketService.getObjectTagging(getObjectKeyForMultiFolder());
        Assertions.assertEquals(tagList.size(), tagListResult.size());
    }

    @Test
    @Order(4)
    public void uploadFileTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3MultipleBucketService.uploadFile(TEST_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(5)
    public void uploadFileTagsTest() {
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFileWithTagsTest1"),
                new Tag("test2", "uploadFileWithTagsTest2"));
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3MultipleBucketService.uploadFile(TEST_FOLDER_PREFIX_PATH, file, tagList);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(getObjectKeyForMultiFolder()));
        List<Tag> tagListResult = s3MultipleBucketService.getObjectTagging(getObjectKeyForMultiFolder());
        Assertions.assertEquals(tagList.size(), tagListResult.size());
    }

    @Test
    @Order(6)
    public void uploadFolderTfmTest() {
        File file = Paths.get(getFullFilePath(TEST_FOLDER_PATH)).toFile();
        s3MultipleBucketService.uploadFolder(file.getName(), file);
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(TEST_FOLDER_PATH + "/testFolderFile1.txt"));
        Assertions.assertTrue(
                s3MultipleBucketService.doesObjectExist(TEST_FOLDER_PATH + "/testFolderFile2.txt"));
    }

    @Test
    @Order(7)
    public void uploadFolderWithTagsTest() {
        File file = Paths.get(getFullFilePath(TEST_FOLDER_PATH)).toFile();
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFolderWithTagsTest1"),
                new Tag("test2", "uploadFolderWithTagsTest2"));
        s3MultipleBucketService.uploadFolder(file.getName(), file, tagList);
        List<Tag> tagListResult1 = s3MultipleBucketService.getObjectTagging(
                TEST_FOLDER_PATH + "/testFolderFile1.txt");
        Assertions.assertEquals(tagList.size(), tagListResult1.size());
        List<Tag> tagListResult2 = s3MultipleBucketService.getObjectTagging(
                TEST_FOLDER_PATH + "/testFolderFile2.txt");
        Assertions.assertEquals(tagList.size(), tagListResult2.size());
    }

    @Test
    @Order(8)
    public void downloadFileTest() {
        loadFile(s3MultipleBucketService, getObjectKeyForMultiFolder(), TEST_OUTPUT_FILE_TXT);
        String outputMsg = readLoadFile(TEST_OUTPUT_FILE_TXT);
        Assertions.assertEquals(TEST_MULTI_MSG, outputMsg);
    }

    @Test
    @Order(9)
    public void downloadFolderTest() {
        s3MultipleBucketService.downloadFolder("test", "testOutputFolder");
        String outputMsg = readLoadFile("testOutputFolder/test/testFile.txt");
        Assertions.assertEquals(TEST_MSG, outputMsg);
    }

    @Test
    @Order(10)
    public void getS3ObjectTest() {
        S3Object s3Object = s3MultipleBucketService.getS3Object(getObjectKeyForMultiFolder());
        Assertions.assertEquals(getObjectKeyForMultiFolder(), s3Object.getKey());
        Assertions.assertEquals(TEST_FILE_CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());
    }

    @Test
    @Order(11)
    public void getFileUrlTest() {
        String url = s3MultipleBucketService.getFileUrl(getObjectKeyForMultiFolder());
        Assertions.assertTrue(url.contains(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(12)
    public void getS3ObjectSummaryTest() {
        S3ObjectSummary s3ObjectSummary =
                s3MultipleBucketService.getS3ObjectSummary(getObjectKeyForMultiFolder());
        Assertions.assertEquals(getObjectKeyForMultiFolder(), s3ObjectSummary.getKey());
    }

    @Test
    @Order(13)
    public void getS3ObjectSummaryListTest() {
        List<S3ObjectSummary> s3ObjectSummary =
                s3MultipleBucketService.getS3ObjectSummaryList(TEST_MULTI_BUCKET_FOLDER_PREFIX_PATH);
        Assertions.assertEquals(1, s3ObjectSummary.size());
    }

    @Test
    @Order(14)
    public void copyFileTest() {
        String destinationFolder = "copedOne";
        s3MultipleBucketService.copyFile(getObjectKeyForMultiFolder(),
                destinationFolder + "/" + TEST_FILE_TXT);
        Assertions.assertTrue(s3MultipleBucketService.doesObjectExist(
                destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(15)
    public void copyFileAndRemoveTest() {
        String destinationFolder = "copedOneAndRemove";
        s3MultipleBucketService.copyFileAndRemove(getObjectKeyForMultiFolder(),
                destinationFolder + "/" + TEST_FILE_TXT);
        Assertions.assertTrue(s3MultipleBucketService.doesObjectExist(
                destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(16)
    public void copyFolderTest() {
        String destinationFolder = "coped";
        s3MultipleBucketService.copyFolder(TEST_FOLDER_PREFIX_PATH, destinationFolder);
        Assertions.assertTrue(s3MultipleBucketService.doesObjectExist(
                 destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(17)
    public void copyFolderAndRemoveTest() {
        String destinationFolder = "copedAndRemove";
        s3MultipleBucketService.copyFolderAndRemove(TEST_FOLDER_PREFIX_PATH, destinationFolder);
        Assertions.assertTrue(s3MultipleBucketService.doesObjectExist(
                destinationFolder + "/" + TEST_FILE_TXT));
        Assertions.assertFalse(s3MultipleBucketService.doesObjectExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(18)
    public void deleteFileTest() {
        s3MultipleBucketService.deleteFile(getObjectKeyForMultiFolder());
        Assertions.assertFalse(s3MultipleBucketService.doesFolderPathExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(19)
    public void deleteFolderTest() {
        s3MultipleBucketService.deleteFolder(TEST_FOLDER_PREFIX_PATH);
        Assertions.assertFalse(
                s3MultipleBucketService.doesFolderPathExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(20)
    public void cleanUpBucketTest() {
        s3MultipleBucketService.cleanUpBucket(TEST_MULTIPLE_BUCKET);
        Assertions.assertFalse(
                s3MultipleBucketService.doesFolderPathExist(getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(21)
    public void deleteBucketTest() {
        s3MultipleBucketService.deleteBucket(TEST_MULTIPLE_BUCKET);
        Assertions.assertFalse(
                s3MultipleBucketService.doesBucketExist(TEST_MULTIPLE_BUCKET));
    }

    @Test
    @Order(22)
    public void createBucketRegionAccessTest() {
        String bucketName = "test-policy-region-lib-bucket";
        PublicAccessBlockConfiguration publicAccessBlockConfiguration =
                new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                        .withIgnorePublicAcls(true)
                        .withBlockPublicPolicy(true)
                        .withRestrictPublicBuckets(true);
        s3MultipleBucketService.createBucket(bucketName, 100L, "eu-central-1",
                publicAccessBlockConfiguration);
        waitNextTest();
        Assertions.assertTrue(s3MultipleBucketService.doesBucketExist(bucketName));
        s3MultipleBucketService.cleanUpBucket(bucketName);
        s3MultipleBucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3MultipleBucketService.doesBucketExist(bucketName));
    }

    @Test
    @Order(23)
    public void createBucketRegionTest() {
        String bucketName = "test--common-region-lib-bucket";
        s3MultipleBucketService.createBucket(bucketName, 100L, "eu-central-1");
        waitNextTest();
        Assertions.assertTrue(s3MultipleBucketService.doesBucketExist(bucketName));
        s3MultipleBucketService.cleanUpBucket(bucketName);
        s3MultipleBucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3MultipleBucketService.doesBucketExist(bucketName));
    }

    @Test
    @Order(24)
    public void createBucketTest() {
        String bucketName = "test-common-lib-bucket";
        waitNextTest();
        s3MultipleBucketService.createBucket(bucketName, 100L);
        waitNextTest();
        Assertions.assertTrue(s3MultipleBucketService.doesBucketExist(bucketName));
        s3MultipleBucketService.cleanUpBucket(bucketName);
        s3MultipleBucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3MultipleBucketService.doesBucketExist(bucketName));
    }
}
