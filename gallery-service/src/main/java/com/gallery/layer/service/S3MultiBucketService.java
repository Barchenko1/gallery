package com.gallery.layer.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;
import com.gallery.layer.factory.BucketDataFactory;
import com.gallery.layer.factory.IBucketDataFactory;
import com.gallery.layer.modal.BucketCapacity;
import com.gallery.layer.modal.BucketData;
import com.gallery.layer.modal.FileFolder;
import com.gallery.layer.util.BucketBalancer;
import com.gallery.layer.util.IBucketBalancer;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gallery.layer.util.S3BucketUtils.handleFolderPath;
import static com.gallery.layer.util.S3BucketUtils.replaceObjectKeyPath;

public class S3MultiBucketService implements IS3MultipleBucketService {
    private final S3BucketService s3BucketService;
    private final IBucketBalancer bucketBalancer;
    private final IBucketDataFactory bucketDataFactory;
    private ConcurrentMap<String, BucketData> bucketDataMap;

    public S3MultiBucketService(AWSCredentialsProvider credentialsProvider, String region,
                                List<BucketCapacity> bucketCapacityList) {
        this.s3BucketService = new S3BucketService(credentialsProvider, region);
        this.bucketBalancer = new BucketBalancer(bucketCapacityList);
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
                .filter(bucketData -> isBucketFree(bucketDataMap.get(bucketData.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFile(bucketData.getBucketName(), folderPath, file);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadFileTfm(String folderPath, File file) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketDataMap.get(bucketData.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFileTfm(bucketData.getBucketName(), folderPath, file);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadFile(String folderPath, File file, List<Tag> tagList) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketDataMap.get(bucketData.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFile(bucketData.getBucketName(), folderPath, file, tagList);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadFileTfm(String folderPath, File file, List<Tag> tagList) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketDataMap.get(bucketData.getBucketName()), file.length()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFileTfm(bucketData.getBucketName(), folderPath, file, tagList);
                    reBalanceBuckets(bucketData.getBucketName(), file.length());
                });
    }

    @Override
    public void uploadFolder(String targetPrefix, File folder) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketData, getFileFolderSize(folder)))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFolder(bucketData.getBucketName(), targetPrefix, folder);
                    reBalanceBuckets(bucketData.getBucketName(), getFileFolderSize(folder));
                });
    }

    @Override
    public void uploadFolder(String targetPrefix, File folder, List<Tag> tagList) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketData, getFileFolderSize(folder)))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadFolder(bucketData.getBucketName(), targetPrefix, folder, tagList);
                    reBalanceBuckets(bucketData.getBucketName(), getFileFolderSize(folder));
                });
    }

    @Override
    public void uploadMultipartFile(String folderPath, MultipartFile multipartFile) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(
                        bucketDataMap.get(bucketData.getBucketName()), multipartFile.getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadMultipartFile(bucketData.getBucketName(), folderPath, multipartFile);
                    reBalanceBuckets(bucketData.getBucketName(), multipartFile.getSize());
                });
    }

    @Override
    public void uploadMultipartFileTfm(String folderPath, MultipartFile multipartFile) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketDataMap.get(bucketData.getBucketName()), multipartFile.getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.uploadMultipartFileTfm(bucketData.getBucketName(), folderPath, multipartFile);
                    reBalanceBuckets(bucketData.getBucketName(), multipartFile.getSize());
                });
    }

    @Override
    public void copyFileAndRemove(String objectKey, String destinationObjectKey) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketData, getS3ObjectSummary(objectKey).getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.copyFileAndRemove(getFoundFileBucketName(objectKey), objectKey,
                            bucketData.getBucketName(), destinationObjectKey);
                });
    }

    @Override
    public void copyFile(String objectKey, String destinationObjectKey) {
        bucketDataMap.values().stream()
                .filter(bucketData -> isBucketFree(bucketData, getS3ObjectSummary(objectKey).getSize()))
                .findFirst()
                .ifPresent(bucketData -> {
                    s3BucketService.copyFile(getFoundFileBucketName(objectKey), objectKey,
                            bucketData.getBucketName(), destinationObjectKey);
                });
    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {
        List<String> foundFolderBucketNames = getFoundFolderBucketNames(folderPath);
        copyS3BucketFolder(foundFolderBucketNames, folderPath, destinationPath);
        foundFolderBucketNames
                .forEach(bucketName -> s3BucketService.deleteFolder(bucketName, folderPath));
    }

    @Override
    public void copyFolder(String folderPath, String destinationPath) {
        copyS3BucketFolder(getFoundFolderBucketNames(folderPath), folderPath, destinationPath);
    }

    @Override
    public List<Tag> getObjectTagging(String objectKey) {
        return getFilteredBuckets(objectKey)
                .map(bucketName -> s3BucketService.getObjectTagging(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("objectKey: %s wasn't found", objectKey)));
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
    public void downloadFolder(String folderPath, String destinationPath) {
        getFilteredBuckets(folderPath)
                .findFirst()
                .ifPresent(bucketName ->
                        s3BucketService.downloadFolder(bucketName, folderPath, destinationPath));
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
    public List<Bucket> getBucketList() {
        return s3BucketService.getBucketList();
    }

    @Override
    public List<S3ObjectSummary> getS3ObjectSummaryList(String folderPath) {
        return getFilteredBuckets(folderPath)
                .map(bucketName -> s3BucketService.getS3ObjectSummaryList(bucketName, folderPath))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("folder with path: %s wasn't found", folderPath)
                ));
    }

    @Override
    public List<S3ObjectSummary> getS3ObjectSummaryListWithLimit(String folderPath, int limit) {
        return getFilteredBuckets(folderPath)
                .map(bucketName -> s3BucketService.getS3ObjectSummaryListWithLimit(bucketName, folderPath, limit))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("folder with path: %s wasn't found", folderPath)
                ));
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

    private void copyS3BucketFolder(List<String> foundFolderBucketNames, String folderPath,
                                    String destinationPath) {
        Queue<FileFolder> fileFolderQueue = foundFolderBucketNames.stream()
                .map(key -> {
                    return new FileFolder.FileFolderBuilder()
                            .bucketName(key)
                            .objectKey(s3BucketService.getS3ObjectSummary(key, folderPath).getKey())
                            .fileSize(calculateS3FolderSize(key, folderPath))
                            .build();
                })
                .collect(Collectors.toCollection(LinkedList::new));
        long sizeCounter = 0;
        List<FileFolder> subFileFolderList = new ArrayList<>();
        for (BucketData bucketData : bucketDataMap.values()) {
            boolean isFileBunchPrepared = false;
            while(isBucketFree(bucketData, sizeCounter)
                    && !fileFolderQueue.isEmpty()) {
                Optional<FileFolder> fileFolderOptional = Optional.ofNullable(fileFolderQueue.poll());
                if (fileFolderOptional.isPresent()) {
                    FileFolder fileFolder = fileFolderOptional.get();
                    subFileFolderList.add(fileFolder);
                    sizeCounter += fileFolder.getFileSize();
                    isFileBunchPrepared = true;
                }
            }
            if (isFileBunchPrepared) {
                subFileFolderList.forEach(fileFolder -> {
                    String destinationObjectKey = replaceObjectKeyPath(
                            fileFolder.getObjectKey(),
                            handleFolderPath(folderPath),
                            handleFolderPath(destinationPath));
                    s3BucketService.copyFile(fileFolder.getBucketName(),
                            fileFolder.getObjectKey(),
                            bucketData.getBucketName(),
                            destinationObjectKey);
                });
            }
        }
    }

    private List<String> getFoundFolderBucketNames(String folderPath) {
        return bucketDataMap.keySet().stream()
                .filter(bucketName -> s3BucketService.doesFolderPathExist(bucketName, folderPath))
                .toList();
    }

    private String getFoundFileBucketName(String objectKey) {
        return bucketDataMap.keySet().stream()
                .filter(bucketName -> s3BucketService.doesObjectExist(bucketName, objectKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("objectKey %s not found", objectKey)
                ));
    }

    private Stream<String> getFilteredBuckets(String objectKey) {
        return bucketDataMap.keySet().stream()
                .filter(bucketName -> isObjectKeyExist(bucketName, objectKey));
    }

    private boolean isObjectKeyExist(String bucketName, String objectKey) {
        return s3BucketService.doesObjectExist(bucketName, objectKey)
                || s3BucketService.doesFolderPathExist(bucketName, objectKey);
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
        bucketDataMap.put(bucketData.getBucketName(), bucketData);
    }

    private long calculateS3FolderSize(String bucketName, String folderPath) {
        return s3BucketService.getS3ObjectSummaryList(bucketName, folderPath).stream()
                .mapToLong(S3ObjectSummary::getSize)
                .sum();
    }

    private long getFileFolderSize(File folder) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .map(File::length)
                .reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException("Couldn't get folder size"));
    }

}
