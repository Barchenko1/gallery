package com.gallery.layer.factory;

import com.gallery.layer.modal.BucketData;
import com.gallery.layer.util.IBucketBalancer;

public class BucketDataFactory implements IBucketDataFactory {

    private final IBucketBalancer bucketBalancer;

    public BucketDataFactory(IBucketBalancer bucketBalancer) {
        this.bucketBalancer = bucketBalancer;
    }

    @Override
    public BucketData getDefaultBucketData(String bucketName, long bucketLimit) {
        return new BucketData.BucketDataBuilder()
                .bucketName(bucketName)
                .currentBucketSize(0)
                .currentFreeCapacity(bucketLimit)
                .maxCapacity(bucketLimit)
                .isAvailable(true)
                .build();
    }

    @Override
    public BucketData getBucketData(String bucketName, long fileCapacity) {
        BucketData bucketData = bucketBalancer.getBucketDataMap().get(bucketName);
        double currentFreeCapacity = bucketData.getCurrentFreeCapacity() - fileCapacity;
        double currentBucketSize = bucketData.getCurrentBucketSize() + fileCapacity;
        boolean isAvailable = bucketData.getMaxCapacity() > currentBucketSize;
        return new BucketData.BucketDataBuilder()
                .bucketName(bucketData.getBucketName())
                .currentBucketSize(currentBucketSize)
                .currentFreeCapacity(currentFreeCapacity)
                .maxCapacity(bucketData.getMaxCapacity())
                .isAvailable(isAvailable)
                .build();
    }
}
