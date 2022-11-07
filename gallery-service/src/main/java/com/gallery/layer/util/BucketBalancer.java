package com.gallery.layer.util;

import com.gallery.layer.cli.AwsCliProcess;
import com.gallery.layer.modal.BucketData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BucketBalancer implements IBucketBalancer {

    private final AwsCliProcess awsCliProcess;
    private final Map<String, Long> bucketNameLimitMap;
    private Map<String, BucketData> bucketDataMap;

    public BucketBalancer(Map<String, Long> bucketNameLimitMap) {
        this.awsCliProcess = new AwsCliProcess();
        this.bucketNameLimitMap = bucketNameLimitMap;
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
        return bucketNameLimitMap.entrySet().stream()
                .map(entry -> {
                    double currentBucketSize =
                            awsCliProcess.getCurrentBucketSize(entry.getKey());
                    return new BucketData.BucketDataBuilder()
                            .bucketName(entry.getKey())
                            .currentBucketSize(currentBucketSize)
                            .currentFreeCapacity(
                                    calculateFreeCapacity(currentBucketSize, entry.getValue()))
                            .maxCapacity(entry.getValue())
                            .isAvailable(
                                    awsCliProcess.isBucketAvailable(
                                            entry.getKey(), entry.getValue()))
                            .build();
                })
                .collect(Collectors.toMap(BucketData::getBucketName, bucketData -> bucketData));
    }

    private double calculateFreeCapacity(double currentBucketSize, double bucketLimit) {
        return bucketLimit > currentBucketSize ? bucketLimit - currentBucketSize : 0;
    }
}
