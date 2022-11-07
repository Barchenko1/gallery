package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.factory.BucketDataFactory;
import com.gallery.layer.factory.IBucketDataFactory;
import com.gallery.layer.modal.BucketData;
import com.gallery.layer.modal.FileFolder;
import com.gallery.layer.util.BucketBalancer;
import com.gallery.layer.util.IBucketBalancer;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gallery.layer.util.S3BucketUtils.handleFolderPath;
import static com.gallery.layer.util.S3BucketUtils.replaceObjectKeyPath;

public class S3MultipleBucketService implements IS3MultipleBucketService {
    private final S3BucketService s3BucketService;
    private final IBucketBalancer bucketBalancer;
    private final IBucketDataFactory bucketDataFactory;
    private ConcurrentMap<String, BucketData> bucketDataMap;

    public S3MultipleBucketService(AmazonS3 s3Client, Map<String, Long> bucketNameLimitMap) {
        this.s3BucketService = new S3BucketService(s3Client);
        this.bucketBalancer = new BucketBalancer(bucketNameLimitMap);
        this.bucketDataMap = bucketBalancer.getBucketDataMap();
        this.bucketDataFactory = new BucketDataFactory(bucketBalancer);
    }

    @Override
    public void createBucket(String bucketName, long bucketLimit) {
        s3BucketService.createBucket(bucketName);
        addBucket(bucketName, bucketLimit);
    }

    @Override
    public void createBucket(String bucketName, long bucketLimit, String region) {
        s3BucketService.createBucket(bucketName, region);
        addBucket(bucketName, bucketLimit);
    }

    @Override
    public void createBucket(String bucketName, long bucketLimit,
                             PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        s3BucketService.createBucket(bucketName, publicAccessBlockConfiguration);
        addBucket(bucketName, bucketLimit);
    }

    @Override
    public void createBucket(String bucketName, long bucketLimit, String region,
                             PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        s3BucketService.createBucket(bucketName, region, publicAccessBlockConfiguration);
        addBucket(bucketName, bucketLimit);
    }

    @Override
    public void uploadFile(String folderPath, File file) {
        bucketDataMap.values().stream()
                .filter(value -> isBucketFree(bucketDataMap.get(value.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFile(bucketData.getBucketName(), folderPath, file);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadFileAsync(String folderPath, File file) {
        bucketDataMap.values().stream()
                .filter(value -> isBucketFree(bucketDataMap.get(value.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFileAsync(bucketData.getBucketName(), folderPath, file);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadMultipartFile(String folderPath, MultipartFile multipartFile) {
        bucketDataMap.values().stream()
                .filter(value -> isBucketFree(bucketDataMap.get(value.getBucketName()), multipartFile.getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadMultipartFile(bucketData.getBucketName(), folderPath, multipartFile);
                    reBalanceBuckets(bucketData.getBucketName(), multipartFile.getSize());
                });
    }

    @Override
    public void uploadMultipartFileAsync(String folderPath, MultipartFile multipartFile) {
        bucketDataMap.values().stream()
                .filter(value -> isBucketFree(bucketDataMap.get(value.getBucketName()), multipartFile.getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadMultipartFileAsync(bucketData.getBucketName(), folderPath, multipartFile);
                    reBalanceBuckets(bucketData.getBucketName(), multipartFile.getSize());
                });
    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {
        List<String> foundFolderBucketNames = bucketDataMap.keySet().stream()
                .filter(key -> s3BucketService.doesFolderPathExist(key, folderPath))
                .toList();

        List<FileFolder> fileFolderList = foundFolderBucketNames.stream()
                .map(key -> {
                    return new FileFolder.FileFolderBuilder()
                            .bucketName(key)
                            .objectKey(s3BucketService.getS3ObjectSummary(key, folderPath).getKey())
                            .fileSize(calculateFolderSize(key, folderPath))
                            .build();
                })
                .toList();


        //todo with bunch of files
        fileFolderList.forEach(fileFolder -> {
            bucketDataMap.values().stream()
                    .filter(bucketData -> isBucketFree(bucketData, fileFolder.getFileSize()))
                    .forEach(bucketData -> {
                        String destinationObjectKey = replaceObjectKeyPath(
                                fileFolder.getObjectKey(),
                                handleFolderPath(folderPath),
                                handleFolderPath(destinationPath));

                        s3BucketService.copyFile(bucketData.getBucketName(),
                                fileFolder.getObjectKey(),
                                getRandomFreeBucket(fileFolder.getFileSize()),
                                destinationObjectKey);
                    });
        });

        foundFolderBucketNames
                .forEach(bucketName -> s3BucketService.deleteFolder(bucketName, folderPath));

//        double totalFilesSize = fileFolderList.stream()
//                .map(FileFolder::getFileSize)
//                .reduce(Double::sum)
//                .orElse(0d);
//
//        String freeBucket = getFreeBucketName(totalFilesSize);
//
//        if (!"".equals(freeBucket)) {
//            s3BucketService.copyFolderAndRemove(
//                    fileFolder.getBucketName(), folderPath, freeBucket, destinationPath);
//        } else {
//
//        }

        getFilteredBuckets(folderPath)
                .findFirst()
                .ifPresent(bucketName ->
                        s3BucketService.copyFolderAndRemove(bucketName, folderPath, "", destinationPath));
    }

    @Override
    public void copyFolder(String folderPath, String destinationPath) {
        getFilteredBuckets(folderPath)
                .findFirst()
                .ifPresent(bucketName ->
                        s3BucketService.copyFolder(bucketName, folderPath, "", destinationPath));
    }

    @Override
    public String getFileUrl(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.getFileUrl(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("url with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public boolean doesObjectExist(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.doesObjectExist(bucketName, objectKey))
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean doesFolderPathExist(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.doesFolderPathExist(bucketName, objectKey))
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        return s3BucketService.doesBucketExist(bucketName);
    }

    @Override
    public byte[] downloadFile(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.downloadFile(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("file with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public void cleanUpBucket(String bucketName) {
        s3BucketService.cleanUpBucket(bucketName);
    }

    @Override
    public void deleteBucket(String bucketName) {
        s3BucketService.deleteBucket(bucketName);
    }

    @Override
    public void deleteFile(String objectKey) {
        getFilteredBuckets(objectKey)
                .findFirst()
                .ifPresent(bucketName -> s3BucketService.deleteFile(bucketName, objectKey));
    }

    @Override
    public void deleteFolder(String objectKey) {
        getFilteredBuckets(objectKey)
                .findFirst()
                .ifPresent(bucketName -> s3BucketService.deleteFolder(bucketName, objectKey));
    }

    @Override
    public S3ObjectSummary getS3ObjectSummary(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.getS3ObjectSummary(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("s3ObjectSummary with objectKey: %s wasn't found", objectKey)));
    }

    @Override
    public S3Object getS3Object(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.getS3Object(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("s3Object with objectKey: %s wasn't found", objectKey)));
    }

    private Stream<String> getFilteredBuckets(String objectKey) {
        return bucketDataMap.keySet().stream()
                .filter(bucketName -> isObjectKeyExist(bucketName, objectKey));
    }

    private boolean isObjectKeyExist(String bucketName, String objectKey) {
        return s3BucketService.doesObjectExist(bucketName, objectKey)
                || s3BucketService.doesFolderPathExist(bucketName, objectKey);
    }

    private String getRandomFreeBucket(long fileSize) {
        return bucketDataMap.entrySet().stream()
                .filter(entry -> isBucketFree(entry.getValue(), fileSize))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No free bucket"));
    }

    private boolean isBucketFree(BucketData bucketData, long fileSize) {
        return bucketData.isAvailable() && bucketData.getCurrentFreeCapacity() > fileSize;
    }

    private void addBucket(String bucketName, long bucketLimit) {
        if (s3BucketService.doesBucketExist(bucketName)) {
            bucketDataMap.computeIfAbsent(bucketName, (key) -> {
                BucketData bucketData = bucketDataFactory.getDefaultBucketData(key, bucketLimit);
                bucketBalancer.reBalanceBuckets(bucketData);
                return bucketData;
            });
        }
    }

    private void reBalanceBuckets(String bucketName, long fileCapacity) {
        BucketData bucketData = bucketDataFactory.getBucketData(bucketName, fileCapacity);
        bucketBalancer.reBalanceBuckets(bucketData);
        bucketDataMap = bucketBalancer.getBucketDataMap();
    }

    private long calculateFolderSize(String bucketName, String folderPath) {
        return s3BucketService.getS3ObjectSummaryList(bucketName, folderPath).stream()
                .mapToLong(S3ObjectSummary::getSize)
                .sum();
    }

}
