package com.gallery.layer.factory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3ConnectionFactory implements IS3ConnectionFactory {

    @Override
    public AmazonS3 getS3Connection(AWSCredentialsProvider credentialsProvider) {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
//                .withClientConfiguration(
//                        PredefinedClientConfigurations.defaultConfig()
//                                .withMaxConnections(8)
//                )
                .build();
    }

    @Override
    public AmazonS3 getS3Connection(AWSCredentialsProvider credentialsProvider, String region) {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(credentialsProvider)
//                .withClientConfiguration(
//                        PredefinedClientConfigurations.defaultConfig()
//                                .withMaxConnections(8)
//                )
                .build();
    }
}
