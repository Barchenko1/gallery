package com.gallery.layer.modal;

public class FileFolder {
    private String bucketName;
    private String objectKey;
    private long fileSize;

    public FileFolder(FileFolderBuilder fileFolderBuilder) {
        this.bucketName = fileFolderBuilder.bucketName;
        this.objectKey = fileFolderBuilder.objectKey;
        this.fileSize = fileFolderBuilder.fileSize;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public static class FileFolderBuilder {
        private String bucketName;
        private String objectKey;
        private long fileSize;

        public FileFolder.FileFolderBuilder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public FileFolder.FileFolderBuilder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public FileFolder.FileFolderBuilder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public FileFolder build() {
            return new FileFolder(this);
        }
    }
}
