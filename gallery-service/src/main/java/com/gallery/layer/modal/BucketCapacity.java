package com.gallery.layer.modal;

public class BucketCapacity {
    private String bucketName;
    private long bucketCapacity;

    public BucketCapacity(BucketCapacityBuilder bucketCapacityBuilder) {
        this.bucketName = bucketCapacityBuilder.bucketName;
        this.bucketCapacity = bucketCapacityBuilder.bucketCapacity;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public long getBucketCapacity() {
        return bucketCapacity;
    }

    public void setBucketCapacity(long bucketCapacity) {
        this.bucketCapacity = bucketCapacity;
    }

    public static class BucketCapacityBuilder {
        private String bucketName;
        private long bucketCapacity;

        public BucketCapacity.BucketCapacityBuilder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public BucketCapacity.BucketCapacityBuilder bucketCapacity(long bucketCapacity) {
            this.bucketCapacity = bucketCapacity;
            return this;
        }

        public BucketCapacity build() {
            return new BucketCapacity(this);
        }
    }
}
