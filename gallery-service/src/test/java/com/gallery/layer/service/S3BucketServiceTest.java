package com.gallery.layer.service;

import com.gallery.layer.config.S3Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class S3BucketServiceTest {

    private static IS3BucketService s3BucketService;

    @BeforeAll
    public static void setupTest() {
        S3Config s3Config = new S3Config();
//        s3BucketService = new S3BucketService(s3Config.s3Client());
    }

    @Test
    public void test() {

    }
}
