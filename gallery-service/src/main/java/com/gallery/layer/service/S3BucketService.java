package com.gallery.layer.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyPartRequest;
import com.amazonaws.services.s3.model.CopyPartResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetPublicAccessBlockRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.ObjectTaggingProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.gallery.layer.connection.IS3BucketConnectionManager;
import com.gallery.layer.connection.S3BucketConnectionManager;
import com.gallery.layer.util.IConverter;
import com.gallery.layer.util.InStreamToByteConverter;
import com.gallery.layer.util.MultipartFileToFileConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.gallery.layer.util.S3BucketUtils.getExpireDate;
import static com.gallery.layer.util.S3BucketUtils.handleFolderPath;
import static com.gallery.layer.util.S3BucketUtils.replaceObjectKeyPath;

public class S3BucketService implements IS3BucketService {

    private final IS3BucketConnectionManager s3BucketConnectionManager;
    private final IConverter<MultipartFile, File> multipartFileToFileConverter =
            new MultipartFileToFileConverter();
    private final IConverter<S3ObjectInputStream, byte[]> inStreamToByteConverter =
            new InStreamToByteConverter();

    public S3BucketService(AWSCredentialsProvider credentialsProvider) {
        this.s3BucketConnectionManager = new S3BucketConnectionManager(credentialsProvider);
    }

    public S3BucketService(AWSCredentialsProvider credentialsProvider, String region) {
        this.s3BucketConnectionManager = new S3BucketConnectionManager(credentialsProvider, region);
    }

    @Override
    public AmazonS3 getS3Client() {
        return s3BucketConnectionManager.getS3Client();
    }

    @Override
    public void createBucket(String bucketName) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        createS3Bucket(s3Client, bucketName);
    }

    @Override
    public void createBucket(String bucketName, PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        createS3Bucket(s3Client, bucketName);
        s3Client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                .withBucketName(bucketName)
                .withPublicAccessBlockConfiguration(publicAccessBlockConfiguration));
    }

    @Override
    public void createBucket(String bucketName, String region) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        createS3BucketWithRegion(s3Client, bucketName, region);
    }

    @Override
    public void createBucket(String bucketName, String region,
                             PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        createS3BucketWithRegion(s3Client, bucketName, region);
        s3Client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                .withBucketName(bucketName)
                .withPublicAccessBlockConfiguration(publicAccessBlockConfiguration));
    }

    @Override
    public void cleanUpBucket(String bucketName) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        if (s3Client.doesBucketExistV2(bucketName)){
            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
            if(!result.getObjectSummaries().isEmpty()) {
                result.getObjectSummaries()
                        .forEach(s3ObjectSummary ->
                                s3Client.deleteObject(bucketName, s3ObjectSummary.getKey()));
            }
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        if (s3Client.doesBucketExistV2(bucketName)) {
            DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
            s3Client.deleteBucket(deleteBucketRequest);
        }
    }

    @Override
    public List<Bucket> getBucketList() {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return s3Client.listBuckets();
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return s3Client.doesObjectExist(bucketName, objectKey);
    }

    @Override
    public boolean doesFolderPathExist(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, handleFolderPath(objectKey));
        return result.getKeyCount() > 0;
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return s3Client.doesBucketExistV2(bucketName);
    }

    @Override
    public byte[] downloadFile(String bucket, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        S3Object s3Object = s3Client.getObject(bucket, objectKey);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return inStreamToByteConverter.convert(s3ObjectInputStream);
    }

    @Override
    public void downloadFolder(String bucketName, String folderPath, String destinationPath) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        File folder = new File(destinationPath);
        MultipleFileDownload multipleFileDownload = transferManager.downloadDirectory(
                bucketName,
                handleFolderPath(folderPath),
                folder
        );

        try {
            multipleFileDownload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transferManager.shutdownNow();
            s3BucketConnectionManager.connectionShutdown();
        }
    }

    @Override
    public void copyFileAndRemove(String bucketName, String objectKey, String destinationBucketName, String destinationObjectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        copyS3BucketObject(s3Client, bucketName, objectKey, destinationBucketName, destinationObjectKey);
        deleteS3BucketFile(s3Client, bucketName, objectKey);
    }

    @Override
    public void copyFile(String bucketName, String objectKey, String destinationBucketName, String destinationObjectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        copyS3BucketObject(s3Client, bucketName, objectKey, destinationBucketName, destinationObjectKey);
    }

    @Override
    public void copyFolderAndRemove(String bucketName, String folderPath,
                                    String destinationBucketName, String destinationPath) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        copyS3BucketFolder(s3Client, bucketName, folderPath, destinationBucketName, destinationPath);
        deleteS3BucketFolder(s3Client, bucketName, folderPath);
    }

    @Override
    public void copyFolder(String bucketName, String folderPath,
                           String destinationBucketName, String destinationPath) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        copyS3BucketFolder(s3Client, bucketName, folderPath, destinationBucketName, destinationPath);
    }

    @Override
    public void uploadFile(String bucketName, String folderPath, File file) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFileToBucket(s3Client, bucketName, handleFolderPath(folderPath), file);
    }

    @Override
    public void uploadFileTfm(String bucketName, String folderPath, File file) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFileToBucketTfm(s3Client, bucketName, handleFolderPath(folderPath), file);
    }

    @Override
    public void uploadFile(String bucketName, String folderPath, File file, List<Tag> tagList) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFileToBucket(s3Client, bucketName, folderPath, file, tagList);
    }

    @Override
    public void uploadFileTfm(String bucketName, String folderPath, File file, List<Tag> tagList) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFileToBucketTfm(s3Client, bucketName, folderPath, file, tagList);
    }

    @Override
    public void uploadFolder(String bucketName, String targetPrefix, File folder) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFolderToS3BucketTfm(s3Client, bucketName, targetPrefix, folder);
    }

    @Override
    public void uploadFolder(String bucket, String targetPrefix, File folder, List<Tag> tagList) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        uploadFolderToS3BucketTfm(s3Client, bucket, targetPrefix, folder, tagList);
    }

    @Override
    public void uploadMultipartFile(String bucketName, String folderPath, MultipartFile multipartFile) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucket(s3Client, bucketName, handleFolderPath(folderPath), file);
        file.delete();
    }

    @Override
    public void uploadMultipartFileTfm(String bucketName, String folderPath, MultipartFile multipartFile) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucketTfm(s3Client, bucketName, handleFolderPath(folderPath), file);
        file.delete();
    }

    @Override
    public void uploadMultipartFile(String bucketName, String folderPath, MultipartFile multipartFile, List<Tag> tagList) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucket(s3Client, bucketName, handleFolderPath(folderPath), file, tagList);
        file.delete();
    }

    @Override
    public void uploadMultipartFileTfm(String bucketName, String folderPath, MultipartFile multipartFile, List<Tag> tagList) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucketTfm(s3Client, bucketName, handleFolderPath(folderPath), file, tagList);
        file.delete();
    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        deleteS3BucketFile(s3Client, bucketName, objectKey);
    }

    @Override
    public void deleteFolder(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        deleteS3BucketFolder(s3Client, bucketName, objectKey);
    }

    @Override
    public List<S3ObjectSummary> getS3ObjectSummaryList(String bucketName, String folderPath) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return getListObjectsV2Result(s3Client, bucketName, handleFolderPath(folderPath)).getObjectSummaries();
    }

    @Override
    public S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return getListObjectsV2Result(s3Client, bucketName, objectKey).getObjectSummaries().stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public S3Object getS3Object(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        return s3Client.getObject(bucketName, objectKey);
    }

    @Override
    public String getFileUrl(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        URL url;
        try {
            // Set the presigned URL to expire after one hour.
            Date expiration = getExpireDate(1000 * 60 * 60);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET);

            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return url.toString();
    }

    @Override
    public List<Tag> getObjectTagging(String bucketName, String objectKey) {
        AmazonS3 s3Client = s3BucketConnectionManager.getS3Client();
        GetObjectTaggingRequest getObjectTaggingRequest =
                new GetObjectTaggingRequest(bucketName, objectKey);

        return s3Client.getObjectTagging(getObjectTaggingRequest).getTagSet();
    }

    private void createS3Bucket(AmazonS3 s3Client, String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            s3Client.createBucket(createBucketRequest);
        }
    }

    private void createS3BucketWithRegion(AmazonS3 s3Client, String bucketName, String region) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, region);
            s3Client.createBucket(createBucketRequest);
        }
    }

    private void deleteS3BucketFile(AmazonS3 s3Client, String bucketName, String objectKey) {
        if (s3Client.doesObjectExist(bucketName, objectKey)) {
            s3Client.deleteObject(bucketName, objectKey);
        }
    }

    private void deleteS3BucketFolder(AmazonS3 s3Client, String bucketName, String objectKey) {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, objectKey);
        result.getObjectSummaries()
                .forEach(s3ObjectSummary -> s3Client.deleteObject(bucketName, s3ObjectSummary.getKey()));
    }

    private void uploadFileToBucket(AmazonS3 s3Client, String bucketName, String folderPath, File file) {
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, getObjectKeyFullPath(folderPath, file.getName()), file);
        s3Client.putObject(putObjectRequest);
    }

    private void uploadFileToBucket(AmazonS3 s3Client, String bucketName,
                                    String folderPath, File file, List<Tag> tagList) {
        ObjectTagging tagging = new ObjectTagging(tagList);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, getObjectKeyFullPath(folderPath, file.getName()), file)
                        .withTagging(tagging);
        s3Client.putObject(putObjectRequest);
    }

    private void uploadFileToBucketTfm(AmazonS3 s3Client, String bucketName, String folderPath, File file) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();
        // TransferManager processes all transfers asynchronously,
        // so this call returns immediately.
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, getObjectKeyFullPath(folderPath, file.getName()), file);

        Upload upload = transferManager.upload(putObjectRequest);
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transferManager.shutdownNow();
            s3BucketConnectionManager.connectionShutdown();
        }
    }

    private void uploadFileToBucketTfm(AmazonS3 s3Client, String bucketName, String folderPath,
                                       File file, List<Tag> tagList) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();
        ObjectTagging tagging = new ObjectTagging(tagList);
        // TransferManager processes all transfers asynchronously,
        // so this call returns immediately.
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, getObjectKeyFullPath(folderPath, file.getName()), file)
                        .withTagging(tagging);

        Upload upload = transferManager.upload(putObjectRequest);
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transferManager.shutdownNow();
            s3BucketConnectionManager.connectionShutdown();
        }
    }

    private void uploadFolderToS3BucketTfm(AmazonS3 s3Client, String bucketName,
                                           String targetPrefix, File folder) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        MultipleFileUpload multipleFileUpload =
                transferManager.uploadDirectory(bucketName, targetPrefix, folder, true);
        try {
            multipleFileUpload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transferManager.shutdownNow();
            s3BucketConnectionManager.connectionShutdown();
        }
    }

    private void uploadFolderToS3BucketTfm(AmazonS3 s3Client, String bucketName, String targetPrefix,
                                           File folder, List<Tag> tagList) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        ObjectMetadataProvider metaDataProvider = (file, meta) -> {
            meta.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        };
        ObjectTaggingProvider objectTagging = uploadContext -> {
            return new ObjectTagging(tagList);
        };

        MultipleFileUpload multipleFileUpload =
                transferManager.uploadDirectory(bucketName, targetPrefix, folder, true,
                        metaDataProvider, objectTagging);
        try {
            multipleFileUpload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transferManager.shutdownNow();
            s3BucketConnectionManager.connectionShutdown();
        }
    }

    private ListObjectsV2Result getListObjectsV2Result(AmazonS3 s3Client, String bucketName, String objectKey) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(objectKey);
        return s3Client.listObjectsV2(listObjectsV2Request);
    }

    private List<PartETag> getETags(List<CopyPartResult> responses) {
        return responses.stream()
                .map(copyPartResult -> new PartETag(copyPartResult.getPartNumber(), copyPartResult.getETag()))
                .collect(Collectors.toList());
    }

    private void copyS3BucketObject(AmazonS3 s3Client, String bucketName, String objectKey,
                                    String destinationBucketName, String destinationObjectKey) {
        try {
            List<CopyPartResult> copyResponses = new ArrayList<>();
            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest =
                    new InitiateMultipartUploadRequest(bucketName, destinationObjectKey);
            InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(initRequest);
            GetObjectMetadataRequest metadataRequest =
                    new GetObjectMetadataRequest(bucketName, objectKey);
            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength();
            // Copy the object using 5 MB parts.
            long partSize = 5 * 1024 * 1024;
            long bytePosition = 0;
            int partNum = 1;
            while (bytePosition < objectSize) {
                // The last part might be smaller than partSize, so check to make sure
                // that lastByte isn't beyond the end of the object.
                long lastByte = Math.min(bytePosition + partSize - 1, objectSize - 1);
                // Copy this part.
                CopyPartRequest copyRequest = new CopyPartRequest()
                        .withSourceBucketName(bucketName)
                        .withSourceKey(objectKey)
                        .withDestinationBucketName(destinationBucketName)
                        .withDestinationKey(destinationObjectKey)
                        .withUploadId(initResult.getUploadId())
                        .withFirstByte(bytePosition)
                        .withLastByte(lastByte)
                        .withPartNumber(partNum++);
                copyResponses.add(s3Client.copyPart(copyRequest));
                bytePosition += partSize;
            }
            // Complete the upload request to concatenate all uploaded parts and make the copied object available.
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                    bucketName,
                    destinationObjectKey,
                    initResult.getUploadId(),
                    getETags(copyResponses));
            s3Client.completeMultipartUpload(completeRequest);
        } catch (SdkClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyS3BucketFolder(AmazonS3 s3Client, String bucketName, String folderPath,
                                    String destinationBucketName, String destinationPath) {
        getListObjectsV2Result(s3Client, bucketName, handleFolderPath(folderPath)).getObjectSummaries()
                .forEach(s3ObjectSummary -> {
                    String destinationObjectKey = replaceObjectKeyPath(s3ObjectSummary.getKey(),
                            handleFolderPath(folderPath), handleFolderPath(destinationPath));
                    copyS3BucketObject(s3Client, bucketName, s3ObjectSummary.getKey(),
                            destinationBucketName, destinationObjectKey);
                });
    }

    private String getObjectKeyFullPath(String folderPath, String fileName) {
        return handleFolderPath(folderPath) + fileName;
    }

}
