package com.gallery.layer.cli;

import com.gallery.layer.cli.script.AwsCliScriptExecutor;
import com.gallery.layer.cli.script.IAwsCliScriptExecutor;

import static com.gallery.layer.constant.AwsCliCommand.S3_BUCKET_SIZE;

public class AwsCliProcess implements IAwsCliProcess {

    private final IAwsCliScriptExecutor awsCliCommandExecutor = new AwsCliScriptExecutor();

    @Override
    public boolean isBucketAvailable(String bucket, long limitBytes) {
        return getBucketSize(bucket) < limitBytes;
    }

    private double getBucketSize(String bucket) {
        String script = String.format(S3_BUCKET_SIZE.getCommand(), bucket, "admin");
        String result = awsCliCommandExecutor.execute(script);
        return "".equals(result) ? 0 : Double.parseDouble(result);
    }
}
