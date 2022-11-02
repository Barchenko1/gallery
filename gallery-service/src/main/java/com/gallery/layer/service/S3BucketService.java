package com.gallery.layer.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyPartRequest;
import com.amazonaws.services.s3.model.CopyPartResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
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

public class S3BucketService implements IS3BucketService {

    private final AmazonS3 s3Client;
    private final IConverter<MultipartFile, File> multipartFileToFileConverter =
            new MultipartFileToFileConverter();
    private final IConverter<S3ObjectInputStream, byte[]> inStreamToByteConverter =
            new InStreamToByteConverter();

    public S3BucketService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void createBucket(String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            s3Client.createBucket(createBucketRequest);
            String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
            System.out.println(bucketLocation);
        }
        waitWhileBucketCreate(bucketName);
    }

    @Override
    public void createBucket(String bucketName, String region) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest =
                    new CreateBucketRequest(bucketName, region);
            s3Client.createBucket(createBucketRequest);
        }
        waitWhileBucketCreate(bucketName);
    }

    @Override
    public void cleanUpBucket(String bucketName) {
        // Delete the sample objects.
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName)
                .withQuiet(false);

        // Verify that the objects were deleted successfully.
        DeleteObjectsResult deleteObjectsResult = s3Client.deleteObjects(multiObjectDeleteRequest);
        int successfulDeletes = deleteObjectsResult.getDeletedObjects().size();
        System.out.println(successfulDeletes + " objects successfully deleted.");
    }

    @Override
    public void deleteBucket(String bucketName) {
        if (s3Client.doesBucketExistV2(bucketName)) {
            DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
            s3Client.deleteBucket(deleteBucketRequest);
        }
    }

    @Override
    public List<Bucket> getBucketList() {
        return s3Client.listBuckets();
    }

    @Override
    public List<Bucket> getSelectedBucketList(List<String> bucketNameList) {
        return s3Client.listBuckets().stream()
                .filter(bucket -> bucketNameList.contains(bucket.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectKey) {
        return s3Client.doesObjectExist(bucketName, objectKey);
    }

    @Override
    public boolean doesFolderPathExist(String bucketName, String objectKey) {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, objectKey);
        return result.getKeyCount() > 0;
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        return s3Client.doesBucketExistV2(bucketName);
    }

    @Override
    public byte[] downloadFile(String bucket, String fileName) {
        S3Object s3Object = s3Client.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return inStreamToByteConverter.convert(s3ObjectInputStream);
    }

    @Override
    public void copyFolderAndRemove(String bucketName, String folderPath, String destinationPath) {
        try {
            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, destinationPath);
            InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(initRequest);

            // Get the object size to track the end of the copy operation.
            GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucketName, folderPath);
            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength();

            // Copy the object using 5 MB parts.
            long partSize = 5 * 1024 * 1024;
            long bytePosition = 0;
            int partNum = 1;
            List<CopyPartResult> copyResponses = new ArrayList<CopyPartResult>();
            while (bytePosition < objectSize) {
                // The last part might be smaller than partSize, so check to make sure
                // that lastByte isn't beyond the end of the object.
                long lastByte = Math.min(bytePosition + partSize - 1, objectSize - 1);

                // Copy this part.
                CopyPartRequest copyRequest = new CopyPartRequest()
                        .withSourceBucketName(bucketName)
                        .withSourceKey(folderPath)
                        .withDestinationBucketName(bucketName)
                        .withDestinationKey(destinationPath)
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
                    destinationPath,
                    initResult.getUploadId(),
                    getETags(copyResponses));
            s3Client.completeMultipartUpload(completeRequest);
            System.out.println("Multipart copy complete.");
    } catch (AmazonServiceException e) {
        // The call was transmitted successfully, but Amazon S3 couldn't process
        // it, so it returned an error response.
        e.printStackTrace();
    } catch (SdkClientException e) {
        // Amazon S3 couldn't be contacted for a response, or the client
        // couldn't parse the response from Amazon S3.
        e.printStackTrace();

    }
    }

    private static List<PartETag> getETags(List<CopyPartResult> responses) {
        List<PartETag> etags = new ArrayList<PartETag>();
        for (CopyPartResult response : responses) {
            etags.add(new PartETag(response.getPartNumber(), response.getETag()));
        }
        return etags;
    }

    @Override
    public void uploadFile(String bucketName, String folderPath, File file) {
        uploadFileToBucket(bucketName, folderPath, file);
    }

    @Override
    public void uploadFolderAsync(String bucket, String folderName) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        MultipleFileUpload multipleFileUpload = transferManager.uploadDirectory(bucket,
                "BuildNumber#1", new File(""), true);

    }

    @Override
    public void uploadFileAsync(String bucketName, String folderPath, File file) {
        uploadFileToBucketAsync(bucketName, folderPath, file);
    }

    @Override
    public void uploadMultipartFile(String bucketName, String folderPath, MultipartFile multipartFile) {
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucket(bucketName, folderPath, file);
        file.delete();
    }

    @Override
    public void uploadMultipartFileAsync(String bucketName, String folderPath, MultipartFile multipartFile) {
        File file = multipartFileToFileConverter.convert(multipartFile);
        uploadFileToBucketAsync(bucketName, folderPath, file);
        file.delete();
    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        s3Client.deleteObject(bucketName, objectKey);
    }

    @Override
    public void deleteFolder(String bucketName, String objectKey) {
        s3Client.deleteObject(bucketName, objectKey);
    }

    @Override
    public S3ObjectSummary getS3ObjectSummary(String bucketName, String objectKey) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(objectKey);
        ListObjectsV2Result listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request);
        S3ObjectSummary s3ObjectSummary = null;
        for (S3ObjectSummary objectSummary: listObjectsV2Result.getObjectSummaries()) {
            s3ObjectSummary = objectSummary;
            break;
        }

        return s3ObjectSummary;
    }

    @Override
    public S3Object getS3Object(String bucketName, String objectKey) {
        return s3Client.getObject(bucketName, objectKey);
    }

    @Override
    public String getFileUrl(String bucketName, String objectKey) {
        URL url;
        try {
            // Set the presigned URL to expire after one hour.
            Date expiration = getExpireDate(1000 * 60 * 60);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return url.toString();
    }

    private void uploadFileToBucket(String bucketName, String folderPath, File file) {
        s3Client.putObject(new PutObjectRequest(bucketName, folderPath + file.getName(), file));
    }

    private void uploadFileToBucketAsync(String bucketName, String folderPath, File file) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        // TransferManager processes all transfers asynchronously,
        // so this call returns immediately.
        Upload upload = transferManager.upload(bucketName, folderPath + file.getName(), file);
        System.out.println("Object upload started");

        // Optionally, wait for the upload to finish before continuing.
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitWhileBucketCreate(String bucketName) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 60 * 1000;
        while (!s3Client.doesBucketExistV2(bucketName) && endTime > System.currentTimeMillis()) {
            try {
//                LOG.info("Waiting for Amazon S3 to create bucket " + bucketName);
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
        if (!s3Client.doesBucketExistV2(bucketName)) {
            throw new IllegalStateException("Could not create bucket " + bucketName);
        }
    }
}
