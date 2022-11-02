package com.gallery.layer.service;

import com.gallery.layer.config.S3Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class S3MultipleBucketServiceTest extends BaseBucketServiceTest {
    private static IS3MultipleBucketService s3MultipleBucketService;

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
        s3MultipleBucketService = new S3MultipleBucketService(
                s3Config.getS3ClientStaticCredentials(ACCESS_KEY, SECRET_KEY, REGION), BUCKET_LIMIT);
    }
    @Test
    public void createBucketTest() {
        s3MultipleBucketService.createBucket(null);
    }
}
