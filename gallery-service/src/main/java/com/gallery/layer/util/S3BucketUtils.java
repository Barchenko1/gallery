package com.gallery.layer.util;

import java.time.Instant;
import java.util.Date;

public class S3BucketUtils {
    public static Date getExpireDate(int duration) {
        Date expiration = new Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += duration;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    public static String replaceObjectKeyPath(String objectKey,
                                              String folderPath,
                                              String destinationPath) {
        return objectKey.replace(folderPath, destinationPath);
    }

    public static String handleFolderPath(String folderPath) {
        return folderPath.endsWith("/") ? folderPath : folderPath + "/";
    }
}
