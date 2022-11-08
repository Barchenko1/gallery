package com.gallery.layer.connection;

import com.amazonaws.services.s3.AmazonS3;

public interface IS3BucketConnectionManager {
    AmazonS3 getS3Client();
    void connectionShutdown();
}
