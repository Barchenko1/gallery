package com.gallery.layer.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwsCliProcess {

    ProcessBuilder processBuilder = new ProcessBuilder();

    public boolean isBucketAvailable(String bucket, long limitBytes) {
        return getBucketSize(bucket) < limitBytes;
    }
    private double getBucketSize(String bucket) {
        String script = String.format(
                "aws s3 ls s3://%s --recursive --human-readable --summarize --profile admin", bucket);
        processBuilder.command("bash", "-c", script);
        Pattern pattern = Pattern.compile("   Total Size: " + "\\d" + " Bytes");
        String result = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result = line.replaceAll("[^0-9]", "");
                }
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return Double.parseDouble(result);
    }
}
