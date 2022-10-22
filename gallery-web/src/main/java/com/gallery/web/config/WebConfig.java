package com.gallery.web.config;

import com.amazonaws.services.s3.AmazonS3;
import com.gallery.layer.config.S3Config;
import com.gallery.layer.service.IS3MultipleBucketService;
import com.gallery.layer.service.S3MultipleBucketService;
import com.gallery.layer.util.IConverter;
import com.gallery.web.util.MegabyteToByteConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:application.yaml")
public class WebConfig {

    @Value("${cloud.aws.key.access}")
    private String accessKey;
    @Value("${cloud.aws.key.secret}")
    private String secretKey;
    @Value("${cloud.aws.region}")
    private String region;
    @Value("${cloud.aws.s3.bucket.limit.size.mb}")
    private int bucketLimit;

    @Bean
    public AmazonS3 s3Client() {
        S3Config s3Config = new S3Config();
        return s3Config.s3Client(accessKey, secretKey, region);
    }

    @Bean
    public IS3MultipleBucketService s3MultipleBucketService() {
        IConverter<Integer, Long> megabyteToByteConverter = new MegabyteToByteConverter();
        return new S3MultipleBucketService(s3Client(), megabyteToByteConverter.convert(bucketLimit));
    }
}
