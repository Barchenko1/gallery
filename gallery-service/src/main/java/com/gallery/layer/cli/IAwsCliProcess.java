package com.gallery.layer.cli;

public interface IAwsCliProcess {
    boolean isBucketAvailable(String bucket, long limitBytes);
}
