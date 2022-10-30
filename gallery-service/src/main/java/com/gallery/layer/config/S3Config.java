package com.gallery.layer.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Config {

    public AmazonS3 getS3ClientStaticCredentials(String accessKey, String secretKey, String region) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public AmazonS3 getS3ClientProfileCredential(String profileName, String region) {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
    }

    public AmazonS3 getS3ClientEnvironmentVariableCredentials(String region) {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .build();
    }
}
