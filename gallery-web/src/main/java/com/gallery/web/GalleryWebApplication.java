package com.gallery.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gallery")
public class GalleryWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GalleryWebApplication.class, args);
    }

}
