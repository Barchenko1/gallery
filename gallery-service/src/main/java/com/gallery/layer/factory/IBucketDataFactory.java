package com.gallery.layer.factory;

import com.gallery.layer.modal.BucketData;

public interface IBucketDataFactory {
    BucketData getDefaultBucketData(String bucketName, long bucketLimit);
    BucketData getBucketData(String bucketName, long fileCapacity);
}
