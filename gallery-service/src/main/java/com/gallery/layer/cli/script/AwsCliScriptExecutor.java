package com.gallery.layer.cli.script;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AwsCliScriptExecutor implements IAwsCliScriptExecutor {

    @Override
    public String execute(String script) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", script);
        String result = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result = line;
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return result;
    }
}
