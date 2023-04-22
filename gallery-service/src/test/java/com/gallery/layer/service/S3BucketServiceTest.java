package com.gallery.layer.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.Bucket;
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
public class S3BucketServiceTest extends BaseBucketServiceTest {

    private static IS3BucketService s3BucketService;

    @BeforeAll
    public static void setupTest() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        s3BucketService = new S3BucketService(credentialsProvider, REGION_ENV);
        s3BucketService.cleanUpBucket(TEST_BUCKET);
        s3BucketService.deleteBucket(TEST_BUCKET);
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
        s3BucketService.createBucket(TEST_BUCKET, publicAccessBlockConfiguration);
        waitNextTest();
        Assertions.assertTrue(s3BucketService.doesBucketExist(TEST_BUCKET));
    }

    @Test
    @Order(2)
    public void uploadFileTfmTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TRM_TXT)).toFile();
        s3BucketService.uploadFileTfm(TEST_BUCKET, TEST_MULTI_BUCKET_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(3)
    public void uploadFileTfmTagsTest() {
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFileWithTagsTest1"),
                new Tag("test2", "uploadFileWithTagsTest2"));
        File file = Paths.get(getFullFilePath(TEST_FILE_TRM_TXT)).toFile();
        s3BucketService.uploadFileTfm(TEST_BUCKET, TEST_MULTI_BUCKET_FOLDER_PREFIX_PATH, file, tagList);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKeyForMultiFolder()));
        List<Tag> tagListResult = s3BucketService.getObjectTagging(TEST_BUCKET, getObjectKeyForMultiFolder());
        Assertions.assertEquals(tagList.size(), tagListResult.size());
    }

    @Test
    @Order(4)
    public void uploadFileTest() {
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3BucketService.uploadFile(TEST_BUCKET, TEST_FOLDER_PREFIX_PATH, file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(5)
    public void uploadFileWithTagsTest() {
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFileWithTagsTest1"),
                new Tag("test2", "uploadFileWithTagsTest2"));
        File file = Paths.get(getFullFilePath(TEST_FILE_TXT)).toFile();
        s3BucketService.uploadFile(TEST_BUCKET, TEST_FOLDER_PREFIX_PATH, file, tagList);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, getObjectKey()));
        List<Tag> tagListResult1 = s3BucketService.getObjectTagging(TEST_BUCKET, getObjectKey());
        Assertions.assertEquals(tagList.size(), tagListResult1.size());
    }

    @Test
    @Order(6)
    public void uploadFolderTest() {
        File file = Paths.get(getFullFilePath(TEST_FOLDER_PATH)).toFile();
        s3BucketService.uploadFolder(TEST_BUCKET, file.getName(), file);
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, TEST_FOLDER_PATH + "/testFolderFile1.txt"));
        Assertions.assertTrue(
                s3BucketService.doesObjectExist(TEST_BUCKET, TEST_FOLDER_PATH + "/testFolderFile2.txt"));
    }

    @Test
    @Order(7)
    public void uploadFolderWithTagsTest() {
        File file = Paths.get(getFullFilePath(TEST_FOLDER_PATH)).toFile();
        List<Tag> tagList = Arrays.asList(new Tag("test1", "uploadFolderWithTagsTest1"),
                new Tag("test2", "uploadFolderWithTagsTest2"));
        s3BucketService.uploadFolder(TEST_BUCKET, file.getName(), file, tagList);
        List<Tag> tagListResult1 = s3BucketService.getObjectTagging(TEST_BUCKET,
                TEST_FOLDER_PATH + "/testFolderFile1.txt");
        Assertions.assertEquals(tagList.size(), tagListResult1.size());
        List<Tag> tagListResult2 = s3BucketService.getObjectTagging(TEST_BUCKET,
                TEST_FOLDER_PATH + "/testFolderFile2.txt");
        Assertions.assertEquals(tagList.size(), tagListResult2.size());
    }

    @Test
    @Order(8)
    public void downloadFileTest() {
        loadFile(s3BucketService, getObjectKey(), TEST_OUTPUT_FILE_TXT);
        String outputMsg = readLoadFile(TEST_OUTPUT_FILE_TXT);
        Assertions.assertEquals(TEST_MSG, outputMsg);
    }

    @Test
    @Order(9)
    public void downloadFolderTest() {
        s3BucketService.downloadFolder(TEST_BUCKET, "test", "testOutputFolder");
        String outputMsg = readLoadFile("testOutputFolder/test/testFile.txt");
        Assertions.assertEquals(TEST_MSG, outputMsg);
    }

    @Test
    @Order(10)
    public void getS3ObjectTest() {
        S3Object s3Object = s3BucketService.getS3Object(TEST_BUCKET, getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3Object.getKey());
        Assertions.assertEquals(TEST_FILE_CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());
    }

    @Test
    @Order(11)
    public void getFileUrlTest() {
        String url = s3BucketService.getFileUrl(TEST_BUCKET, getObjectKey());
        Assertions.assertTrue(url.contains(getObjectKey()));
    }

    @Test
    @Order(12)
    public void getS3ObjectSummaryTest() {
        S3ObjectSummary s3ObjectSummary = s3BucketService.getS3ObjectSummary(
                TEST_BUCKET, getObjectKey());
        Assertions.assertEquals(getObjectKey(), s3ObjectSummary.getKey());
    }

    @Test
    @Order(13)
    public void getS3ObjectSummaryListTest() {
        List<S3ObjectSummary> s3ObjectSummary = s3BucketService.getS3ObjectSummaryList(
                TEST_BUCKET, TEST_FOLDER_PREFIX_PATH);
        Assertions.assertEquals(1, s3ObjectSummary.size());
    }

    @Test
    @Order(14)
    public void getS3ObjectSummaryListLimitTest() {
        List<S3ObjectSummary> s3ObjectSummary = s3BucketService.getS3ObjectSummaryListWithLimit(
                TEST_BUCKET, TEST_FOLDER_PREFIX_PATH, 2);
        Assertions.assertEquals(1, s3ObjectSummary.size());
    }

    @Test
    @Order(15)
    public void copyFileTest() {
        String destinationFolder = "copedOne";
        s3BucketService.copyFile(TEST_BUCKET, getObjectKeyForMultiFolder(),
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT);
        Assertions.assertTrue(s3BucketService.doesObjectExist(
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(16)
    public void copyFileAndRemoveTest() {
        String destinationFolder = "copedOneAndRemove";
        s3BucketService.copyFileAndRemove(TEST_BUCKET, getObjectKeyForMultiFolder(),
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT);
        Assertions.assertTrue(s3BucketService.doesObjectExist(
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(17)
    public void copyFolderTest() {
        String destinationFolder = "coped";
        s3BucketService.copyFolder(TEST_BUCKET, TEST_FOLDER_PREFIX_PATH, TEST_BUCKET, destinationFolder);
        Assertions.assertTrue(s3BucketService.doesObjectExist(
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT));
    }

    @Test
    @Order(18)
    public void copyFolderAndRemoveTest() {
        String destinationFolder = "copedAndRemove";
        s3BucketService.copyFolderAndRemove(TEST_BUCKET, TEST_FOLDER_PREFIX_PATH, TEST_BUCKET, destinationFolder);
        Assertions.assertTrue(s3BucketService.doesObjectExist(
                TEST_BUCKET, destinationFolder + "/" + TEST_FILE_TXT));
        Assertions.assertFalse(s3BucketService.doesObjectExist(
                TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(19)
    public void deleteFileTest() {
        s3BucketService.deleteFile(TEST_BUCKET, getObjectKeyForMultiFolder());
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKeyForMultiFolder()));
    }

    @Test
    @Order(20)
    public void deleteFolderTest() {
        s3BucketService.deleteFolder(TEST_BUCKET, TEST_FOLDER_PREFIX_PATH);
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(21)
    public void cleanUpBucketTest() {
        s3BucketService.cleanUpBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesFolderPathExist(TEST_BUCKET, getObjectKey()));
    }

    @Test
    @Order(22)
    public void getBucketListTest() {
        List<Bucket> bucketList = s3BucketService.getBucketList();
        boolean testBucketName = bucketList.stream()
                        .anyMatch(bucketLimit -> TEST_BUCKET.equals(bucketLimit.getName()));
        Assertions.assertTrue(testBucketName);
    }

    @Test
    @Order(23)
    public void deleteBucketTest() {
        s3BucketService.deleteBucket(TEST_BUCKET);
        Assertions.assertFalse(s3BucketService.doesBucketExist(TEST_BUCKET));
    }

    @Test
    @Order(24)
    public void createBucketRegionAccessTest() {
        String bucketName = "test-policy-region-lib-bucket";
        PublicAccessBlockConfiguration publicAccessBlockConfiguration =
                new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                        .withIgnorePublicAcls(true)
                        .withBlockPublicPolicy(true)
                        .withRestrictPublicBuckets(true);
        s3BucketService.createBucket(bucketName, "eu-central-1",
                publicAccessBlockConfiguration);
        waitNextTest();
        Assertions.assertTrue(s3BucketService.doesBucketExist(bucketName));
        s3BucketService.cleanUpBucket(bucketName);
        s3BucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3BucketService.doesBucketExist(bucketName));
    }

    @Test
    @Order(25)
    public void createBucketRegionTest() {
        String bucketName = "test--common-region-lib-bucket";
        s3BucketService.createBucket(bucketName, "eu-central-1");
        waitNextTest();
        Assertions.assertTrue(s3BucketService.doesBucketExist(bucketName));
        s3BucketService.cleanUpBucket(bucketName);
        s3BucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3BucketService.doesBucketExist(bucketName));
    }

    @Test
    @Order(26)
    public void createBucketTest() {
        String bucketName = "test-common-lib-bucket";
        waitNextTest();
        s3BucketService.createBucket(bucketName);
        waitNextTest();
        Assertions.assertTrue(s3BucketService.doesBucketExist(bucketName));
        s3BucketService.cleanUpBucket(bucketName);
        s3BucketService.deleteBucket(bucketName);
        Assertions.assertFalse(s3BucketService.doesBucketExist(bucketName));
    }
}
