package com.gallery.layer.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.layer.util.IConverter;
import com.gallery.layer.util.InStreamToByteConverter;
import com.gallery.layer.util.MultipartFileToFileConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.util.Date;

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
    public byte[] downloadFile(String bucket, String fileName) {
        S3Object s3Object = s3Client.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return inStreamToByteConverter.convert(s3ObjectInputStream);
    }

    @Override
    public void uploadFile(String bucketName, MultipartFile multipartFile) {
        File file = multipartFileToFileConverter.convert(multipartFile);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getName(), file));
        file.delete();
    }

    @Override
    public void deleteFile(String bucket, String objectKey) {
        s3Client.deleteObject(bucket, objectKey);
    }

    @Override
    public void addFileToFolder(String bucket, String prefix, String objectKey) {

    }

    @Override
    public void addFolder(String bucket, String folderName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, folderName, "");
        s3Client.putObject(putObjectRequest);
    }

    public S3ObjectSummary getObjectSummary(String bucketName, String objectKey) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(objectKey);
        ListObjectsV2Result listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request);
        S3ObjectSummary s3ObjectSummary = null;

        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
//        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
//        s3ObjectSummary.getLastModified();
        return s3ObjectSummary;
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


}
