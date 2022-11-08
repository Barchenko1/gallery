package com.gallery.layer.connection;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.gallery.layer.factory.IS3ConnectionFactory;
import com.gallery.layer.factory.S3ConnectionFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class S3BucketConnectionManager implements IS3BucketConnectionManager {

    private final IS3ConnectionFactory s3ConnectionFactory;
    private final AWSCredentialsProvider credentialsProvider;
    private String region;
    private AtomicBoolean isConnectionShutdown;
    private AmazonS3 s3Client;

    public S3BucketConnectionManager(AWSCredentialsProvider credentialsProvider) {
        this.s3ConnectionFactory = new S3ConnectionFactory();
        this.credentialsProvider = credentialsProvider;
        this.isConnectionShutdown = new AtomicBoolean(true);
    }

    public S3BucketConnectionManager(AWSCredentialsProvider credentialsProvider, String region) {
        this.s3ConnectionFactory = new S3ConnectionFactory();
        this.credentialsProvider = credentialsProvider;
        this.region = region;
        this.isConnectionShutdown = new AtomicBoolean(true);
    }

    @Override
    public AmazonS3 getS3Client() {
        if (region != null) {
            connectWithRegion();
        } else {
            connect();
        }
        return this.s3Client;
    }

    @Override
    public void connectionShutdown() {
        isConnectionShutdown.set(true);
    }

    private void connect() {
        if (isConnectionShutdown.get()) {
            this.s3Client = s3ConnectionFactory.getS3Connection(credentialsProvider);
            isConnectionShutdown.set(false);
        }
    }

    private void connectWithRegion() {
        if (isConnectionShutdown.get()) {
            this.s3Client = s3ConnectionFactory.getS3Connection(credentialsProvider, region);
            isConnectionShutdown.set(false);
        }
    }
}
