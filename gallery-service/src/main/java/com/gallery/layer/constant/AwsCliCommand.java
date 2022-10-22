package com.gallery.layer.constant;

public enum AwsCliCommand {

    S3_BUCKET_SIZE("aws s3api list-objects --bucket %s --output json --profile %s --query \"sum(Contents[].Size)\""),
    S3_BUCKET_CONTENT_COUNT("aws s3api list-objects --bucket %s --output json --profile %s --query \"length(Contents[])\"");

    private final String command;
    AwsCliCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
