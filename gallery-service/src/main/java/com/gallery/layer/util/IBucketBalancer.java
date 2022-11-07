package com.gallery.layer.util;

import com.gallery.layer.modal.BucketData;

import java.util.concurrent.ConcurrentMap;

public interface IBucketBalancer {
    ConcurrentMap<String, BucketData> getBucketDataMap();
    void initBucketBalance();
    void reBalanceBuckets(BucketData bucketData);
}
