package com.gallery.layer.cli;

import com.gallery.layer.cli.script.AwsCliScriptExecutor;
import com.gallery.layer.cli.script.IAwsCliScriptExecutor;

import static com.gallery.layer.constant.AwsCliCommand.S3_BUCKET_SIZE;

public class AwsCliProcess implements IAwsCliProcess {

    private final IAwsCliScriptExecutor awsCliCommandExecutor = new AwsCliScriptExecutor();

    @Override
    public boolean isBucketAvailable(String bucket, double limitBytes) {
        return getBucketSize(bucket) < limitBytes;
    }

    @Override
    public double getCurrentBucketSize(String bucketName) {
        return getBucketSize(bucketName);
    }

    private double getBucketSize(String bucketName) {
        String script = String.format(S3_BUCKET_SIZE.getCommand(), bucketName, "admin");
        String result = awsCliCommandExecutor.execute(script);
        return "".equals(result) ? 0 : Double.parseDouble(result);
    }
}
