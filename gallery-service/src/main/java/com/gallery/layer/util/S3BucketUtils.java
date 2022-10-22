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
}
