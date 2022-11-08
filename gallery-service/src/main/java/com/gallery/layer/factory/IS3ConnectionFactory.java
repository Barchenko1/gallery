package com.gallery.layer.factory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Region;

public interface IS3ConnectionFactory {
    AmazonS3 getS3Connection(AWSCredentialsProvider credentialsProvider);
    AmazonS3 getS3Connection(AWSCredentialsProvider credentialsProvider, String region);
}
