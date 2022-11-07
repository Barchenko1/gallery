package com.gallery.layer.modal;

public class BucketData {
    private String bucketName;
    private double currentBucketSize;
    private double currentFreeCapacity;
    private double maxCapacity;
    private boolean isAvailable;

    public BucketData(BucketDataBuilder bucketDataBuilder) {
        this.bucketName = bucketDataBuilder.bucketName;
        this.currentBucketSize = bucketDataBuilder.currentBucketSize;
        this.currentFreeCapacity = bucketDataBuilder.currentFreeCapacity;
        this.maxCapacity = bucketDataBuilder.maxCapacity;
        this.isAvailable = bucketDataBuilder.isAvailable;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public double getCurrentBucketSize() {
        return currentBucketSize;
    }

    public void setCurrentBucketSize(double currentSize) {
        this.currentBucketSize = currentBucketSize;
    }

    public double getCurrentFreeCapacity() {
        return currentFreeCapacity;
    }

    public void setCurrentFreeCapacity(double currentFreeCapacity) {
        this.currentFreeCapacity = currentFreeCapacity;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public static class BucketDataBuilder {
        private String bucketName;
        private double currentBucketSize;
        private double currentFreeCapacity;
        private double maxCapacity;
        private boolean isAvailable;

        public BucketDataBuilder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public BucketDataBuilder currentBucketSize(double currentBucketSize) {
            this.currentBucketSize = currentBucketSize;
            return this;
        }

        public BucketDataBuilder currentFreeCapacity(double currentFreeCapacity) {
            this.currentFreeCapacity = currentFreeCapacity;
            return this;
        }

        public BucketDataBuilder maxCapacity(double maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public BucketDataBuilder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public BucketData build() {
            return new BucketData(this);
        }
    }
}
