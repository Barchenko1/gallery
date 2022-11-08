package com.gallery.layer.service;

import com.gallery.layer.modal.BucketCapacity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseBucketServiceTest {

    protected static final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY_ID");
    protected static final String SECRET_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
    protected static final String REGION_ENV = System.getenv("CLOUD_AWS_S3_BUCKET_REGION");
    protected static List<BucketCapacity> BUCKET_NAME_LIMIT_LIST = new ArrayList<>() {{
        add(new BucketCapacity.BucketCapacityBuilder()
                .bucketName("gallery-anime")
                .bucketCapacity(100L)
                .build());
    }};

    protected static String TEST_BUCKET = "test-single-bucket-lib";
    protected static String TEST_MULTIPLE_BUCKET = "test-multi-bucket-lib";
    protected String TEST_FOLDER_PATH = "testFolder";
    protected String TEST_FOLDER_PREFIX_PATH = "test";
    protected String TEST_ASYNC_FOLDER_PREFIX_PATH = "testAsync";
    protected String TEST_FILE_TXT = "testFile.txt";
    protected String TEST_ASYNC_FILE_TXT = "testFileAsync.txt";
    protected String TEST_OUTPUT_FILE_TXT = "testFileOutput.txt";
    protected String TEST_MSG = "test multi file upload lib";
    protected String TEST_FILE_CONTENT_TYPE = "text/plain";

    protected String getObjectKey() {
        return TEST_FOLDER_PREFIX_PATH + "/" + TEST_FILE_TXT;
    }

    protected String getObjectAsyncKey() {
        return TEST_ASYNC_FOLDER_PREFIX_PATH + "/" + TEST_ASYNC_FILE_TXT;
    }

    protected void waitNextTest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadFile(IS3BucketService s3BucketService,
                                String targetFilePath, String destinationFile) {
        try (OutputStream out = new FileOutputStream(destinationFile)) {
            out.write(s3BucketService.downloadFile(TEST_BUCKET, targetFilePath));
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    protected void loadFile(IS3MultipleBucketService s3MultipleBucketService,
                            String targetFilePath, String destinationFile) {
        try (OutputStream out = new FileOutputStream(destinationFile)) {
            out.write(s3MultipleBucketService.downloadFile(targetFilePath));
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    protected String readLoadFile(String fileName) {
        File file = new File(fileName);
        String outputMsg = "";
        try {
            outputMsg = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
        return outputMsg;
    }

    protected URI getFullFilePath(String filePath) {
        filePath = "/" + filePath;
        Optional<URL> resource = Optional.ofNullable(
                this.getClass().getResource(filePath));
        return resource
                .map(e -> {
                    try {
                        return e.toURI();
                    } catch (URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .orElseThrow(RuntimeException::new);
    }
}
