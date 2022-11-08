package com.gallery.layer.util;

import com.gallery.layer.cli.AwsCliProcess;
import com.gallery.layer.modal.BucketCapacity;
import com.gallery.layer.modal.BucketData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BucketBalancer implements IBucketBalancer {

    private final AwsCliProcess awsCliProcess;
    private final List<BucketCapacity> bucketCapacityList;
    private Map<String, BucketData> bucketDataMap;

    public BucketBalancer(List<BucketCapacity> bucketCapacityList) {
        this.awsCliProcess = new AwsCliProcess();
        this.bucketCapacityList = bucketCapacityList;
        initBucketBalance();
    }

    @Override
    public ConcurrentMap<String, BucketData> getBucketDataMap() {
        return new ConcurrentHashMap<>(bucketDataMap);
    }

    @Override
    public void initBucketBalance() {
        bucketDataMap = balanceBuckets();
    }

    @Override
    public void reBalanceBuckets(BucketData bucketData) {
        bucketDataMap.put(bucketData.getBucketName(), bucketData);
    }

    private Map<String, BucketData> balanceBuckets() {
        return bucketCapacityList.stream()
                .map(bucketCapacity -> {
                    double currentBucketSize =
                            awsCliProcess.getCurrentBucketSize(bucketCapacity.getBucketName());
                    return new BucketData.BucketDataBuilder()
                            .bucketName(bucketCapacity.getBucketName())
                            .currentBucketSize(currentBucketSize)
                            .currentFreeCapacity(
                                    calculateFreeCapacity(currentBucketSize, bucketCapacity.getBucketCapacity()))
                            .maxCapacity(bucketCapacity.getBucketCapacity())
                            .isAvailable(
                                    awsCliProcess.isBucketAvailable(
                                            bucketCapacity.getBucketName(), bucketCapacity.getBucketCapacity()))
                            .build();
                })
                .collect(Collectors.toMap(BucketData::getBucketName, bucketData -> bucketData));
    }

    private double calculateFreeCapacity(double currentBucketSize, double bucketLimit) {
        return bucketLimit > currentBucketSize ? bucketLimit - currentBucketSize : 0;
    }
}
