package com.gallery.web.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.gallery.layer.modal.BucketCapacity;
import com.gallery.layer.service.IS3MultipleBucketService;
import com.gallery.layer.service.S3MultiBucketService;
import com.gallery.layer.util.IConverter;
import com.gallery.web.util.MegabyteToByteConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;


@Configuration
@PropertySource("classpath:application.yaml")
public class WebConfig {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;
    @Value("${CLOUD_AWS_S3_BUCKET_NAME}")
    private String bucketName;
    @Value("${CLOUD_AWS_S3_BUCKET_LIMIT_SIZE_MB}")
    private long bucketLimit;
    @Value("${CLOUD_AWS_S3_BUCKET_REGION}")
    private String region;

    @Bean
    public IS3MultipleBucketService s3MultipleBucketService() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return new S3MultiBucketService(credentialsProvider, region, getInitBucketNameLimitList());
    }

    private List<BucketCapacity> getInitBucketNameLimitList() {
        IConverter<Long, Long> megabyteToByteConverter = new MegabyteToByteConverter();

        return new ArrayList<>() {{
            add(new BucketCapacity.BucketCapacityBuilder()
                    .bucketName(bucketName)
                    .bucketCapacity(megabyteToByteConverter.convert(bucketLimit))
                    .build());
        }};
    }
}
