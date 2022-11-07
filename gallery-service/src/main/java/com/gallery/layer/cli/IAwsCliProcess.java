package com.gallery.layer.cli;

public interface IAwsCliProcess {
    boolean isBucketAvailable(String bucket, double limitBytes);

    double getCurrentBucketSize(String bucketName);
}
