package com.gallery.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.gallery",
        exclude = {
                ContextInstanceDataAutoConfiguration.class,
                ContextStackAutoConfiguration.class,
                ContextRegionProviderAutoConfiguration.class
        })
public class GalleryWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GalleryWebApplication.class, args);
    }

}
